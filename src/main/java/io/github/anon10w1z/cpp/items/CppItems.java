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
	public static Item DYNAMITE;
	public static Item STONE_BOAT;
	public static Item OBSIDIAN_BOAT;
	public static Item FRIED_EGG;
	public static Item CRAFTING_PAD;
	public static Item BINOCULAR_LENS;
	public static Item BINOCULARS;

	public static void registerItems() {
		DYNAMITE = new ItemDynamite();
		GameRegistry.register(DYNAMITE);
		OBSIDIAN_BOAT = new ItemObsidianBoat();
		GameRegistry.register(OBSIDIAN_BOAT);
		FRIED_EGG = new ItemFood(5, 0.6F, false).setUnlocalizedName("eggFried").setRegistryName(CppModInfo.MOD_ID, "egg_fried");
		GameRegistry.register(FRIED_EGG);
		if (CppConfigHandler.craftingTableChanges) {
			CRAFTING_PAD = new ItemCraftingPad();
			GameRegistry.register(CRAFTING_PAD);
		}
		BINOCULAR_LENS = new Item().setUnlocalizedName("binocularLens").setRegistryName(CppModInfo.MOD_ID, "binocular_lens").setCreativeTab(CreativeTabs.TOOLS);
		GameRegistry.register(BINOCULAR_LENS);
		BINOCULARS = new ItemBinoculars();
		GameRegistry.register(BINOCULARS);
	}
}
