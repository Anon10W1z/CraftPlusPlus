package com.anon10w1z.craftPP.recipes;

import com.anon10w1z.craftPP.blocks.CppBlocks;
import com.anon10w1z.craftPP.main.CppUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CppRecipes {
	/**
	 * Registers the recipes for Craft++
	 */
	public static void registerRecipes() {
		//Storage Blocks
		CppUtils.registerStorageRecipes(new ItemStack(Items.flint), CppBlocks.flint_block);
		CppUtils.registerStorageRecipes(new ItemStack(Items.sugar), CppBlocks.sugar_block);
		CppUtils.registerStorageRecipes(new ItemStack(Items.coal, 1, 1), CppBlocks.charcoal_block);
		//Better Vanilla
		GameRegistry.addShapelessRecipe(new ItemStack(Items.string, 4), new ItemStack(Blocks.wool, 1, 0));
	}
}
