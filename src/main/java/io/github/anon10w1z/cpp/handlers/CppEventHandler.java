package io.github.anon10w1z.cpp.handlers;

import io.github.anon10w1z.cpp.capabilities.CapabilitySelfPlanting;
import io.github.anon10w1z.cpp.capabilities.SelfPlantingProvider;
import io.github.anon10w1z.cpp.enchantments.CppEnchantmentBase;
import io.github.anon10w1z.cpp.enchantments.CppEnchantments;
import io.github.anon10w1z.cpp.enchantments.EntityTickingEnchantment;
import io.github.anon10w1z.cpp.items.CppItems;
import io.github.anon10w1z.cpp.main.CppModInfo;
import io.github.anon10w1z.cpp.main.CppUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * The event handler for Craft++
 */
@SuppressWarnings({"unused", "unchecked"})
public final class CppEventHandler {
	//There is no need to compile the pattern everytime we want to use it, compile it once and reuse it
	private static final Pattern pattern = Pattern.compile("(?i)" + '\u00a7' + "[0-9A-FK-OR]");
	/**
	 * The singleton instance of the event handler
	 */
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
		Entity entity = event.getEntity();
		World world = entity.worldObj;
		//New Drops
		if (!world.isRemote && world.getGameRules().getBoolean("doMobLoot")) {
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
			else if (entity instanceof EntityCreeper) {
				if (event.getSource().damageType != null && CppConfigHandler.creeperDropTntChance > Math.random()) {
					event.getDrops().clear();
					entity.dropItem(Item.getItemFromBlock(Blocks.tnt), 1);
				}
			}
		}
		//Drop removals
		List<EntityItem> drops = event.getDrops();
		List<EntityItem> dropsCopy = CppUtils.copyList(drops);
		for (EntityItem dropEntity : dropsCopy) {
			ItemStack dropItem = dropEntity.getEntityItem();
			if (event.getSource().getEntity() != null) {
				Item drop = dropItem.getItem();
				Entity source = event.getSource().getEntity();
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
	 * Strips the input text of its formatting codes
	 *
	 * @param text The text
	 * @return The text without its formatting codes
	 */
	private String getTextWithoutFormattingCodes(String text) {
		return pattern.matcher(text).replaceAll("");
	}

	/**
	 * Makes creepers and baby zombies burn in daylight. <br>
	 * Also gives functionality to all ticking enchantments.
	 *
	 * @param event The LivingUpdateEvent
	 */
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		EntityLivingBase livingEntity = event.getEntityLiving();
		if (!livingEntity.worldObj.isRemote) {
			World world = livingEntity.worldObj;
			if (((livingEntity instanceof EntityCreeper && CppConfigHandler.creeperBurnInDaylight) || (livingEntity instanceof EntityZombie && livingEntity.isChild() && CppConfigHandler.babyZombieBurnInDaylight)) && world.isDaytime()) {
				float f = livingEntity.getBrightness(1);
				Random random = world.rand;
				BlockPos blockPos = new BlockPos(livingEntity.posX, Math.round(livingEntity.posY), livingEntity.posZ);
				if (f > 0.5 && random.nextFloat() * 30 < (f - 0.4) * 2 && world.canSeeSky(blockPos)) {
					ItemStack itemstack = livingEntity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
					boolean doSetFire = true;
					if (itemstack != null) {
						doSetFire = false;
						if (itemstack.isItemStackDamageable()) {
							itemstack.setItemDamage(itemstack.getItemDamage() + random.nextInt(2));
							if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
								livingEntity.renderBrokenItemStack(itemstack);
								livingEntity.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
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
	 * Allows a player to shear name tags off living entities
	 *
	 * @param event The EntityInteractEvent
	 */
	@SubscribeEvent
	public void entityRightclick(PlayerInteractEvent.EntityInteract event) {
		if (event.getEntityPlayer().getHeldItemMainhand() != null) {
			EntityPlayer player = event.getEntityPlayer();
			ItemStack heldItem = player.getHeldItemMainhand();
			World world = player.worldObj;
			Entity target = event.getTarget();
			if (heldItem.getItem() instanceof ItemShears && target instanceof EntityLivingBase && target.hasCustomName() && !world.isRemote) {
				target.playSound(SoundEvents.entity_sheep_shear, 1, 1);
				ItemStack nameTag = new ItemStack(Items.name_tag).setStackDisplayName(target.getCustomNameTag());
				target.entityDropItem(nameTag, 0);
				target.setCustomNameTag("");
				heldItem.damageItem(1, player);
			}
		}
		if (event.getEntityPlayer().getHeldItemOffhand() != null) {
			EntityPlayer player = event.getEntityPlayer();
			ItemStack heldItem = player.getHeldItemOffhand();
			World world = player.worldObj;
			Entity target = event.getTarget();
			if (heldItem.getItem() instanceof ItemShears && target instanceof EntityLivingBase && target.hasCustomName() && !world.isRemote) {
				target.playSound(SoundEvents.entity_sheep_shear, 1, 1);
				ItemStack nameTag = new ItemStack(Items.name_tag).setStackDisplayName(target.getCustomNameTag());
				target.entityDropItem(nameTag, 0);
				target.setCustomNameTag("");
				heldItem.damageItem(1, player);
			}
		}
	}

	/**
	 * Allows thrown seeds to plant themselves in farmland, and gives the Homing enchantment functionality
	 *
	 * @param event The WorldTickEvent
	 */
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (CppConfigHandler.autoSeedPlanting && !event.world.isRemote) {
			World world = event.world;
			List<EntityItem> entityItems = world.getEntities(EntityItem.class, EntitySelectors.IS_ALIVE);
			for (EntityItem entityItem : entityItems)
				if (entityItem.hasCapability(CapabilitySelfPlanting.CAPABILITY_SELF_PLANTING, null))
					entityItem.getCapability(CapabilitySelfPlanting.CAPABILITY_SELF_PLANTING, null).handlePlantingLogic(entityItem);
			for (Object entityObject : world.getEntities(Entity.class, EntitySelectors.IS_ALIVE))
				CppEnchantmentBase.cppEnchantments.stream().filter(cppEnchantment -> cppEnchantment.getClass().isAnnotationPresent(EntityTickingEnchantment.class)).forEach(cppEnchantment -> cppEnchantment.performAction((Entity) entityObject, null));
		}
	}

	/**
	 * Adds tooltips for monster spawners
	 *
	 * @param event The ItemTooltipEvent
	 */
	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block != null && block == Blocks.mob_spawner) {
			NBTTagCompound stackTagCompound = stack.getTagCompound();
			if (stackTagCompound != null) {
				NBTTagCompound blockEntityTagCompound = stackTagCompound.getCompoundTag("BlockEntityTag");
				if (blockEntityTagCompound != null) {
					String entityName = blockEntityTagCompound.getString("EntityId");
					Class entityClass = (Class) EntityList.stringToClassMapping.get(entityName);
					if (entityClass != null) {
						TextFormatting color = IMob.class.isAssignableFrom(entityClass) ? TextFormatting.RED : TextFormatting.BLUE;
						String unlocalizedEntityName = "entity." + entityName + ".name";
						String localizedEntityName = I18n.format(unlocalizedEntityName);
						if (localizedEntityName.equals(unlocalizedEntityName))
							event.getToolTip().add(color + entityName);
						else
							event.getToolTip().add(color + localizedEntityName);
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
		if (CppConfigHandler.mobSpawnerSilkTouchDrop && !player.capabilities.isCreativeMode && event.getState().getBlock() == Blocks.mob_spawner && EnchantmentHelper.getEnchantmentLevel(Enchantments.silkTouch, player.getHeldItemMainhand()) != 0 && player.canHarvestBlock(Blocks.mob_spawner.getDefaultState())) {
			World world = event.getWorld();
			BlockPos blockPos = event.getPos();
			TileEntityMobSpawner spawnerTileEntity = (TileEntityMobSpawner) world.getTileEntity(blockPos);
			NBTTagCompound spawnerTagCompound = new NBTTagCompound();
			spawnerTileEntity.getSpawnerBaseLogic().writeToNBT(spawnerTagCompound);
			NBTTagCompound stackTagCompound = new NBTTagCompound();
			stackTagCompound.setTag("BlockEntityTag", spawnerTagCompound);
			ItemStack spawnerStack = new ItemStack(Blocks.mob_spawner);
			spawnerStack.setTagCompound(stackTagCompound);
			EntityItem spawnerEntityItem = new EntityItem(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, spawnerStack);
			spawnerEntityItem.setDefaultPickupDelay();
			world.spawnEntityInWorld(spawnerEntityItem);
			event.setExpToDrop(0);
		}
	}

	/**
	 * Enables the Blazing enchantment functionality
	 *
	 * @param event The HarvestDropsEvent
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onHarvestDrops(HarvestDropsEvent event) {
		EntityPlayer harvester = event.getHarvester();
		CppEnchantments.performAction("blazing", harvester, event);
		CppEnchantments.performAction("siphon", harvester, event);
	}

	/**
	 * Enables the Quickdraw enchantment functionality
	 *
	 * @param event The ArrowNockEvent
	 */
	@SubscribeEvent
	public void onArrowNock(ArrowNockEvent event) {
		CppEnchantments.performAction("quickdraw", event.getEntityPlayer(), event);
	}

	/**
	 * Enables the Hops enchantment functionality
	 *
	 * @param event The LivingJumpEvent
	 */
	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		CppEnchantments.performAction("hops", event.getEntityLiving(), event);
	}

	/**
	 * Prevents taking extra fall damage from the Hops enchantment
	 *
	 * @param event The LivingFallEvent
	 */
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event) {
		CppEnchantments.performAction("hops", event.getEntityLiving(), event);
	}

	/**
	 * Gives the player the workbenching achievement when crafting a crafting pad
	 *
	 * @param event The ItemCraftingEvent
	 */
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		if (event.crafting.getItem() == CppItems.crafting_pad)
			event.player.addStat(AchievementList.buildWorkBench);
	}

	/**
	 * Syncs the config file if it changes
	 *
	 * @param event The OnConfigChangedEvent
	 */
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(CppModInfo.MOD_ID))
			CppConfigHandler.syncConfig();
	}

	/**
	 * Enables binoculars functionality
	 *
	 * @param event The FOVUpdateEvent
	 */
	@SubscribeEvent
	public void onFOVUpdate(FOVUpdateEvent event) {
		ItemStack helmet = event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (helmet != null && helmet.getItem() == CppItems.binoculars)
			event.setNewfov(event.getNewfov() / CppConfigHandler.binocularZoomAmount);
	}

	/**
	 * Syncs up motion-affecting enchantments to the client
	 *
	 * @param event The ClientTickEvent
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		World world = Minecraft.getMinecraft().theWorld;
		if (world != null) {
			List<Entity> arrows = world.getEntities(EntityArrow.class, EntitySelectors.IS_ALIVE);
			for (Entity arrow : arrows)
				CppEnchantments.performAction("homing", arrow, null);
			List<Entity> xpOrbs = world.getEntities(EntityXPOrb.class, EntitySelectors.IS_ALIVE);
			for (Entity xpOrb : xpOrbs)
				CppEnchantments.performAction("veteran", xpOrb, null);
		}
	}

	@SubscribeEvent
	public void addItemCaps(AttachCapabilitiesEvent.Entity event) {
		if (event.getEntity() instanceof EntityItem)
			event.addCapability(new ResourceLocation(CppModInfo.MOD_ID), new SelfPlantingProvider());
	}
}
