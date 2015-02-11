package com.anon10w1z.craftPP.recipes;

import com.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.anon10w1z.craftPP.main.CppUtils;
import com.anon10w1z.craftPP.main.CraftPlusPlus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

/**
 * Used to remove certain recipes from the game
 */
public class CppRecipeReplacer {
	/**
	 * Removes unwanted recipes from the game, and replaces them with new ones
	 */
	@SuppressWarnings("unchecked")
	public static void replaceRecipes() {
		//Stone Tools
		CraftPlusPlus.logInfo("Replacing stone tool recipes");
		if (CppConfigHandler.useBetterStoneToolRecipes) {
			removeRecipes(new ItemStack(Items.stone_sword));
			removeRecipes(new ItemStack(Items.stone_shovel));
			removeRecipes(new ItemStack(Items.stone_pickaxe));
			removeRecipes(new ItemStack(Items.stone_axe));
			removeRecipes(new ItemStack(Items.stone_hoe));
			OreDictionary.registerOre("stone", new ItemStack(Blocks.stone, 1, OreDictionary.WILDCARD_VALUE));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_sword), "S", "S", "T", 'S', "stone", 'T', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_shovel), "S", "T", "T", 'S', "stone", 'T', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_pickaxe), "SSS", " T ", " T ", 'S', "stone", 'T', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_axe), "SS", "ST", " T", 'S', "stone", 'T', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_hoe), "SS", " T", " T", 'S', "stone", 'T', "stickWood"));
		}
		//Stairs
		CraftPlusPlus.logInfo("Replacing stairs recipes");
		for (Block block : CppUtils.getArray(Block.blockRegistry, Block.class)) {
			if (block instanceof BlockStairs && CppConfigHandler.useBetterStairsRecipes) {
				try {
					BlockStairs stairs = (BlockStairs) block;
					Block modelBlock = CppUtils.findObject(stairs, "modelBlock", "field_150149_b");
					IBlockState modelState = CppUtils.findObject(stairs, "modelState", "field_150151_M");
					replaceStairsRecipe(stairs, new ItemStack(modelBlock, 1, modelBlock.getMetaFromState(modelState)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//Buttons
		CraftPlusPlus.logInfo("Replacing vanilla button recipes");
		removeRecipes(new ItemStack(Blocks.wooden_button));
		removeRecipes(new ItemStack(Blocks.stone_button));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.wooden_button, 4), "plankWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.stone_button, 4), "stone"));
	}

	/**
	 * Removes a recipe/recipes from the game. <br>
	 * All recipes for the resulting ItemStack are removed from the game.
	 *
	 * @param result - The ItemStack which is outputted from the workbench
	 */
	@SuppressWarnings("unchecked")
	private static void removeRecipes(ItemStack result) {
		List recipeList = CraftingManager.getInstance().getRecipeList();
		for (IRecipe recipe : CppUtils.getArray(recipeList, IRecipe.class)) {
			ItemStack recipeResult = recipe.getRecipeOutput();
			if (ItemStack.areItemStacksEqual(result, recipeResult))
				recipeList.remove(recipe);
		}
	}

	/**
	 * Replaces the recipe for stairs with a better one
	 *
	 * @param stairs   - The stairs
	 * @param material - The material used to craft the stairs
	 */
	private static void replaceStairsRecipe(Block stairs, ItemStack material) {
		if (stairs instanceof BlockStairs && CppConfigHandler.useBetterStairsRecipes) {
			removeRecipes(new ItemStack(stairs, 4));
			GameRegistry.addRecipe(new ItemStack(stairs, 4), " S", "SS", 'S', material);
		}
	}
}
