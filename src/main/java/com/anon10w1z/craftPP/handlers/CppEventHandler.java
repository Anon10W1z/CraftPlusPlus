package com.anon10w1z.craftPP.handlers;

import com.anon10w1z.craftPP.main.CppReferences;
import com.anon10w1z.craftPP.main.CppUtils;
import com.anon10w1z.craftPP.main.CraftPlusPlus;
import com.anon10w1z.craftPP.misc.CppKeyBindings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

/**
 * The event handler for Craft++
 */
@SuppressWarnings({"unused", "unchecked"})
public class CppEventHandler {
	private static boolean enablePotionEffectGui = true;

	/**
	 * Affects living entity drops
	 *
	 * @param event The LivingDropsEvent
	 */
	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {
		Entity entity = event.entity;
		World world = entity.worldObj;
		// New Drops
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
			// Living entities drop their name tags
			String entityNameTag = entity.getCustomNameTag();
			if (!entityNameTag.equals("")) {
				ItemStack nameTag = new ItemStack(Items.name_tag);
				nameTag.setStackDisplayName(entityNameTag);
				entity.entityDropItem(nameTag, 0);
				entity.setCustomNameTag("");
			}
			// Bats drop leather
			if (entity instanceof EntityBat && CppConfigHandler.enableBatLeatherDrop)
				entity.dropItem(Items.leather, 1);
				// Enderman drop the block they are carrying
			else if (entity instanceof EntityEnderman && CppConfigHandler.enableEndermanBlockDrop) {
				EntityEnderman enderman = (EntityEnderman) entity;
				IBlockState heldBlockState = enderman.func_175489_ck();
				Block heldBlock = heldBlockState.getBlock();
				int blockMeta = heldBlock.getMetaFromState(heldBlockState);
				enderman.entityDropItem(new ItemStack(heldBlock, 1, blockMeta), 0);
				enderman.func_175490_a(Blocks.air.getDefaultState());
			}
			// Creepers can rarely drop a TNT
			else if (entity instanceof EntityCreeper) {
				if (world.rand.nextInt(10) == 0 && CppConfigHandler.creeperDropTnt && event.source.damageType != null) {
					event.drops.clear();
					entity.dropItem(Item.getItemFromBlock(Blocks.tnt), 1);
				}
			}
		}
		// Drop removals
		List<EntityItem> dropsCopy = new ArrayList<EntityItem>(event.drops);
		for (EntityItem dropEntity : dropsCopy) {
			ItemStack dropItem = dropEntity.getEntityItem();
			if (event.source.getEntity() != null) {
				Item drop = dropItem.getItem();
				Entity source = event.source.getEntity();
				if (source instanceof EntityWolf && entity instanceof EntitySheep) {
					if (drop == Items.mutton || drop == Items.cooked_mutton)
						event.drops.remove(dropEntity);
				} else if (source instanceof EntityOcelot && entity instanceof EntityChicken) {
					if (drop == Items.chicken || drop == Items.cooked_chicken)
						event.drops.remove(dropEntity);
				}
			}
		}
	}

	/**
	 * Gives functionality for the creative mode ender pearl throwing
	 *
	 * @param event The PlayerInteractEvent
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		EntityPlayer player = event.entityPlayer;
		if (player.getHeldItem() != null && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
			Item heldItem = player.getHeldItem().getItem();
			World world = event.world;
			if (heldItem == Items.ender_pearl && !world.isRemote && player.capabilities.isCreativeMode) {
				world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
				EntityEnderPearl enderpearl = new EntityEnderPearl(world, player);
				world.spawnEntityInWorld(enderpearl);
				player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(Items.ender_pearl)]);
			}
		}
	}

	/**
	 * Makes creepers and baby zombies burn in daylight
	 *
	 * @param event The LivingUpdateEvent
	 */
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if (!entity.worldObj.isRemote) {
			World world = entity.worldObj;
			Random random = world.rand;
			if (((entity instanceof EntityCreeper && CppConfigHandler.creeperBurnInDaylight) || (entity instanceof EntityZombie && entity.isChild() && CppConfigHandler.babyZombieBurnInDaylight)) && world.isDaytime()) {
				float f = entity.getBrightness(1);
				BlockPos blockpos = new BlockPos(entity.posX, Math.round(entity.posY), entity.posZ);
				if (f > 0.5 && random.nextFloat() * 30 < (f - 0.4) * 2 && world.canSeeSky(blockpos)) {
					boolean doSetFire = true;
					ItemStack itemstack = entity.getEquipmentInSlot(4);
					if (itemstack != null) {
						doSetFire = false;
						if (itemstack.isItemStackDamageable()) {
							itemstack.setItemDamage(itemstack.getItemDamage() + random.nextInt(2));
							if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
								entity.renderBrokenItemStack(itemstack);
								entity.setCurrentItemOrArmor(4, null);
							}
						}
					}
					if (doSetFire)
						entity.setFire(8);
				}
			}
		}
	}

	/**
	 * Syncs the config file if it changes
	 *
	 * @param event The OnConfigChangedEvent
	 */
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.modID.equals(CppReferences.MOD_ID))
			CppConfigHandler.syncConfig();
	}

	/**
	 * Allows a player to shear name tags off living entities
	 *
	 * @param event The EntityInteractEvent
	 */
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		if (event.entityPlayer.getHeldItem() != null) {
			EntityPlayer player = event.entityPlayer;
			ItemStack heldItem = player.getHeldItem();
			World world = player.worldObj;
			Entity target = event.target;
			if (heldItem.getItem() instanceof ItemShears && target instanceof EntityLivingBase && target.hasCustomName() && !world.isRemote) {
				target.playSound("mob.sheep.shear", 1, 1);
				ItemStack nameTag = new ItemStack(Items.name_tag).setStackDisplayName(target.getCustomNameTag());
				target.entityDropItem(nameTag, 0);
				target.setCustomNameTag("");
				heldItem.damageItem(1, player);
			}
		}
	}

	/**
	 * Allows thrown seeds to plant themselves in farmland
	 *
	 * @param event The WorldTickEvent
	 */
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (CppConfigHandler.enableAutoSeedPlanting) {
			World world = event.world;
			List<EntityItem> entityItemList = world.getEntities(EntityItem.class, IEntitySelector.selectAnything);
			for (EntityItem entityItem : entityItemList) {
				ItemStack itemstack = entityItem.getEntityItem();
				if (itemstack.getItem() instanceof ItemSeeds) {
					DataWatcher dataWatcher = entityItem.getDataWatcher();
					Map map = CppUtils.findObject(dataWatcher, "watchedObjects", "field_75695_b");
					if (!map.containsKey(30))
						dataWatcher.addObject(30, 0);
					if (!map.containsKey(31))
						dataWatcher.addObject(31, world.rand.nextInt(51) + 50);

					dataWatcher.updateObject(30, dataWatcher.getWatchableObjectInt(30) + 1);
					BlockPos entityPosDown = new BlockPos(entityItem).down();
					BlockPos lastTickEntityPosDown = new BlockPos(entityItem.lastTickPosX, entityItem.lastTickPosY, entityItem.lastTickPosZ).down();
					if (entityPosDown.compareTo(lastTickEntityPosDown) != 0)
						dataWatcher.updateObject(30, 0);
					if (dataWatcher.getWatchableObjectInt(30) >= entityItem.getDataWatcher().getWatchableObjectInt(31))
						itemstack.onItemUse(CppUtils.getFakePlayer(world), world, entityPosDown, EnumFacing.UP, 0, 0, 0);
				}
			}
		}
	}

	/**
	 * Adds tooltips for monster spawners
	 *
	 * @param event The ItemTooltipEvent
	 */
	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.itemStack;
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block != null && block == Blocks.mob_spawner) {
			NBTTagCompound stackTagCompound = stack.getTagCompound();
			NBTTagCompound blockEntityTagCompound = stackTagCompound.getCompoundTag("BlockEntityTag");
			String entityName = blockEntityTagCompound.getString("EntityId");
			if (!entityName.equals("")) {
				String unlocalizedEntityName = "entity." + entityName + ".name";
				String localizedEntityName = StatCollector.translateToLocal(unlocalizedEntityName);
				if (localizedEntityName.equals(unlocalizedEntityName))
					event.toolTip.add(EnumChatFormatting.BLUE + entityName);
				else
					event.toolTip.add(EnumChatFormatting.BLUE + localizedEntityName);
			}
		}
	}

	/**
	 * Enables mob spawners to drop themselves when harvested with silk touch
	 *
	 * @param event The (BlockEvent) BreakEvent
	 */
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (!player.capabilities.isCreativeMode && event.state.getBlock() == Blocks.mob_spawner && EnchantmentHelper.getSilkTouchModifier(player) && player.canHarvestBlock(Blocks.mob_spawner)) {
			World world = event.world;
			BlockPos blockPos = event.pos;
			TileEntityMobSpawner spawnerTileEntity = (TileEntityMobSpawner) world.getTileEntity(blockPos);
			NBTTagCompound spawnerTagCompound = new NBTTagCompound(); //tag for spawner (contained within tag for stack)
			spawnerTileEntity.getSpawnerBaseLogic().writeToNBT(spawnerTagCompound);
			NBTTagCompound stackTagCompound = new NBTTagCompound(); //tag for stack
			stackTagCompound.setTag("BlockEntityTag", spawnerTagCompound);
			ItemStack spawnerStack = new ItemStack(Blocks.mob_spawner);
			spawnerStack.setTagCompound(stackTagCompound);
			EntityItem spawnerEntityItem = new EntityItem(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, spawnerStack);
			spawnerEntityItem.setDefaultPickupDelay();
			world.spawnEntityInWorld(spawnerEntityItem);
			event.setExpToDrop(0); //prevents infinite XP loophole
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (CppKeyBindings.potionKey.isPressed()) {
			enablePotionEffectGui = !enablePotionEffectGui;
		}
	}

	/**
	 * Draws potion effect icons in the top-left corner
	 *
	 * @param event The (Post) RenderGameOverlayEvent
	 */
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && enablePotionEffectGui)
			CraftPlusPlus.proxy.displayPotionEffects();
	}
}
