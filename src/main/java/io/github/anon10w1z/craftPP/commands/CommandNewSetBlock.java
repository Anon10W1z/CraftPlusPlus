package io.github.anon10w1z.craftPP.commands;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

/**
 * Adds the ability to set blocks by block ID
 */
public class CommandNewSetBlock extends CommandSetBlock {
	public void execute(ICommandSender commandSender, String[] arguments) throws CommandException {
		if (arguments.length >= 4)
			try {
				UniqueIdentifier blockIdentifier = GameRegistry.findUniqueIdentifierFor(Block.getBlockById(Integer.parseInt(arguments[3])));
				if (blockIdentifier != null)
					arguments[3] = blockIdentifier.name;
			} catch (NumberFormatException ignored) {

			}
		super.execute(commandSender, arguments);
	}
}

