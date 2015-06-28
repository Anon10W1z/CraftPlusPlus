package io.github.anon10w1z.craftPP.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Contains, initializes, and registers all of Craft++'s items
 */
public class CppItems {
	public static Item dynamite;
	public static Item fried_egg;

	public static void registerItems() {
		dynamite = new ItemDynamite();
		GameRegistry.registerItem(dynamite, "dynamite");
		fried_egg = new ItemFood(5, 0.6F, false).setUnlocalizedName("eggFried");
		GameRegistry.registerItem(fried_egg, "egg_fried");
	}
}
