package com.anon10w1z.craftPP.handlers;

import com.anon10w1z.craftPP.main.CppReferences;
import com.anon10w1z.craftPP.main.CppUtils;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * The event handler for Craft++
 */
@SuppressWarnings({"unused", "unchecked"})
public class CppEventHandler {
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
		World world = event.world;
		if (player.getHeldItem() != null && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
			Item heldItem = player.getHeldItem().getItem();
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
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
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
						if (itemstack.isItemStackDamageable()) {
							itemstack.setItemDamage(itemstack.getItemDamage() + random.nextInt(2));
							if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
								entity.renderBrokenItemStack(itemstack);
								entity.setCurrentItemOrArmor(4, null);
							}
						}
						doSetFire = false;
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
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
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
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		World world = event.world;
		List<EntityItem> entityItemList = world.getEntities(EntityItem.class, Predicates.alwaysTrue());
		if (CppConfigHandler.enableAutoSeedPlanting)
			for (EntityItem entityItem : entityItemList) {
				ItemStack itemstack = entityItem.getEntityItem();
				if (itemstack.getItem() instanceof ItemSeeds) {
					BlockPos entityPos = new BlockPos(entityItem).down();
					BlockPos lastTickEntityPos = new BlockPos(entityItem.lastTickPosX, entityItem.lastTickPosY, entityItem.lastTickPosZ).down();
					DataWatcher dataWatcher = entityItem.getDataWatcher();
					Map map = CppUtils.findObject(entityItem.getDataWatcher(), "watchedObjects", "field_75695_b");
					if (!map.containsKey(30))
						dataWatcher.addObject(30, 0);
					if (!map.containsKey(31))
						dataWatcher.addObject(31, world.rand.nextInt(51) + 50);

					dataWatcher.updateObject(30, dataWatcher.getWatchableObjectInt(30) + 1);
					if (entityPos.compareTo(lastTickEntityPos) != 0)
						dataWatcher.updateObject(30, 0);
					if (dataWatcher.getWatchableObjectInt(30) >= entityItem.getDataWatcher().getWatchableObjectInt(31))
						itemstack.onItemUse(CppUtils.getFakePlayer(world), world, entityPos, EnumFacing.UP, 0, 0, 0);
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
			String string = blockEntityTagCompound.getString("EntityId");
			event.toolTip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("entity." + string + ".name")); //uses localized entity name
		}
	}

	/**
	 * Enables mob spawners to drop themselves when harvested with silk touch
	 *
	 * @param event The (block) BreakEvent
	 */
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (!player.capabilities.isCreativeMode && event.state.getBlock() == Blocks.mob_spawner && EnchantmentHelper.getSilkTouchModifier(player)) {
			World world = event.world;
			BlockPos blockPos = event.pos;
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if (tileEntity instanceof TileEntityMobSpawner) { //just to be safe
				TileEntityMobSpawner spawnerTileEntity = (TileEntityMobSpawner) tileEntity;
				ItemStack spawner = new ItemStack(Blocks.mob_spawner);
				NBTTagCompound stackTagCompound = new NBTTagCompound();
				NBTTagCompound spawnerTagCompound = new NBTTagCompound();
				spawnerTileEntity.writeToNBT(spawnerTagCompound);
				stackTagCompound.setTag("BlockEntityTag", spawnerTagCompound);
				spawner.setTagCompound(stackTagCompound);
				EntityItem spawnerEntityItem = new EntityItem(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, spawner);
				spawnerEntityItem.setDefaultPickupDelay();
				world.spawnEntityInWorld(spawnerEntityItem);
				event.setExpToDrop(0); //prevents infinite XP loophole
			}
		}
	}

	/**
	 * Draws potion effect icons in the top-left corner
	 *
	 * @param event The Post (RenderGameOverLayEvent)
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && CppConfigHandler.enablePotionGui) {
			Minecraft minecraft = Minecraft.getMinecraft();
			int xPos = 2;
			int yPos = 2;

			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			minecraft.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
			//some constants for drawing textures
			final int POTION_ICON_SIZE = 18;
			final int POTION_ICON_SPACING = POTION_ICON_SIZE + 2;
			final int POTION_ICON_BASE_X_OFFSET = 0;
			final int POTION_ICON_BASE_Y_OFFSET = 198;
			final int POTION_ICONS_PER_ROW = 8;
			Collection<PotionEffect> potionEffects = minecraft.thePlayer.getActivePotionEffects();
			for (PotionEffect potionEffect : potionEffects) {
				Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
				if (potion.hasStatusIcon()) {
					int iconIndex = potion.getStatusIconIndex();
					new Gui().drawTexturedModalRect(xPos, yPos, POTION_ICON_BASE_X_OFFSET + iconIndex % POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_BASE_Y_OFFSET + iconIndex / POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_SIZE, POTION_ICON_SIZE);
					xPos += POTION_ICON_SPACING;
				}
			}
		}
	}
}
