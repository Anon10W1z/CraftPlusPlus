package io.github.anon10w1z.cpp.handlers;

import io.github.anon10w1z.cpp.enchantments.CppEnchantmentBase;
import io.github.anon10w1z.cpp.enchantments.CppEnchantments;
import io.github.anon10w1z.cpp.enchantments.EntityTickingEnchantment;
import io.github.anon10w1z.cpp.entities.EntitySitPoint;
import io.github.anon10w1z.cpp.gui.GuiCppConfig;
import io.github.anon10w1z.cpp.items.CppItems;
import io.github.anon10w1z.cpp.main.CppModInfo;
import io.github.anon10w1z.cpp.main.CppUtils;
import io.github.anon10w1z.cpp.main.CraftPlusPlus;
import io.github.anon10w1z.cpp.misc.CppExtendedEntityProperties;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.client.GuiIngameModOptions;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

/**
 * The event handler for Craft++
 */
@SuppressWarnings({"unused", "unchecked"})
public final class CppEventHandler {
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
		World world = event.world;
		if (!world.isRemote) {
			Action action = event.action;
			Item heldItem = player.getHeldItem() == null ? null : player.getHeldItem().getItem();
			//Ender pearls
			if (action == Action.RIGHT_CLICK_AIR && player.capabilities.isCreativeMode && heldItem == Items.ender_pearl) {
				world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
				EntityEnderPearl enderPearl = new EntityEnderPearl(world, player);
				world.spawnEntityInWorld(enderPearl);
				player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(Items.ender_pearl)]);
			}
			//Signs
			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (CppConfigHandler.signOverhaul && heldItem == Items.sign) {
					event.setCanceled(true);
					Block block = world.getBlockState(event.pos).getBlock();
					if (event.face != EnumFacing.DOWN && !(block instanceof BlockSign) && block.getMaterial().isSolid()) {
						EnumFacing facing = event.face;
						BlockPos blockPos = event.pos.offset(facing);
						if (player.canPlayerEdit(blockPos, facing, player.getHeldItem()) && Blocks.standing_sign.canPlaceBlockAt(world, blockPos)) {
							if (facing == EnumFacing.UP) {
								int rotation = MathHelper.floor_double(((player.rotationYaw + 180) * 16 / 360) + 0.5) & 15;
								world.setBlockState(blockPos, Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, rotation), 3);
							} else
								world.setBlockState(blockPos, Blocks.wall_sign.getDefaultState().withProperty(BlockWallSign.FACING, facing), 3);
							if (!player.capabilities.isCreativeMode)
								--player.getHeldItem().stackSize;
							TileEntitySign tileEntitySign = (TileEntitySign) world.getTileEntity(blockPos);
							ReflectionHelper.setPrivateValue(TileEntitySign.class, tileEntitySign, true, "isEditable", "field_145916_j");
							return;
						}
					}
				}
				if (CppConfigHandler.signOverhaul && event.world.getTileEntity(event.pos) instanceof TileEntitySign) {
					TileEntitySign tileEntitySign = (TileEntitySign) event.world.getTileEntity(event.pos);
					if (heldItem == CppItems.sponge_wipe) {
						String signText = String.join("", Arrays.stream(tileEntitySign.signText).map(chatComponent -> getTextWithoutFormattingCodes(chatComponent.getUnformattedText())).collect(Collectors.toList()));
						if (!signText.equals("")) {
							ReflectionHelper.setPrivateValue(TileEntitySign.class, tileEntitySign, new IChatComponent[]{new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")}, "signText", "field_145915_a");
							if (player instanceof EntityPlayerMP)
								((EntityPlayerMP) player).playerNetServerHandler.sendPacket(tileEntitySign.getDescriptionPacket());
							if (!player.capabilities.isCreativeMode)
								player.inventory.consumeInventoryItem(CppItems.sponge_wipe);
						}
					}
					if (ItemStack.areItemsEqual(player.getHeldItem(), new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()))) {
						String signText = String.join("", Arrays.stream(tileEntitySign.signText).map(chatComponent -> getTextWithoutFormattingCodes(chatComponent.getUnformattedText())).collect(Collectors.toList()));
						if (signText.equals("")) {
							ReflectionHelper.setPrivateValue(TileEntitySign.class, tileEntitySign, true, "isEditable", "field_145916_j");
							player.openEditSign(tileEntitySign);
							if (!player.capabilities.isCreativeMode && --player.inventory.mainInventory[player.inventory.currentItem].stackSize <= 0)
								player.inventory.mainInventory[player.inventory.currentItem] = null;
						}
					}
				}
				if (CppConfigHandler.sitOnStairs && heldItem == null && world.getBlockState(event.pos).getBlock() instanceof BlockStairs) {
					for (Object entityObject : world.getEntities(EntitySitPoint.class, IEntitySelector.selectAnything)) {
						EntitySitPoint sitPoint = (EntitySitPoint) entityObject;
						if (sitPoint.blockPos.equals(event.pos.down())) //if there is someone already sitting in the target position
							return;
					}
					EntitySitPoint sitPoint = new EntitySitPoint(world, event.pos.down());
					world.spawnEntityInWorld(sitPoint);
					player.mountEntity(sitPoint);
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
		return Pattern.compile("(?i)" + '\u00a7' + "[0-9A-FK-OR]").matcher(text).replaceAll("");
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
				BlockPos blockPos = new BlockPos(livingEntity.posX, Math.round(livingEntity.posY), livingEntity.posZ);
				if (f > 0.5 && random.nextFloat() * 30 < (f - 0.4) * 2 && world.canSeeSky(blockPos)) {
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
		if (event.entity instanceof EntityItem)
			CppExtendedEntityProperties.registerExtendedProperties((EntityItem) event.entity);
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
			List<EntityItem> entityItems = world.getEntities(EntityItem.class, IEntitySelector.selectAnything);
			for (EntityItem entityItem : entityItems)
				CppExtendedEntityProperties.getExtendedProperties(entityItem).handlePlantingLogic();
			for (Object entityObject : world.getEntities(Entity.class, IEntitySelector.selectAnything))
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
		if (CppConfigHandler.mobSpawnerSilkTouchDrop && !player.capabilities.isCreativeMode && event.state.getBlock() == Blocks.mob_spawner && EnchantmentHelper.getSilkTouchModifier(player) && player.canHarvestBlock(Blocks.mob_spawner)) {
			World world = event.world;
			BlockPos blockPos = event.pos;
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
		EntityPlayer harvester = event.harvester;
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
		CppEnchantments.performAction("quickdraw", event.entityPlayer, event);
	}

	/**
	 * Enables the Hops enchantment functionality
	 *
	 * @param event The LivingJumpEvent
	 */
	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		CppEnchantments.performAction("hops", event.entityLiving, event);
	}

	/**
	 * Prevents taking extra fall damage from the Hops enchantment
	 *
	 * @param event The LivingFallEvent
	 */
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event) {
		CppEnchantments.performAction("hops", event.entityLiving, event);
	}

	/**
	 * Gives the player the workbenching achievement when crafting a crafting pad
	 *
	 * @param event The ItemCraftingEvent
	 */
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		if (event.crafting.getItem() == CppItems.crafting_pad)
			event.player.triggerAchievement(AchievementList.buildWorkBench);
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
	 * Enables binoculars functionality
	 *
	 * @param event The FOVUpdateEvent
	 */
	@SubscribeEvent
	public void onFOVUpdate(FOVUpdateEvent event) {
		ItemStack helmet = event.entity.getEquipmentInSlot(4);
		if (helmet != null && helmet.getItem() == CppItems.binoculars)
			event.newfov /= CppConfigHandler.binocularZoomAmount;
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
			List<Entity> arrows = world.getEntities(EntityArrow.class, IEntitySelector.selectAnything);
			for (Entity arrow : arrows)
				CppEnchantments.performAction("homing", arrow, null);
			List<Entity> xpOrbs = world.getEntities(EntityXPOrb.class, IEntitySelector.selectAnything);
			for (Entity xpOrb : xpOrbs)
				CppEnchantments.performAction("veteran", xpOrb, null);
		}
	}

	/**
	 * Draws potion effect icons in the top-left corner
	 *
	 * @param event The (Post) RenderGameOverlayEvent
	 */
	@SubscribeEvent
	public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.CROSSHAIRS) {
			if (CraftPlusPlus.proxy.isPotionKeyPressed())
				displayPotionEffects = !displayPotionEffects; //toggle the potion effect overlay
			if (displayPotionEffects && !CraftPlusPlus.proxy.isGuiOpen()) {
				int xPos = 2;
				int yPos = 2;
				Minecraft minecraft = Minecraft.getMinecraft();
				List<PotionEffect> potionEffects = new ArrayList<>(minecraft.thePlayer.getActivePotionEffects());
				Collections.sort(potionEffects, (potionEffect1, potionEffect2) -> potionEffect1.getDuration() - potionEffect2.getDuration());
				Gui gui = new Gui();
				for (PotionEffect potionEffect : potionEffects) {
					Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
					int potionDuration = potionEffect.getDuration();
					if (!potion.hasStatusIcon())
						continue;
					minecraft.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/inventory.png")); //draw from inventory.png
					int iconIndex = potion.getStatusIconIndex();
					if ((potionDuration > 200 || potionDuration <= 60 || potionDuration % 20 < 10) && (potionDuration > 60 || potionDuration % 10 < 5)) {
						//constants for drawing/loading potion icons from inventory.png
						final int POTION_ICON_SIZE = 18;
						final int POTION_ICON_BASE_X_OFFSET = 0;
						final int POTION_ICON_BASE_Y_OFFSET = 198;
						final int POTION_ICONS_PER_ROW = 8;
						final int POTION_ICON_SPACING = POTION_ICON_SIZE + 2;
						GL11.glPushMatrix();
						GL11.glScalef(8 / 9F, 8 / 9F, 1);
						gui.drawTexturedModalRect(xPos, yPos, POTION_ICON_BASE_X_OFFSET + iconIndex % POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_BASE_Y_OFFSET + iconIndex / POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_SIZE, POTION_ICON_SIZE);
						GL11.glPopMatrix();
						if (potionEffect.getAmplifier() > 0) {
							FontRenderer fontRenderer = minecraft.fontRendererObj;
							String amplifierString = Integer.toString(potionEffect.getAmplifier() + 1);
							gui.drawString(fontRenderer, amplifierString, xPos + 17 - fontRenderer.getStringWidth(amplifierString), yPos + 9, 0xFFFFFF);
						}
					}
				}
			}
		}
	}

	/**
	 * Changes the mod options GUI into Craft++'s config GUI
	 *
	 * @param event The GuiOpenEvent
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		if (event.gui instanceof GuiIngameModOptions)
			event.gui = new GuiCppConfig();
	}
}
