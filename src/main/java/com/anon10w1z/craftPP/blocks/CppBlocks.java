package com.anon10w1z.craftPP.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Contains, initializes, and registers all of Craft++'s blocks
 */
public class CppBlocks {
	//Storage Blocks
	public static Block flint_block;
	public static Block sugar_block;
	public static Block charcoal_block;

	/**
	 * Registers the blocks for Craft++
	 */
	public static void registerBlocks() {
		//Storage Blocks
		flint_block = new BlockCppStorage(Material.rock, "flint").setHardness(0.8F).setStepSound(Block.soundTypePiston);
		GameRegistry.registerBlock(flint_block, "flint_block");

		sugar_block = new BlockFalling().setUnlocalizedName("sugarBlock").setHardness(0.5F).setStepSound(Block.soundTypeSand);
		sugar_block.setHarvestLevel("shovel", 0);
		GameRegistry.registerBlock(sugar_block, "sugar_block");

		charcoal_block = new BlockCppStorage(Material.rock, "charcoal").setHardness(5).setResistance(10).setStepSound(Block.soundTypePiston);
		Blocks.fire.setFireInfo(charcoal_block, 5, 5);
		GameRegistry.registerBlock(charcoal_block, "charcoal_block");
	}
}
