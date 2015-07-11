package io.github.anon10w1z.craftPP.handlers;

import com.google.common.collect.Maps;
import io.github.anon10w1z.craftPP.enchantments.CppEnchantments;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.util.Map;

/**
 * The config handler for Craft++
 */
public class CppConfigHandler {
	/**
	 * The actual configuration containing the configuration file
	 */
	public static Configuration config;
	//Mob Drops
	public static double creeperDropTntChance;
	public static double endermanBlockDropChance;
	public static double batLeatherDropChance;
	public static int minAnimalBoneDropAmount;
	public static int maxAnimalBoneDropAmount;
	//Recipes
	public static boolean useBetterStoneToolRecipes;
	public static boolean useBetterStairsRecipes;
	//Enchantments
	public static Map<String, Boolean> enchantmentNameToEnable = Maps.newHashMap();
	//Miscellaneous
	public static boolean creeperBurnInDaylight;
	public static boolean babyZombieBurnInDaylight;
	public static boolean enableAutoSeedPlanting;
	//Miscellaneous: Requires Restart
	public static boolean commandBlockInRedstoneTab;
	public static boolean enableFlintAndSteelDispenserBehavior;
	public static boolean renameButtons;

	/**
	 * Initializes the config handler for Craft++
	 *
	 * @param configFile The configuration file (fetched from the FMLPreInitializationEvent)
	 */
	public static void init(File configFile) {
		config = new Configuration(configFile);
		config.load();
		syncConfig();
	}

	/**
	 * Syncs the config file
	 */
	public static void syncConfig() {
		String mobDropsCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Mob Drops";
		creeperDropTntChance = get(mobDropsCategory, "Chance of creepers dropping TNT", 1D, "The chance of creepers dropping TNT, out of 10.").setMinValue(0).setMaxValue(10).getDouble() / 10;
		endermanBlockDropChance = get(mobDropsCategory, "Chance of enderman dropping held block", 10D, "The chance of enderman dropping their held block, out of 10.").setMinValue(0).setMaxValue(10).getDouble() / 10;
		batLeatherDropChance = get(mobDropsCategory, "Chance of bats dropping leather", 10D, "The chance of bats dropping leather, out of 10.").setMinValue(0).setMaxValue(10).getDouble() / 10;
		maxAnimalBoneDropAmount = get(mobDropsCategory, "Maximum number of bones animals drop", 2, "The maximum number of bones animals can drop.").setMinValue(0).getInt();
		minAnimalBoneDropAmount = get(mobDropsCategory, "Minimum number of bones animals drop", 1, "The minimum number of bones animals can drop.").setMinValue(0).setMaxValue(maxAnimalBoneDropAmount).getInt();
		config.setCategoryComment(mobDropsCategory, "Modify mob drops");

		String recipesCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Recipes";
		useBetterStoneToolRecipes = get(recipesCategory, "Stone tools crafted from stone", true, "Are stone tools crafted out of stone?");
		useBetterStairsRecipes = get(recipesCategory, "Better stairs recipe enabled", true, "Is the better stairs recipe enabled?");
		config.setCategoryComment(recipesCategory, "Toggle Craft++'s recipe enhancements");
		config.setCategoryRequiresMcRestart(recipesCategory, true);

		String enchantmentsCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Enchantments";
		for (String enchantmentName : CppEnchantments.enchantmentNames)
			enchantmentNameToEnable.put(enchantmentName, get(enchantmentsCategory, "Enable " + enchantmentName, true, "Is the " + enchantmentName + " enchantment enabled?"));
		config.setCategoryComment(enchantmentsCategory, "Toggle each of Craft++'s enchantments");
		config.setCategoryRequiresMcRestart(enchantmentsCategory, true);

		String miscCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Miscellaneous";
		creeperBurnInDaylight = get(miscCategory, "Creepers burn in daylight", true, "Do creepers burn in daylight?");
		babyZombieBurnInDaylight = get(miscCategory, "Baby zombies burn in daylight", true, "Do baby zombies burn in daylight?");
		enableAutoSeedPlanting = get(miscCategory, "Enable automatic seed planting", true, "Do dropped seeds plant themselves?");
		config.setCategoryComment(miscCategory, "Miscellaneous settings");
		//Requires Restart
		String miscRequiresRestartCategory = miscCategory + Configuration.CATEGORY_SPLITTER + "Requires Restart";
		commandBlockInRedstoneTab = get(miscRequiresRestartCategory, "Command Blocks in creative menu", true, "Can command blocks be obtained from the redstone creative tab?");
		enableFlintAndSteelDispenserBehavior = get(miscRequiresRestartCategory, "Enable flint and steel dispenser behavior", false, "Can you use flint and steel with dispensers?");
		renameButtons = get(miscRequiresRestartCategory, "Rename buttons", true, "Do buttons get renamed based on their material?");
		config.setCategoryComment(miscRequiresRestartCategory, "Settings that require a Minecraft restart");
		config.setCategoryRequiresMcRestart(miscRequiresRestartCategory, true);

		if (config.hasChanged())
			config.save();
	}

	public static Property get(String category, String key, int defaultValue, String comment) {
		return config.get(category, key, defaultValue, comment);
	}

	public static Property get(String category, String key, double defaultValue, String comment) {
		Property property = config.get(category, key, defaultValue, comment);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return property.setConfigEntryClass(NumberSliderEntry.class);
		return property;
	}

	public static boolean get(String category, String key, boolean defaultValue, String comment) {
		return config.get(category, key, defaultValue, comment).getBoolean();
	}
}
