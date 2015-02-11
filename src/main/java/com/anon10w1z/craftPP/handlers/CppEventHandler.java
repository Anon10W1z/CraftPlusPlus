package com.anon10w1z.craftPP.handlers;

import com.anon10w1z.craftPP.main.CppReferences;
import com.anon10w1z.craftPP.main.CppUtils;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The event handler for Craft++
 */
@SuppressWarnings("unused")
public class CppEventHandler {
	/**
	 * Affects living entity drops.
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
			if (entity instanceof EntityBat && CppConfigHandler.enableBatLeatherDrop)
				entity.dropItem(Items.leather, 1);
				//Enderman drop the block they are carrying
			else if (entity instanceof EntityEnderman && CppConfigHandler.enableEndermanBlockDrop) {
				EntityEnderman enderman = (EntityEnderman) entity;
				IBlockState heldBlockState = enderman.func_175489_ck();
				Block heldBlock = heldBlockState.getBlock();
				int blockMeta = heldBlock.getMetaFromState(heldBlockState);
				enderman.entityDropItem(new ItemStack(heldBlock, 1, blockMeta), 0);
				enderman.func_175490_a(Blocks.air.getDefaultState());
			}
			//Creepers can rarely drop a TNT
			else if (entity instanceof EntityCreeper) {
				if (world.rand.nextInt(10) == 0 && CppConfigHandler.creeperDropTnt && event.source.damageType != null) {
					event.drops.clear();
					entity.dropItem(Item.getItemFromBlock(Blocks.tnt), 1);
				}
			}
		}
		//Drop removals
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
	 * Gives functionality for the creative mode ender pearl throwing.
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
	 * Makes creepers and baby zombies burn in daylight.
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
					boolean flag = true;
					ItemStack itemstack = entity.getEquipmentInSlot(4);
					if (itemstack != null) {
						if (itemstack.isItemStackDamageable()) {
							itemstack.setItemDamage(itemstack.getItemDamage() + random.nextInt(2));
							if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
								entity.renderBrokenItemStack(itemstack);
								entity.setCurrentItemOrArmor(4, null);
							}
						}
						flag = false;
					}
					if (flag)
						entity.setFire(8);
				}
			}
		}
	}

	/**
	 * Syncs the config file if it changes.
	 *
	 * @param event The OnConfigChangedEvent
	 */
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(CppReferences.MOD_ID))
			CppConfigHandler.syncConfig();
	}

	/**
	 * Allows a player to shear name tags off living entities.
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
	 * Allows thrown seeds to plant themselves in farmland.
	 *
	 * @param event The WorldTickEvent
	 */
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		World world = event.world;
		@SuppressWarnings("unchecked") List<EntityItem> entityItemList = world.getEntities(EntityItem.class, Predicates.notNull());
		for (EntityItem entityItem : CppUtils.getArray(entityItemList, EntityItem.class)) {
			ItemStack itemstack = entityItem.getEntityItem();
			if (itemstack.getItem() instanceof ItemSeeds) {
				BlockPos entityPos = new BlockPos(entityItem).down();
				BlockPos lastTickEntityPos = new BlockPos(entityItem.lastTickPosX, entityItem.lastTickPosY, entityItem.lastTickPosZ).down();
				DataWatcher dataWatcher = entityItem.getDataWatcher();
				dataWatcher.updateObject(30, dataWatcher.getWatchableObjectInt(30) + 1);
				if (entityPos.compareTo(lastTickEntityPos) != 0)
					dataWatcher.updateObject(30, 0);
				if (dataWatcher.getWatchableObjectInt(30) >= entityItem.getDataWatcher().getWatchableObjectInt(31))
					itemstack.onItemUse(CppUtils.getFakePlayer(world), world, entityPos, EnumFacing.UP, 0, 0, 0);
			}
		}
	}

	/**
	 * Adds some properties to item entities.
	 *
	 * @param event The EntityConstructing (event)
	 */
	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityItem) {
			Entity entity = event.entity;
			DataWatcher dataWatcher = entity.getDataWatcher();
			dataWatcher.addObject(30, 0); //the number of ticks this entity has been above the same BlockPos
			dataWatcher.addObject(31, entity.worldObj.rand.nextInt(51) + 50); //the number of ticks necessary to be above the same BlockPos
		}
	}
}
