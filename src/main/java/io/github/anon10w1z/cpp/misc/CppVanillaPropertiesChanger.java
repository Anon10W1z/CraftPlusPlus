package io.github.anon10w1z.cpp.misc;

import io.github.anon10w1z.cpp.handlers.CppConfigHandler;
import io.github.anon10w1z.cpp.items.CppItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * The vanilla properties changer for Craft++
 */
public class CppVanillaPropertiesChanger {
	/**
	 * Initializes the vanilla properties changer
	 */
	@SuppressWarnings("unchecked")
	public static void init() {
		//Modifying block creative tabs
		if (CppConfigHandler.commandBlockInRedstoneTab)
			Blocks.COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
		Blocks.DRAGON_EGG.setCreativeTab(CreativeTabs.DECORATIONS);
		//Modifying block names
		if (CppConfigHandler.renameButtons) {
			Blocks.STONE_BUTTON.setUnlocalizedName("buttonStone");
			Blocks.WOODEN_BUTTON.setUnlocalizedName("buttonWood");
		}
		//Modifying achievements
		if (CppConfigHandler.craftingTableChanges)
			ReflectionHelper.setPrivateValue(Achievement.class, AchievementList.BUILD_WORK_BENCH, new ItemStack(CppItems.CRAFTING_PAD), "theItemStack", "field_75990_d");
	}
}
