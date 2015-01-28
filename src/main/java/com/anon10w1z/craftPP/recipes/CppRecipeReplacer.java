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
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Used to remove certain recipes from the game
 */
public class CppRecipeReplacer {
	private static CraftingManager craftingmanager = CraftingManager.getInstance();

	/**
	 * Removes unwanted recipes from the game, and replaces them with new ones
	 */
	public static void replaceRecipes() {
		//Stone Tools
		if (CppConfigHandler.useBetterStoneToolRecipes) {
			removeRecipes(new ItemStack(Items.stone_sword));
			removeRecipes(new ItemStack(Items.stone_shovel));
			removeRecipes(new ItemStack(Items.stone_pickaxe));
			removeRecipes(new ItemStack(Items.stone_axe));
			removeRecipes(new ItemStack(Items.stone_hoe));

			OreDictionary.registerOre("stone", new ItemStack(Blocks.stone, 1, OreDictionary.WILDCARD_VALUE));
			craftingmanager.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_sword), "S", "S", "T", 'S', "stone", 'T', "stickWood"));
			craftingmanager.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_shovel), "S", "T", "T", 'S', "stone", 'T', "stickWood"));
			craftingmanager.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_pickaxe), "SSS", " T ", " T ", 'S', "stone", 'T', "stickWood"));
			craftingmanager.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_axe), "SS", "ST", " T", 'S', "stone", 'T', "stickWood"));
			craftingmanager.addRecipe(new ShapedOreRecipe(new ItemStack(Items.stone_hoe), "SS", " T", " T", 'S', "stone", 'T', "stickWood"));
		}
		//Stairs
		@SuppressWarnings("unchecked") Block[] blocks = CppUtils.getArray(Block.blockRegistry, Block.class);
		for (Block block : blocks) {
			if (block instanceof BlockStairs && CppConfigHandler.useBetterStairsRecipes) {
				BlockStairs stairs = (BlockStairs) block;
				try {
					Field modelBlockField = CppUtils.findField(stairs.getClass(), "modelBlock", "field_150149_b");
					Block modelBlock = (Block) modelBlockField.get(stairs);
					Field modelStateField = CppUtils.findField(stairs.getClass(), "modelState", "field_150151_M");
					IBlockState modelState = (IBlockState) modelStateField.get(stairs);
					int metadata = modelBlock.getMetaFromState(modelState);
					replaceStairsRecipe(stairs, new ItemStack(modelBlock, 1, metadata));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//Buttons
		removeRecipes(new ItemStack(Blocks.wooden_button));
		removeRecipes(new ItemStack(Blocks.stone_button));

		craftingmanager.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.wooden_button, 4), "plankWood"));
		craftingmanager.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.stone_button, 4), "stone"));
	}

	/**
	 * Removes a recipe/recipes from the game. <br>
	 * All recipes for the resulting ItemStack are removed from the game.
	 *
	 * @param result - The ItemStack which is outputted from the workbench
	 */
	private static void removeRecipes(ItemStack result) {
		List recipeList = craftingmanager.getRecipeList();
		@SuppressWarnings("unchecked") IRecipe[] recipes = CppUtils.getArray(recipeList, IRecipe.class);
		for (IRecipe recipe : recipes) {
			ItemStack recipeResult = recipe.getRecipeOutput();
			if (recipeResult != null && ItemStack.areItemStacksEqual(result, recipeResult)) {
				int i = 0;
				while (recipeList.contains(recipe)) {
					CraftPlusPlus.logInfo("Removing crafting recipe #" + ++i + " for " + result.getDisplayName());
					recipeList.remove(recipe);
				}
			}
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
			craftingmanager.addRecipe(new ItemStack(stairs, 4), " S", "SS", 'S', material);
		}
	}
}
