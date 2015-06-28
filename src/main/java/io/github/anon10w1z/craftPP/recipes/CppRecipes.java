package io.github.anon10w1z.craftPP.recipes;

import io.github.anon10w1z.craftPP.blocks.CppBlocks;
import io.github.anon10w1z.craftPP.items.CppItems;
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
		//Craft++ Items
		GameRegistry.addRecipe(new ItemStack(CppItems.dynamite, 1, 0), " W", " G", "S ", 'W', Items.string, 'G', Items.gunpowder, 'S', Blocks.sand);
		GameRegistry.addSmelting(Items.egg, new ItemStack(CppItems.fried_egg), 0.35F);
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
		GameRegistry.addShapelessRecipe(input.copy().splitStack(9), output);
	}
}
