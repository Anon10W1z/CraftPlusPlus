package io.github.anon10w1z.craftPP.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Contains, initializes, and registers all of Craft++'s items
 */
public class CppItems {
	public static Item dynamite;
	public static Item obsidian_boat;
	public static Item fried_egg;
	public static Item binocular_lens;
	public static Item binoculars;

	public static void registerItems() {
		dynamite = new ItemDynamite();
		GameRegistry.registerItem(dynamite, "dynamite");
		obsidian_boat = new ItemObsidianBoat();
		GameRegistry.registerItem(obsidian_boat, "obsidian_boat");
		fried_egg = new ItemFood(5, 0.6F, false).setUnlocalizedName("eggFried");
		GameRegistry.registerItem(fried_egg, "egg_fried");
		binocular_lens = new Item().setUnlocalizedName("binocularLens").setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerItem(binocular_lens, "binocular_lens");
		binoculars = new ItemBinoculars();
		GameRegistry.registerItem(binoculars, "binoculars");
	}
}
