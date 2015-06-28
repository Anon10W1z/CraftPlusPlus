package io.github.anon10w1z.craftPP.handlers;

import com.google.common.collect.Maps;
import io.github.anon10w1z.craftPP.enchantments.CppEnchantments;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;

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
		creeperDropTntChance = config.get(mobDropsCategory, "Chance of creepers dropping TNT", 1D, "The chance of creepers dropping TNT, out of 10").setMinValue(0).setMaxValue(10).setConfigEntryClass(NumberSliderEntry.class).getDouble() / 10;
		endermanBlockDropChance = config.get(mobDropsCategory, "Chance of enderman dropping held block", 10D, "The chance of enderman dropping their held block, out of 10").setMinValue(0).setMaxValue(10).setConfigEntryClass(NumberSliderEntry.class).getDouble() / 10;
		batLeatherDropChance = config.get(mobDropsCategory, "Chance of bats dropping leather", 10D, "The chance of bats dropping leather, out of 10").setMinValue(0).setMaxValue(10).setConfigEntryClass(NumberSliderEntry.class).getDouble() / 10;
		config.setCategoryComment(mobDropsCategory, "Modify mob drops");

		String recipesCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Recipes";
		useBetterStoneToolRecipes = config.get(recipesCategory, "Stone tools crafted from stone", true, "Are stone tools crafted out of stone?").getBoolean();
		useBetterStairsRecipes = config.get(recipesCategory, "Better stairs recipe enabled", true, "Is the better stairs recipe enabled?").getBoolean();
		config.setCategoryComment(recipesCategory, "Enable/disable certain recipes");
		config.setCategoryRequiresMcRestart(recipesCategory, true);

		String enchantmentsCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Enchantments";
		for (String enchantmentName : CppEnchantments.enchantmentNames)
			enchantmentNameToEnable.put(enchantmentName, config.get(enchantmentsCategory, "Enable " + enchantmentName, true, "Is the " + enchantmentName + " enchantment enabled?").getBoolean());
		config.setCategoryComment(enchantmentsCategory, "Enable/disable Craft++'s enchantments");
		config.setCategoryRequiresMcRestart(enchantmentsCategory, true);

		String miscCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Miscellaneous";
		creeperBurnInDaylight = config.get(miscCategory, "Creepers burn in daylight", true, "Do creepers burn in daylight?").getBoolean();
		babyZombieBurnInDaylight = config.get(miscCategory, "Baby zombies burn in daylight", true, "Do baby zombies burn in daylight?").getBoolean();
		enableAutoSeedPlanting = config.get(miscCategory, "Enable automatic seed planting", true, "Do dropped seeds plant themselves?").getBoolean();
		config.setCategoryComment(miscCategory, "Miscellaneous settings");
		//Requires Restart
		String miscRequiresRestartCategory = miscCategory + Configuration.CATEGORY_SPLITTER + "Requires Restart";
		commandBlockInRedstoneTab = config.get(miscRequiresRestartCategory, "Command Blocks in creative menu", true, "Can command blocks be obtained from the redstone creative tab?").getBoolean();
		enableFlintAndSteelDispenserBehavior = config.get(miscRequiresRestartCategory, "Enable flint and steel dispenser behavior", false, "Can you use flint and steel with dispensers?").getBoolean();
		renameButtons = config.get(miscRequiresRestartCategory, "Rename buttons", true, "Do buttons get renamed based on their material?").getBoolean();
		config.setCategoryComment(miscRequiresRestartCategory, "Settings that require a Minecraft restart");
		config.setCategoryRequiresMcRestart(miscRequiresRestartCategory, true);

		if (config.hasChanged())
			config.save();
	}
}
