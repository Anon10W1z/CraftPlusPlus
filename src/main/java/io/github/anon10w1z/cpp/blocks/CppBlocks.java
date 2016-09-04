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
	public static Block FLINT_BLOCK;
	public static Block SUGAR_BLOCK;
	public static Block CHARCOAL_BLOCK;

	/**
	 * Registers the blocks for Craft++
	 */
	public static void registerBlocks() {
		FLINT_BLOCK = new BlockCppStorage(Material.ROCK, "flint").setSoundType(SoundType.STONE).setHardness(0.8F);
		registerBlock(FLINT_BLOCK);

		SUGAR_BLOCK = new BlockSugar();
		registerBlock(SUGAR_BLOCK);

		CHARCOAL_BLOCK = new BlockCppStorage(Material.ROCK, "charcoal").setSoundType(SoundType.STONE).setHardness(5).setResistance(10);
		Blocks.FIRE.setFireInfo(CHARCOAL_BLOCK, 5, 5);
		registerBlock(CHARCOAL_BLOCK);
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
