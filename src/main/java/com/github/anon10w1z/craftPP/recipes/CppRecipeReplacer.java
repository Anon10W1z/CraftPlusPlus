package com.github.anon10w1z.craftPP.recipes;

import com.github.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.github.anon10w1z.craftPP.main.CppUtils;
import com.github.anon10w1z.craftPP.main.CraftPlusPlus;
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
@SuppressWarnings("unchecked")
public class CppRecipeReplacer {
	/**
	 * Actually does all of the recipe replacing
	 */
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

			GameRegistry.addRecipe(new ItemStack(Items.stone_sword), "S", "S", "T", 'S', Blocks.stone, 'T', Items.stick);
			GameRegistry.addRecipe(new ItemStack(Items.stone_shovel), "S", "T", "T", 'S', Blocks.stone, 'T', Items.stick);
			GameRegistry.addRecipe(new ItemStack(Items.stone_pickaxe), "SSS", " T ", " T ", 'S', Blocks.stone, 'T', Items.stick);
			GameRegistry.addRecipe(new ItemStack(Items.stone_axe), "SS", "ST", " T", 'S', Blocks.stone, 'T', Items.stick);
			GameRegistry.addRecipe(new ItemStack(Items.stone_hoe), "SS", " T", " T", 'S', Blocks.stone, 'T', Items.stick);
		}
		//Stairs
		if (CppConfigHandler.useBetterStairsRecipes) {
			CraftPlusPlus.logInfo("Replacing stairs recipes");
			boolean doStairsFieldsExist = true;
			Iterable<Block> blocks = Block.blockRegistry;
			for (Block block : blocks)
				if (block instanceof BlockStairs) {
					BlockStairs stairs = (BlockStairs) block;
					IBlockState modelState = CppUtils.findObject(stairs, "modelState", "field_150151_M");
					if (modelState == null) {
						doStairsFieldsExist = false;
						break;
					}
					Block modelBlock = modelState.getBlock();
					int modelMetadata = OreDictionary.WILDCARD_VALUE;
					if (modelBlock.getBlockState().getValidStates().size() > 1)
						modelMetadata = modelBlock.getMetaFromState(modelState);
					replaceStairsRecipe(stairs, new ItemStack(modelBlock, 1, modelMetadata));
				}
			if (!doStairsFieldsExist) { //backup
				CraftPlusPlus.logInfo("Reverting to backup stairs recipe replacing");
				replaceStairsRecipe(Blocks.oak_stairs, new ItemStack(Blocks.planks));
				replaceStairsRecipe(Blocks.spruce_stairs, new ItemStack(Blocks.planks, 1, 1));
				replaceStairsRecipe(Blocks.birch_stairs, new ItemStack(Blocks.planks, 1, 2));
				replaceStairsRecipe(Blocks.jungle_stairs, new ItemStack(Blocks.planks, 1, 3));
				replaceStairsRecipe(Blocks.acacia_stairs, new ItemStack(Blocks.planks, 1, 4));
				replaceStairsRecipe(Blocks.dark_oak_stairs, new ItemStack(Blocks.planks, 1, 5));

				replaceStairsRecipe(Blocks.stone_stairs, new ItemStack(Blocks.cobblestone));
				replaceStairsRecipe(Blocks.brick_stairs, new ItemStack(Blocks.brick_block));
				replaceStairsRecipe(Blocks.stone_brick_stairs, new ItemStack(Blocks.stonebrick, 1, OreDictionary.WILDCARD_VALUE));
				replaceStairsRecipe(Blocks.nether_brick_stairs, new ItemStack(Blocks.nether_brick));
				replaceStairsRecipe(Blocks.sandstone_stairs, new ItemStack(Blocks.sandstone, 1, OreDictionary.WILDCARD_VALUE));
				replaceStairsRecipe(Blocks.red_sandstone_stairs, new ItemStack(Blocks.red_sandstone, 1, OreDictionary.WILDCARD_VALUE));
				replaceStairsRecipe(Blocks.quartz_stairs, new ItemStack(Blocks.quartz_block, 1, OreDictionary.WILDCARD_VALUE));
			}
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
	private static void removeRecipes(ItemStack result) {
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> recipeListCopy = CppUtils.copyList(recipeList);
		for (IRecipe recipe : recipeListCopy)
			if (ItemStack.areItemStacksEqual(result, recipe.getRecipeOutput()))
				recipeList.remove(recipe);
	}

	/**
	 * Removes the specified IRecipe (class) from the game
	 *
	 * @param recipeClass The class of the IRecipe to remove from the game
	 */
	private static void removeRecipe(Class<? extends IRecipe> recipeClass) {
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> recipeListCopy = CppUtils.copyList(recipeList);
		for (IRecipe recipe : recipeListCopy)
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
