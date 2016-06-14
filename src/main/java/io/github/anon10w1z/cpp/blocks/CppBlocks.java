package io.github.anon10w1z.cpp.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Contains, initializes, and registers all of Craft++'s blocks
 */
public class CppBlocks {
	public static Block flint_block;
	public static Block sugar_block;
	public static Block charcoal_block;

	/**
	 * Registers the blocks for Craft++
	 */
	public static void registerBlocks() {
		flint_block = new BlockCppStorage(Material.rock, "flint").setStepSound(SoundType.STONE).setHardness(0.8F);
		registerBlock(flint_block);

		sugar_block = new BlockSugar();
		registerBlock(sugar_block);

		charcoal_block = new BlockCppStorage(Material.rock, "charcoal").setStepSound(SoundType.STONE).setHardness(5).setResistance(10);
		Blocks.fire.setFireInfo(charcoal_block, 5, 5);
		registerBlock(charcoal_block);
	}

	/**
	 * Registers a block with Minecraft
	 *
	 * @param block The block to be registered
	 */
	private static void registerBlock(Block block) {
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}
}
