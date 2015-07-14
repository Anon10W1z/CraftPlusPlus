package io.github.anon10w1z.craftPP.commands;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandFill;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

/**
 * Adds the ability to fill an area of blocks by ID
 */
public class CommandNewFill extends CommandFill {
	@Override
	public void execute(ICommandSender commandSender, String[] arguments) throws CommandException {
		if (arguments.length >= 7)
			try {
				UniqueIdentifier blockIdentifier = GameRegistry.findUniqueIdentifierFor(Block.getBlockById(Integer.parseInt(arguments[6])));
				if (blockIdentifier != null)
					arguments[6] = blockIdentifier.name;
			} catch (NumberFormatException ignored) {

			}
		if (arguments.length >= 10)
			try {
				UniqueIdentifier blockIdentifier = GameRegistry.findUniqueIdentifierFor(Block.getBlockById(Integer.parseInt(arguments[9])));
				if (blockIdentifier != null)
					arguments[9] = blockIdentifier.name;
			} catch (NumberFormatException ignored) {

			}
		super.execute(commandSender, arguments);
	}
}
