package io.github.anon10w1z.craftPP.misc;

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
	@SuppressWarnings("unchecked")
	public static void init() {
		OreDictionary.registerOre("stone", new ItemStack(Blocks.stone, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("button", Blocks.stone_button);
		OreDictionary.registerOre("button", Blocks.wooden_button);
		OreDictionary.initVanillaEntries();
	}
}
