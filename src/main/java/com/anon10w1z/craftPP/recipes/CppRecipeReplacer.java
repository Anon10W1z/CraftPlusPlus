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
import net.minecraft.item.crafting.RecipeRepairItem;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

/**
 * Used to remove certain recipes from the game, and replace them with new ones
 */
public class CppRecipeReplacer {
	/**
	 * Actually does all of the recipe replacing
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
		boolean doStairsFieldsExist = true;
		for (Block block : CppUtils.getBlockArray())
			if (block instanceof BlockStairs && CppConfigHandler.useBetterStairsRecipes) {
				BlockStairs stairs = (BlockStairs) block;
				Block modelBlock = CppUtils.findObject(stairs, "modelBlock", "field_150149_b");
				IBlockState modelState = CppUtils.findObject(stairs, "modelState", "field_150151_M");
				if (modelBlock == null || modelState == null) {
					CraftPlusPlus.logInfo("Reverting to backup stairs recipe replacing");
					doStairsFieldsExist = false;
					break;
				}
				replaceStairsRecipe(stairs, new ItemStack(modelBlock, 1, modelBlock.getMetaFromState(modelState)));
			}
		if (!doStairsFieldsExist) { //backup
			replaceStairsRecipe(Blocks.oak_stairs, new ItemStack(Blocks.planks));
			replaceStairsRecipe(Blocks.spruce_stairs, new ItemStack(Blocks.planks, 1, 1));
			replaceStairsRecipe(Blocks.birch_stairs, new ItemStack(Blocks.planks, 1, 2));
			replaceStairsRecipe(Blocks.jungle_stairs, new ItemStack(Blocks.planks, 1, 3));
			replaceStairsRecipe(Blocks.acacia_stairs, new ItemStack(Blocks.planks, 1, 4));
			replaceStairsRecipe(Blocks.dark_oak_stairs, new ItemStack(Blocks.planks, 1, 5));

			replaceStairsRecipe(Blocks.stone_stairs, new ItemStack(Blocks.cobblestone));
			replaceStairsRecipe(Blocks.brick_stairs, new ItemStack(Blocks.brick_block));
			replaceStairsRecipe(Blocks.stone_brick_stairs, new ItemStack(Blocks.stonebrick));
			replaceStairsRecipe(Blocks.nether_brick_stairs, new ItemStack(Blocks.nether_brick));
			replaceStairsRecipe(Blocks.sandstone_stairs, new ItemStack(Blocks.sandstone));
			replaceStairsRecipe(Blocks.red_sandstone_stairs, new ItemStack(Blocks.red_sandstone));
			replaceStairsRecipe(Blocks.quartz_stairs, new ItemStack(Blocks.quartz_block));
		}
		//Buttons
		CraftPlusPlus.logInfo("Replacing vanilla button recipes");
		removeRecipes(new ItemStack(Blocks.wooden_button));
		removeRecipes(new ItemStack(Blocks.stone_button));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.wooden_button, 4), "plankWood"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.stone_button, 4), "stone"));
		//Repairing Items
		CraftPlusPlus.logInfo("Removing tool repair recipes");
		removeRecipe(RecipeRepairItem.class);
	}

	/**
	 * Removes a recipe/recipes from the game. <br>
	 * All recipes for the resulting ItemStack are removed from the game.
	 *
	 * @param result The ItemStack which is outputted from the workbench
	 */
	@SuppressWarnings("unchecked")
	private static void removeRecipes(ItemStack result) {
		List recipeList = CraftingManager.getInstance().getRecipeList();
		for (IRecipe recipe : CppUtils.getRecipeArray())
			if (ItemStack.areItemStacksEqual(result, recipe.getRecipeOutput()))
				recipeList.remove(recipe);
	}

	/**
	 * Removes the specified IRecipe (class) from the game
	 *
	 * @param recipeClass The class of the IRecipe to remove from the game
	 */
	@SuppressWarnings("unchecked")
	private static void removeRecipe(Class<? extends IRecipe> recipeClass) {
		List recipeList = CraftingManager.getInstance().getRecipeList();
		for (IRecipe recipe : CppUtils.getRecipeArray())
			if (recipe.getClass() == recipeClass)
				recipeList.remove(recipe);
	}

	/**
	 * Replaces the recipe for stairs with the better one
	 *
	 * @param stairs   The stairs
	 * @param material The material used to craft the stairs
	 */
	private static void replaceStairsRecipe(Block stairs, ItemStack material) {
		ItemStack stairsStack = new ItemStack(stairs, 4);
		removeRecipes(stairsStack);
		GameRegistry.addRecipe(stairsStack, " S", "SS", 'S', material);
	}
}
