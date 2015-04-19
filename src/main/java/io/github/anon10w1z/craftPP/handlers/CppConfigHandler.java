package io.github.anon10w1z.craftPP.handlers;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * The config handler for Craft++
 */
public class CppConfigHandler {
	/**
	 * The actual configuration containing the configuration file
	 */
	public static Configuration config;
	//Creepers
	public static boolean creeperBurnInDaylight;
	public static boolean creeperDropTnt;
	//Recipes
	public static boolean useBetterStoneToolRecipes;
	public static boolean useBetterStairsRecipes;
	//Miscellaneous
	public static boolean babyZombieBurnInDaylight;
	public static boolean enableEndermanBlockDrop;
	public static boolean enableBatLeatherDrop;
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
		String creeperCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Creepers";
		creeperBurnInDaylight = config.get(creeperCategory, "Creepers burn in daylight", true, "Do creepers burn in daylight?").getBoolean();
		creeperDropTnt = config.get(creeperCategory, "Creepers drop TNT", true, "Do creepers have a 1/10 chance of dropping TNT?").getBoolean();
		config.setCategoryComment(creeperCategory, "Properties of creepers");

		String recipesCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Recipes";
		useBetterStoneToolRecipes = config.get(recipesCategory, "Stone tools crafted from stone", true, "Are stone tools crafted out of stone?").getBoolean();
		useBetterStairsRecipes = config.get(recipesCategory, "Better stairs recipe enabled", true, "Is the better vanilla stairs recipe enabled?").getBoolean();
		config.setCategoryComment(recipesCategory, "Enable/disable certain recipes");
		config.setCategoryRequiresMcRestart(recipesCategory, true);

		String miscCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Miscellaneous";
		String miscRequiresRestartCategory = miscCategory + Configuration.CATEGORY_SPLITTER + "Requires Restart";
		babyZombieBurnInDaylight = config.get(miscCategory, "Baby zombies burn in daylight", true, "Do baby zombies burn in daylight?").getBoolean();
		enableEndermanBlockDrop = config.get(miscCategory, "Enable endermen dropping held block", true, "Do enderman drop the block they are holding?").getBoolean();
		enableBatLeatherDrop = config.get(miscCategory, "Leather dropped by bats", true, "Do bats drop leather?").getBoolean();
		enableAutoSeedPlanting = config.get(miscCategory, "Enable automatic seed planting", true, "Do dropped seeds have the ability to plant themselves in farmland?").getBoolean();
		config.setCategoryComment(miscCategory, "Miscellaneous settings");
		//Requires Restart
		commandBlockInRedstoneTab = config.get(miscRequiresRestartCategory, "Command Blocks in creative menu", true, "Can command blocks be obtained in the redstone creative tab?").getBoolean();
		enableFlintAndSteelDispenserBehavior = config.get(miscRequiresRestartCategory, "Enable flint and steel dispenser behavior", false, "Can you use flint and steel with dispensers?").getBoolean();
		renameButtons = config.get(miscRequiresRestartCategory, "Rename buttons", true, "Do buttons get renamed based on their type?").getBoolean();
		config.setCategoryComment(miscRequiresRestartCategory, "Settings that require a Minecraft restart");
		config.setCategoryRequiresMcRestart(miscRequiresRestartCategory, true);

		if (config.hasChanged())
			config.save();
	}
}
