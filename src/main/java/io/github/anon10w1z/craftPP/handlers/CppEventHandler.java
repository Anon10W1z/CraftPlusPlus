package io.github.anon10w1z.craftPP.handlers;

import io.github.anon10w1z.craftPP.enchantments.CppEnchantmentBase;
import io.github.anon10w1z.craftPP.enchantments.EntityTickingEnchantment;
import io.github.anon10w1z.craftPP.main.CppModInfo;
import io.github.anon10w1z.craftPP.main.CraftPlusPlus;
import io.github.anon10w1z.craftPP.misc.CppExtendedEntityProperties;
import io.github.anon10w1z.craftPP.misc.CppUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

/**
 * The event handler for Craft++
 */
@SuppressWarnings({"unused", "unchecked"})
public final class CppEventHandler {
	public static CppEventHandler instance = new CppEventHandler();

	/**
	 * Whether or not to enable the potion effect overlay
	 */
	private static boolean displayPotionEffects = true;

	/**
	 * Affects living entity drops
	 *
	 * @param event The LivingDropsEvent
	 */
	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {
		Entity entity = event.entity;
		World world = entity.worldObj;
		//New Drops
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
			//Living entities drop their name tags
			String entityNameTag = entity.getCustomNameTag();
			if (!entityNameTag.equals("")) {
				ItemStack nameTag = new ItemStack(Items.name_tag);
				nameTag.setStackDisplayName(entityNameTag);
				entity.entityDropItem(nameTag, 0);
				entity.setCustomNameTag("");
			}
			//Bats drop leather
			if (entity instanceof EntityBat && CppConfigHandler.batLeatherDropChance > Math.random())
				entity.dropItem(Items.leather, 1);
				//Enderman drop the block they are carrying
			else if (entity instanceof EntityEnderman && CppConfigHandler.endermanBlockDropChance > Math.random()) {
				EntityEnderman enderman = (EntityEnderman) entity;
				IBlockState heldBlockState = enderman.func_175489_ck();
				Block heldBlock = heldBlockState.getBlock();
				enderman.entityDropItem(new ItemStack(heldBlock, 1, heldBlock.getMetaFromState(heldBlockState)), 0);
				enderman.func_175490_a(Blocks.air.getDefaultState());
			}
			//Creepers can rarely drop a TNT
			else if (entity instanceof EntityCreeper) {
				if (event.source.damageType != null && CppConfigHandler.creeperDropTntChance > Math.random()) {
					event.drops.clear();
					entity.dropItem(Item.getItemFromBlock(Blocks.tnt), 1);
				}
			}
		}
		//Drop removals
		List<EntityItem> drops = event.drops;
		List<EntityItem> dropsCopy = CppUtils.copyList(drops);
		for (EntityItem dropEntity : dropsCopy) {
			ItemStack dropItem = dropEntity.getEntityItem();
			if (event.source.getEntity() != null) {
				Item drop = dropItem.getItem();
				Entity source = event.source.getEntity();
				if (source instanceof EntityWolf && entity instanceof EntitySheep) {
					if (drop == Items.mutton || drop == Items.cooked_mutton)
						drops.remove(dropEntity);
				} else if (source instanceof EntityOcelot && entity instanceof EntityChicken) {
					if (drop == Items.chicken || drop == Items.cooked_chicken)
						drops.remove(dropEntity);
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
		if (player.getHeldItem() != null && event.action == Action.RIGHT_CLICK_AIR) {
			Item heldItem = player.getHeldItem().getItem();
			World world = event.world;
			if (heldItem == Items.ender_pearl && !world.isRemote && player.capabilities.isCreativeMode) {
				world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
				EntityEnderPearl enderPearl = new EntityEnderPearl(world, player);
				world.spawnEntityInWorld(enderPearl);
				player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(Items.ender_pearl)]);
			}
		}
	}

	/**
	 * Makes creepers and baby zombies burn in daylight. <br>
	 * Also gives functionality to all ticking enchantments.
	 *
	 * @param event The LivingUpdateEvent
	 */
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		EntityLivingBase livingEntity = event.entityLiving;
		if (!livingEntity.worldObj.isRemote) {
			World world = livingEntity.worldObj;
			if (((livingEntity instanceof EntityCreeper && CppConfigHandler.creeperBurnInDaylight) || (livingEntity instanceof EntityZombie && livingEntity.isChild() && CppConfigHandler.babyZombieBurnInDaylight)) && world.isDaytime()) {
				float f = livingEntity.getBrightness(1);
				Random random = world.rand;
				BlockPos blockpos = new BlockPos(livingEntity.posX, Math.round(livingEntity.posY), livingEntity.posZ);
				if (f > 0.5 && random.nextFloat() * 30 < (f - 0.4) * 2 && world.canSeeSky(blockpos)) {
					ItemStack itemstack = livingEntity.getEquipmentInSlot(4);
					boolean doSetFire = true;
					if (itemstack != null) {
						doSetFire = false;
						if (itemstack.isItemStackDamageable()) {
							itemstack.setItemDamage(itemstack.getItemDamage() + random.nextInt(2));
							if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
								livingEntity.renderBrokenItemStack(itemstack);
								livingEntity.setCurrentItemOrArmor(4, null);
							}
						}
					}
					if (doSetFire)
						livingEntity.setFire(8);
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
		if (event.modID.equals(CppModInfo.MOD_ID))
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
	 * Registers the extended item entity properties for Craft++
	 *
	 * @param event The EntityConstructing (event)
	 */
	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing event) {
		CppExtendedEntityProperties.registerExtendedProperties(event.entity); //verified to be item entity at registration time
	}

	/**
	 * Allows thrown seeds to plant themselves in farmland. <br>
	 * Also gives the Homing enchantment functionality.
	 *
	 * @param event The WorldTickEvent
	 */
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (CppConfigHandler.enableAutoSeedPlanting && !event.world.isRemote) {
			World world = event.world;
			List<EntityItem> entityItems = world.getEntities(EntityItem.class, IEntitySelector.selectAnything);
			for (EntityItem entityItem : entityItems)
				CppExtendedEntityProperties.getExtendedProperties(entityItem).handlePlantingLogic();
			for (Object entityObject : world.getEntities(Entity.class, IEntitySelector.selectAnything))
				CppEnchantmentBase.cppEnchantments.stream().filter(cppEnchantment -> cppEnchantment.getClass().isAnnotationPresent(EntityTickingEnchantment.class)).forEach(cppEnchantment -> cppEnchantment.performAction((Entity) entityObject, event));
		}
	}

	/**
	 * Syncs up arrows affected by homing to the client
	 *
	 * @param event The ClientTickEvent
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		Optional<CppEnchantmentBase> homingEnchantment = CppEnchantmentBase.getByName("homing");
		World world = Minecraft.getMinecraft().theWorld;
		if (homingEnchantment.isPresent() && world != null) {
			List<EntityArrow> arrows = world.getEntities(EntityArrow.class, IEntitySelector.selectAnything);
			for (EntityArrow arrow : arrows)
				homingEnchantment.get().performAction(arrow, null);
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
			if (stackTagCompound != null) {
				NBTTagCompound blockEntityTagCompound = stackTagCompound.getCompoundTag("BlockEntityTag");
				if (blockEntityTagCompound != null) {
					String entityName = blockEntityTagCompound.getString("EntityId");
					Class entityClass = (Class) EntityList.stringToClassMapping.get(entityName);
					if (entityClass != null) {
						EnumChatFormatting color = IMob.class.isAssignableFrom(entityClass) ? EnumChatFormatting.RED : EnumChatFormatting.BLUE;
						String unlocalizedEntityName = "entity." + entityName + ".name";
						String localizedEntityName = StatCollector.translateToLocal(unlocalizedEntityName);
						if (localizedEntityName.equals(unlocalizedEntityName))
							event.toolTip.add(color + entityName);
						else
							event.toolTip.add(color + localizedEntityName);
					}
				}
			}
		}
	}

	/**
	 * Enables mob spawners to drop themselves when harvested with silk touch
	 *
	 * @param event The (Block) BreakEvent
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

	/**
	 * Enables the Blazing enchantment functionality
	 *
	 * @param event The HarvestDropsEvent
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onHarvestDrops(HarvestDropsEvent event) {
		Optional<CppEnchantmentBase> blazingEnchantment = CppEnchantmentBase.getByName("blazing");
		if (blazingEnchantment.isPresent())
			blazingEnchantment.get().performAction(event.harvester, event);
	}

	/**
	 * Enables the Quickdraw enchantment functionality
	 *
	 * @param event The ArrowNockEvent
	 */
	@SubscribeEvent
	public void onArrowNock(ArrowNockEvent event) {
		Optional<CppEnchantmentBase> quickdrawEnchantment = CppEnchantmentBase.getByName("quickdraw");
		if (quickdrawEnchantment.isPresent())
			quickdrawEnchantment.get().performAction(event.entityPlayer, event);
	}

	/**
	 * Enables the Hops enchantment functionality
	 *
	 * @param event The LivingJumpEvent
	 */
	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		Optional<CppEnchantmentBase> hopsEnchantment = CppEnchantmentBase.getByName("hops");
		if (hopsEnchantment.isPresent())
			hopsEnchantment.get().performAction(event.entityLiving, event);
	}

	/**
	 * Draws potion effect icons in the top-left corner
	 *
	 * @param event The (Post) RenderGameOverlayEvent
	 */
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.CROSSHAIRS) {
			if (CraftPlusPlus.proxy.isPotionKeyPressed())
				displayPotionEffects = !displayPotionEffects; //toggle the potion effect overlay
			if (displayPotionEffects && !CraftPlusPlus.proxy.isGuiOpen())
				CraftPlusPlus.proxy.displayPotionEffects();
		}
	}

	/**
	 * Changes the mod options GUI into Craft++'s config GUI
	 *
	 * @param event The event to pass to the client proxy (check if it is a GuiOpenEvent there)
	 */
	@SubscribeEvent
	public void onEvent(Event event) {
		CraftPlusPlus.proxy.handleGuiOpen(event);
	}
}
