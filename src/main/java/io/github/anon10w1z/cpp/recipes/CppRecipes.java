package io.github.anon10w1z.cpp.recipes;

import io.github.anon10w1z.cpp.blocks.CppBlocks;
import io.github.anon10w1z.cpp.handlers.CppConfigHandler;
import io.github.anon10w1z.cpp.items.CppItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * The recipe registration class for Craft++
 */
public class CppRecipes {
	/**
	 * Registers the recipes for Craft++
	 */
	public static void registerRecipes() {
		//Storage Blocks
		registerStorageRecipes(new ItemStack(Items.FLINT), CppBlocks.FLINT_BLOCK);
		registerStorageRecipes(new ItemStack(Items.SUGAR), CppBlocks.SUGAR_BLOCK);
		registerStorageRecipes(new ItemStack(Items.COAL, 1, 1), CppBlocks.CHARCOAL_BLOCK);
		//Craft++ Items
		GameRegistry.addRecipe(new ItemStack(CppItems.DYNAMITE, 1, 0), " W", " G", "S ", 'W', Items.STRING, 'G', Items.GUNPOWDER, 'S', Blocks.SAND);
		GameRegistry.addRecipe(new ItemStack(CppItems.OBSIDIAN_BOAT), "O O", "OOO", 'O', Blocks.OBSIDIAN);
		if (CppConfigHandler.craftingTableChanges)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CppItems.CRAFTING_PAD), "PP", "PP", 'P', "plankWood"));
		GameRegistry.addSmelting(Items.EGG, new ItemStack(CppItems.FRIED_EGG), 0.35F);
		ItemStack dyeStack = new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage());
		GameRegistry.addRecipe(new ItemStack(CppItems.BINOCULAR_LENS, 4), " I ", "IPI", " I ", 'I', Items.IRON_INGOT, 'P', Blocks.GLASS_PANE);
		GameRegistry.addRecipe(new ItemStack(CppItems.BINOCULARS), "DDD", "LIL", "DDD", 'D', dyeStack, 'L', CppItems.BINOCULAR_LENS, 'I', Items.IRON_INGOT);
		//Better Vanilla
		GameRegistry.addShapelessRecipe(new ItemStack(Items.STRING, 4), Blocks.WOOL);
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
