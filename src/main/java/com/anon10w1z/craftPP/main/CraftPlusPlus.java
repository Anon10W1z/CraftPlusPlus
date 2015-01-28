package com.anon10w1z.craftPP.main;

import com.anon10w1z.craftPP.blocks.CppBlocks;
import com.anon10w1z.craftPP.dispenser.CppDispenserBehaviors;
import com.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.anon10w1z.craftPP.handlers.CppEventHandler;
import com.anon10w1z.craftPP.handlers.CppFuelHandler;
import com.anon10w1z.craftPP.proxies.CppCommonProxy;
import com.anon10w1z.craftPP.recipes.CppRecipeReplacer;
import com.anon10w1z.craftPP.recipes.CppRecipes;
import com.google.common.collect.Lists;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * The main mod file of Craft++
 *
 * @author Anon10W1z
 */
@SuppressWarnings("unused")
@Mod(modid = CppReferences.MOD_ID, name = CppReferences.NAME, version = CppReferences.VERSION, guiFactory = CppReferences.PACKAGE_LOCATION + "." + "gui.CppGuiFactory")
public final class CraftPlusPlus {
	/**
	 * The proxy of CraftPlusPlus
	 */
	@SidedProxy(modId = CppReferences.MOD_ID, clientSide = CppReferences.PACKAGE_LOCATION + "." + "proxies.CppClientProxy", serverSide = CppReferences.PACKAGE_LOCATION + "." + "proxies.CppCommonProxy")
	private static CppCommonProxy proxy = new CppCommonProxy();

	/**
	 * The mod instance of CraftPlusPlus
	 */
	@Instance(CppReferences.MOD_ID)
	private static CraftPlusPlus instance;

	/**
	 * The logger for Craft++
	 */
	private Logger logger = LogManager.getLogger("Craft++");

	/**
	 * Logs a message with Craft++'s logger with the INFO level.
	 *
	 * @param message The string to be logged
	 */
	public static void logInfo(String message) {
		instance.logger.info(message);
	}

	/**
	 * Logs a message with Craft++'s logger with the ERROR level.
	 *
	 * @param message The string to be logged
	 */
	public static void logError(String message) {
		instance.logger.error(message);
	}

	/**
	 * Performs the following actions: <br>
	 * - Hard-codes the mcmod.info <br>
	 * - Initializes the config handler <br>
	 * - Enables the Version Checker support <br>
	 * - Registers the blocks
	 *
	 * @param event The FMLPreInitializationEvent
	 */
	@EventHandler
	@SuppressWarnings("unchecked")
	public void onPreInit(FMLPreInitializationEvent event) {
		logInfo("Hard-coding the mcmod.info");
		ModMetadata modMetadata = event.getModMetadata();
		modMetadata.modId = CppReferences.MOD_ID;
		modMetadata.name = CppReferences.NAME;
		modMetadata.version = CppReferences.VERSION;
		modMetadata.description = "A simple vanilla-enhancing mod.";
		ArrayList<String> authorList = Lists.newArrayList();
		authorList.add("Anon10W1z");
		modMetadata.authorList = authorList;
		logInfo("Initializing the Version Checker Support");
		FMLInterModComms.sendRuntimeMessage(CppReferences.MOD_ID, "VersionChecker", "addVersionCheck", "https://dl.dropboxusercontent.com/u/76347756/VersionCheck.json");
		logInfo("Initializing the config handler");
		CppConfigHandler.init(event);
		logInfo("Registering the blocks");
		CppBlocks.registerBlocks();
		logInfo("Pre-initialization completed successfully");
	}

	/**
	 * Performs the following actions: <br>
	 * - Registers the block inventory renderers <br>
	 * - Registers the event handler <br>
	 * - Registers the fuel handler <br>
	 * - Registers the crafting recipes <br>
	 * - Registers the dispenser behaviors <br>
	 * - Initializes the vanilla properties changer <br>
	 * - Initializes the recipe remover
	 *
	 * @param event The FMLInitializationEvent
	 */
	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		logInfo("Registering the block inventory renderers");
		proxy.registerBlockInventoryRenderers();
		logInfo("Registering the event handler");
		CppEventHandler eventHandler = new CppEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		FMLCommonHandler.instance().bus().register(eventHandler);
		GameRegistry.registerFuelHandler(new CppFuelHandler());
		logInfo("Registering the crafting/furnace recipes");
		CppRecipes.registerRecipes();
		logInfo("Registering the dispenser behaviors");
		CppDispenserBehaviors.registerDispenserBehaviors();
		logInfo("Initializing the vanilla properties changer");
		CppVanillaPropertiesChanger.init();
		logInfo("Initializing the crafting recipe remover");
		CppRecipeReplacer.replaceRecipes();
		logInfo("Initialization completed successfully");
	}
}
