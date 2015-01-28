package com.anon10w1z.craftPP.recipes;

import com.anon10w1z.craftPP.blocks.CppBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class CppRecipes {
	/**
	 * Registers the recipes for Craft++
	 */
	public static void registerRecipes() {
		//Crafting
		CraftingManager craftingmanager = CraftingManager.getInstance();
		//Crafting: Storage Blocks
		craftingmanager.addShapelessRecipe(new ItemStack(Items.flint, 9), CppBlocks.flint_block);
		craftingmanager.addRecipe(new ItemStack(CppBlocks.flint_block), "FFF", "FFF", "FFF", 'F', Items.flint);

		craftingmanager.addShapelessRecipe(new ItemStack(Items.sugar, 9), CppBlocks.sugar_block);
		craftingmanager.addRecipe(new ItemStack(CppBlocks.sugar_block), "SSS", "SSS", "SSS", 'S', Items.sugar);

		craftingmanager.addShapelessRecipe(new ItemStack(Items.coal, 9, 1), CppBlocks.charcoal_block);
		craftingmanager.addRecipe(new ItemStack(CppBlocks.charcoal_block), "CCC", "CCC", "CCC", 'C', new ItemStack(Items.coal, 1, 1));
		//Crafting: Better Vanilla
		craftingmanager.addShapelessRecipe(new ItemStack(Items.string, 4), new ItemStack(Blocks.wool, 1, 0));
		craftingmanager.addShapelessRecipe(new ItemStack(Blocks.grass), Blocks.dirt, new ItemStack(Blocks.tallgrass, 1, 1));
	}
}
