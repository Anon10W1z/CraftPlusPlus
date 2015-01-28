package com.anon10w1z.craftPP.main;

import com.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockStem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraftforge.common.FishingHooks;

import java.util.Random;

public class CppVanillaPropertiesChanger {
	/**
	 * Initializes the vanilla properties changer
	 */
	public static void init() {
		//Modifying block step sounds
		@SuppressWarnings("unchecked") Block[] blocks = Iterables.toArray(Block.blockRegistry, Block.class);
		for (Block block : blocks) {
			if (block instanceof BlockStem || block instanceof BlockNetherWart)
				block.setStepSound(Block.soundTypeGrass);
			else if (block instanceof BlockFire && block.stepSound == Block.soundTypeCloth) {
				block.setStepSound(new SoundType(null, 1.5F, 0.65F) {
					@Override
					public String getStepSound() {
						return "fire.fire";
					}
				});
			}
		}
		//Modifying block creative tabs
		if (CppConfigHandler.commandBlockInRedstoneTab) Blocks.command_block.setCreativeTab(CreativeTabs.tabRedstone);

		Blocks.dragon_egg.setCreativeTab(CreativeTabs.tabDecorations);
		//Modifying block names
		Blocks.stone_button.setUnlocalizedName("buttonStone");
		Blocks.wooden_button.setUnlocalizedName("buttonWood");
		//Adding fishables
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(Items.paper), 10));
		FishingHooks.removeJunk(new Predicate<WeightedRandomFishable>() {
			@Override
			public boolean apply(WeightedRandomFishable input) {
				return input.getItemStack(new Random()).getItem() != Item.getItemFromBlock(Blocks.tripwire_hook);
			}
		});
	}
}
