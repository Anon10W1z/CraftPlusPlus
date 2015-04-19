package io.github.anon10w1z.craftPP.recipes;

import io.github.anon10w1z.craftPP.blocks.CppBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * The recipe registration class for Craft++
 */
public class CppRecipes {
	/**
	 * Registers the recipes for Craft++
	 */
	public static void registerRecipes() {
		//Storage Blocks
		registerStorageRecipes(new ItemStack(Items.flint), CppBlocks.flint_block);
		registerStorageRecipes(new ItemStack(Items.sugar), CppBlocks.sugar_block);
		registerStorageRecipes(new ItemStack(Items.coal, 1, 1), CppBlocks.charcoal_block);
		//Better Vanilla
		GameRegistry.addShapelessRecipe(new ItemStack(Items.string, 4), Blocks.wool);
	}

	/**
	 * Registers storage block recipes for the specified input and output
	 *
	 * @param input  The ingredient ItemStack of the recipe
	 * @param output The output (storage block) of the recipe
	 */
	private static void registerStorageRecipes(ItemStack input, Block output) {
		GameRegistry.addRecipe(new ItemStack(output), "III", "III", "III", 'I', input);
		GameRegistry.addShapelessRecipe(input.splitStack(9), output);
	}
}
