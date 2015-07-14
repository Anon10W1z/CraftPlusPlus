package io.github.anon10w1z.craftPP.commands;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Registers the commands for Craft++
 */
public class CppCommands {
	/**
	 * Registers the commands for Craft++
	 *
	 * @param event The FMLServerStartingEvent
	 */
	public static void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandNewGive());
		event.registerServerCommand(new CommandNewReplaceItem());
		event.registerServerCommand(new CommandNewSetBlock());
		event.registerServerCommand(new CommandNewFill());
		event.registerServerCommand(new CommandNewTestForBlock());
	}
}
