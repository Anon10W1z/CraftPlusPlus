package com.anon10w1z.craftPP.main;

import com.anon10w1z.craftPP.handlers.CppConfigHandler;
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
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

/**
 * The vanilla properties changer for Craft++
 */
public class CppVanillaPropertiesChanger {
	/**
	 * Initializes the vanilla properties changer
	 */
	@SuppressWarnings("unchecked")
	public static void init() {
		//Modifying block step sounds
		for (Block block : CppUtils.getBlockArray()) {
			if (block instanceof BlockStem || block instanceof BlockNetherWart)
				block.setStepSound(Block.soundTypeGrass);
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
		Item fakeSpawner = new Item() {
			private ItemStack getSpawnerFromEntityName(String entityName) {
				ItemStack spawner = new ItemStack(Blocks.mob_spawner);
				NBTTagCompound nbtTagCompound = new NBTTagCompound();
				NBTTagCompound blockEntityTag = new NBTTagCompound();
				blockEntityTag.setString("EntityId", entityName);
				nbtTagCompound.setTag("BlockEntityTag", blockEntityTag);
				spawner.setTagCompound(nbtTagCompound);
				return spawner;
			}			{
				this.setHasSubtypes(true);
				this.setCreativeTab(CreativeTabs.tabMisc);
			}

			@SuppressWarnings("unchecked")
			public void getSubItems(Item item, CreativeTabs creativeTab, List subItemsList) {
				for (Object entityName : EntityList.getEntityNameList()) { //iterate over entity names
					Class entityClass = (Class) EntityList.stringToClassMapping.get(entityName);
					if (entityClass != null && EntityLivingBase.class.isAssignableFrom(entityClass) && entityClass != EntityArmorStand.class) //make sure spawners in the creative menu can only spawn living entities, and no armor stands
						subItemsList.add(this.getSpawnerFromEntityName((String) entityName));
				}
			}


		}; //unusual way to get mob spawners in creative mode menu
		GameRegistry.registerItem(fakeSpawner, "fake_spawner");
		//Modifying block names
		if (CppConfigHandler.renameButtons) {
			Blocks.stone_button.setUnlocalizedName("buttonStone");
			Blocks.wooden_button.setUnlocalizedName("buttonWood");
		}
		//Adding fishables
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(Items.paper), 10));
	}
}
