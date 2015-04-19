package com.github.anon10w1z.craftPP.misc;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Enhances the vanilla ore dictionary system
 */
public class OreDictionaryEnhancer {
	/**
	 * Initializes the ore dictionary enhancer
	 */
	public static void init() {
		OreDictionary.registerOre("stone", new ItemStack(Blocks.stone, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.initVanillaEntries();
	}
}
