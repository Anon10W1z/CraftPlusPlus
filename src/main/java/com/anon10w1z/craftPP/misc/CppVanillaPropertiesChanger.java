package com.anon10w1z.craftPP.misc;

import com.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.anon10w1z.craftPP.main.CraftPlusPlus;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockStem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
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
			CraftPlusPlus.logInfo("Did not find NotEnoughItems/TooManyItems, using replacement miscellaneous creative tab");
			CreativeTabs fakeMiscCreativeTab = new CreativeTabs(4, "misc") {
				@Override
				@SideOnly(Side.CLIENT)
				public Item getTabIconItem() {
					return CreativeTabs.tabMisc.getTabIconItem();
				}

				@Override
				@SideOnly(Side.CLIENT)
				public void displayAllReleventItems(List list) {
					CreativeTabs.tabMisc.displayAllReleventItems(list);
					List<String> entityNameList = EntityList.getEntityNameList();
					for (String entityName : entityNameList) { //iterate over entity names
						Class entityClass = (Class) EntityList.stringToClassMapping.get(entityName);
						if (entityClass != null && EntityLivingBase.class.isAssignableFrom(entityClass) && entityClass != EntityArmorStand.class) {//make sure spawners in the creative menu can only spawn living entities, and no armor stands
							NBTTagCompound blockEntityTag = new NBTTagCompound();
							NBTTagCompound stackTagCompound = new NBTTagCompound();
							blockEntityTag.setString("EntityId", entityName);
							stackTagCompound.setTag("BlockEntityTag", blockEntityTag);
							ItemStack spawnerStack = new ItemStack(Blocks.mob_spawner);
							spawnerStack.setTagCompound(stackTagCompound);
							list.add(spawnerStack);
						}
					}
				}
			};
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
