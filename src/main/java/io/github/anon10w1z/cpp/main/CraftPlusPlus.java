package io.github.anon10w1z.cpp.main;

import io.github.anon10w1z.cpp.blocks.CppBlocks;
import io.github.anon10w1z.cpp.capabilities.CapabilitySelfPlanting;
import io.github.anon10w1z.cpp.dispenser.CppDispenserBehaviors;
import io.github.anon10w1z.cpp.enchantments.CppEnchantments;
import io.github.anon10w1z.cpp.entities.CppEntities;
import io.github.anon10w1z.cpp.handlers.CppConfigHandler;
import io.github.anon10w1z.cpp.handlers.CppEventHandler;
import io.github.anon10w1z.cpp.handlers.CppFuelHandler;
import io.github.anon10w1z.cpp.handlers.CppGuiHandler;
import io.github.anon10w1z.cpp.items.CppItems;
import io.github.anon10w1z.cpp.misc.CppVanillaPropertiesChanger;
import io.github.anon10w1z.cpp.proxies.CppCommonProxy;
import io.github.anon10w1z.cpp.recipes.CppRecipeReplacer;
import io.github.anon10w1z.cpp.recipes.CppRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

/**
 * The main mod file of Craft++
 *
 * @author Anon10W1z
 */
@Mod(modid = CppModInfo.MOD_ID, name = CppModInfo.NAME, version = CppModInfo.VERSION, guiFactory = CppModInfo.PACKAGE_LOCATION + ".gui.CppGuiFactory", dependencies = "after:*")
@SuppressWarnings("unused")
public final class CraftPlusPlus {
	/**
	 * The proxy of Craft++
	 */
	@SidedProxy(modId = CppModInfo.MOD_ID, clientSide = CppModInfo.PACKAGE_LOCATION + ".proxies.CppClientProxy", serverSide = CppModInfo.PACKAGE_LOCATION + ".proxies.CppCommonProxy")
	public static CppCommonProxy proxy;

	/**
	 * The mod instance of Craft++
	 */
	@Instance(CppModInfo.MOD_ID)
	public static CraftPlusPlus instance;

	/**
	 * The logger for Craft++
	 */
	private static Logger logger;

	/**
	 * Logs a message with Craft++'s logger with the INFO level
	 *
	 * @param message The string to be logged
	 */
	public static void logInfo(String message) {
		logger.info("Craft++: " + message);
	}

	/**
	 * Performs the following actions: <br>
	 * - Initializes the logger <br>
	 * - Hard-codes the mcmod.info <br>
	 * - Initializes the config handler <br>
	 * - Enables the Version Checker support <br>
	 * - Registers the blocks and items
	 *
	 * @param event The FMLPreInitializationEvent
	 */
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		logInfo("Initialized the logger");
		logInfo("Hard-coding the mcmod.info");
		ModMetadata modMetadata = event.getModMetadata();
		modMetadata.modId = CppModInfo.MOD_ID;
		modMetadata.name = CppModInfo.NAME;
		modMetadata.version = CppModInfo.VERSION;
		modMetadata.description = "A simple vanilla-enhancing mod";
		modMetadata.authorList = Collections.singletonList("Anon10W1z");
		modMetadata.url = "http://goo.gl/fiVx7H";
		logInfo("Enabling the Version Checker Support");
		NBTTagCompound versionCheckerTagCompound = new NBTTagCompound();
		versionCheckerTagCompound.setString("curseProjectName", "235061-craft-by-anon10w1z");
		versionCheckerTagCompound.setString("curseFilenameParser", "craft++-[]");
		versionCheckerTagCompound.setString("modDisplayName", "Craft++");
		FMLInterModComms.sendRuntimeMessage(CppModInfo.MOD_ID, "VersionChecker", "addCurseCheck", versionCheckerTagCompound);
		logInfo("Initializing the config handler");
		CppConfigHandler.init(event.getSuggestedConfigurationFile());
		logInfo("Registering the blocks");
		CppBlocks.registerBlocks();
		logInfo("Registering the items");
		CppItems.registerItems();
		logInfo("Pre-initialization completed successfully");
	}

	/**
	 * Performs the following actions: <br>
	 * - Registers the entities <br>
	 * - Registers the renderers <br>
	 * - Registers the GUI handler <br>
	 * - Registers the enchantments <br>
	 * - Registers the event handler <br>
	 * - Registers the key bindings <br>
	 * - Registers the fuel handler <br>
	 * - Registers the crafting recipes <br>
	 * - Registers the dispenser behaviors <br>
	 * - Registers the self-planting <br>
	 * - Initializes the vanilla properties changer <br>
	 * - Initializes the recipe remover
	 *
	 * @param event The FMLInitializationEvent
	 */
	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		logInfo("Registering the entities");
		CppEntities.registerEntities(this);
		logInfo("Registering the renderers");
		proxy.registerRenderers();
		logInfo("Registering the GUI handler");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new CppGuiHandler());
		logInfo("Registering the enchantments");
		CppEnchantments.registerEnchantments();
		logInfo("Registering the event handler");
		MinecraftForge.EVENT_BUS.register(CppEventHandler.instance);
		logInfo("Registering the fuel handler");
		GameRegistry.registerFuelHandler(new CppFuelHandler());
		logInfo("Registering the crafting/furnace recipes");
		CppRecipes.registerRecipes();
		logInfo("Registering the dispenser behaviors");
		CppDispenserBehaviors.registerDispenserBehaviors();
		logInfo("Registering the self-planting");
		CapabilitySelfPlanting.register();
		logInfo("Initializing the vanilla properties changer");
		CppVanillaPropertiesChanger.init();
		logInfo("Initializing the crafting recipe remover");
		CppRecipeReplacer.replaceRecipes();
		logInfo("Initialization completed successfully");
	}
}
