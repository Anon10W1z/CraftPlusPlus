package com.anon10w1z.craftPP.handlers;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CppConfigHandler {
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
	//Miscellaneous: Requires Restart
	public static boolean commandBlockInRedstoneTab;
	public static boolean enableFlintAndSteelDispenserBehavior;

	public static void init(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		syncConfig();
	}

	public static void syncConfig() {
		String creeperCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Creepers";
		creeperBurnInDaylight = config.get(creeperCategory, "Creepers burn in daylight", true, "Do creepers burn in " + "daylight?").getBoolean();
		creeperDropTnt = config.get(creeperCategory, "Creepers drop TNT", true, "Do creepers have a 1/10 chance of " + "dropping TNT?").getBoolean();
		config.setCategoryComment(creeperCategory, "Properties of creepers");

		String recipesCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Recipes";
		useBetterStoneToolRecipes = config.get(recipesCategory, "Stone tools crafted from stone", true, "Are stone " + "tools crafted out of stone?").getBoolean();
		useBetterStairsRecipes = config.get(recipesCategory, "Better stairs recipe enabled", true, "Is the better " + "vanilla stairs recipe enabled?").getBoolean();
		config.setCategoryComment(recipesCategory, "Enable/disable certain recipes");
		config.setCategoryRequiresMcRestart(recipesCategory, true);

		String miscCategory = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "Miscellaneous";
		String miscRequiresRestartCategory = miscCategory + Configuration.CATEGORY_SPLITTER + "Requires Restart";
		babyZombieBurnInDaylight = config.get(miscCategory, "Baby zombies burn in daylight", true, "Do baby zombies " + "burn in daylight?").getBoolean();
		enableEndermanBlockDrop = config.get(miscCategory, "Enable endermen dropping held block", true, "Do enderman " + "drop the block they are holding?").getBoolean();
		enableBatLeatherDrop = config.get(miscCategory, "Leather dropped by bats", true, "Do bats drop leather?").getBoolean();
		commandBlockInRedstoneTab = config.get(miscRequiresRestartCategory, "Command Blocks in creative menu", true, "Can command blocks be obtained in the redstone creative tab?").getBoolean();
		enableFlintAndSteelDispenserBehavior = config.get(miscRequiresRestartCategory, "Enable flint and steel " + "dispenser behavior", false, "Can you use flint and steel with dispensers?").getBoolean();
		config.setCategoryComment(miscCategory, "Miscellaneous settings");
		config.setCategoryComment(miscRequiresRestartCategory, "Settings that require a Minecraft restart");
		config.setCategoryRequiresMcRestart(miscRequiresRestartCategory, true);

		if (config.hasChanged())
			config.save();
	}
}
