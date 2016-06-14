package io.github.anon10w1z.cpp.items;

import io.github.anon10w1z.cpp.handlers.CppConfigHandler;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Contains, initializes, and registers all of Craft++'s items
 */
public class CppItems {
	public static Item dynamite;
	public static Item stone_boat;
	public static Item obsidian_boat;
	public static Item fried_egg;
	public static Item crafting_pad;
	public static Item binocular_lens;
	public static Item binoculars;

	public static void registerItems() {
		dynamite = new ItemDynamite();
		GameRegistry.register(dynamite);
		obsidian_boat = new ItemObsidianBoat();
		GameRegistry.register(obsidian_boat);
		fried_egg = new ItemFood(5, 0.6F, false).setUnlocalizedName("eggFried").setRegistryName(CppModInfo.MOD_ID, "egg_fried");
		GameRegistry.register(fried_egg);
		if (CppConfigHandler.craftingTableChanges) {
			crafting_pad = new ItemCraftingPad();
			GameRegistry.register(crafting_pad);
		}
		binocular_lens = new Item().setUnlocalizedName("binocularLens").setRegistryName(CppModInfo.MOD_ID, "binocular_lens").setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.register(binocular_lens);
		binoculars = new ItemBinoculars();
		GameRegistry.register(binoculars);
	}
}
