package com.github.anon10w1z.craftPP.misc;

import com.github.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.github.anon10w1z.craftPP.main.CraftPlusPlus;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockStem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraftforge.common.FishingHooks;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The vanilla properties changer for Craft++
 */
public class CppVanillaPropertiesChanger {
	/**
	 * Initializes the vanilla properties changer
	 */
	@SuppressWarnings({"unchecked", "unused"})
	public static void init() {
		//Modifying block step sounds
		Iterable<Block> blocks = Block.blockRegistry;
		for (Block block : blocks) {
			//stems
			if (block instanceof BlockStem || block instanceof BlockNetherWart)
				block.setStepSound(Block.soundTypeGrass);
				//fire
			else if (block instanceof BlockFire) {
				block.setStepSound(new SoundType(null, 1.5F, 0.65F) {
					@Override
					public String getStepSound() {
						return "fire.fire";
					}
				});
			}
		}
		//Modifying block creative tabs
		if (CppConfigHandler.commandBlockInRedstoneTab)
			Blocks.command_block.setCreativeTab(CreativeTabs.tabRedstone);
		Blocks.dragon_egg.setCreativeTab(CreativeTabs.tabDecorations);
		if (Loader.isModLoaded("NotEnoughItems") || Loader.isModLoaded("TooManyItems")) {
			CraftPlusPlus.logInfo("Found NotEnoughItems/TooManyItems, setting creative tab of mob spawner directly");
			Blocks.mob_spawner.setCreativeTab(CreativeTabs.tabMisc);
		} else {
			CreativeTabs fakeMiscCreativeTab = new CreativeTabs(4, "misc") {
				@Override
				@SideOnly(Side.CLIENT)
				public Item getTabIconItem() {
					return CreativeTabs.tabMisc.getTabIconItem();
				}

				@Override
				@SideOnly(Side.CLIENT)
				public void displayAllReleventItems(List list) {
					CreativeTabs.tabMisc.displayAllReleventItems(list); //add all items from the vanilla misc creative tab
					List<Integer> spawnEggIds = Lists.newArrayList(); //a list containing all entity IDs in the misc creative tab
					for (ItemStack itemstack : (List<ItemStack>) list)
						if (itemstack.getItem() == Items.spawn_egg)
							spawnEggIds.add(itemstack.getItemDamage()); //where getItemDamage returns the entity ID
					for (int entityId : spawnEggIds) {
						NBTTagCompound blockEntityTag = new NBTTagCompound();
						NBTTagCompound stackTagCompound = new NBTTagCompound();
						blockEntityTag.setString("EntityId", EntityList.getStringFromID(entityId));
						stackTagCompound.setTag("BlockEntityTag", blockEntityTag);
						ItemStack spawnerStack = new ItemStack(Blocks.mob_spawner);
						spawnerStack.setTagCompound(stackTagCompound);
						list.add(spawnerStack);
					}
					Collections.sort(list, new Comparator() {
						@Override
						public int compare(Object object1, Object object2) {
							ItemStack itemstack1 = (ItemStack) object1;
							ItemStack itemstack2 = (ItemStack) object2;
							return Item.getIdFromItem(itemstack1.getItem()) - Item.getIdFromItem(itemstack2.getItem());
						}
					}); //sort the items, in ascending order of item IDs
				}
			}; //instantiating a creative tab automatically registers it
		}
		//Modifying block names
		if (CppConfigHandler.renameButtons) {
			Blocks.stone_button.setUnlocalizedName("buttonStone");
			Blocks.wooden_button.setUnlocalizedName("buttonWood");
		}
		//Adding fishables
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(Items.paper), 10));
	}
}
