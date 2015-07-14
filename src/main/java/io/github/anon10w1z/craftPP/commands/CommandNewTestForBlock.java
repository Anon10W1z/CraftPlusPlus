package io.github.anon10w1z.craftPP.commands;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

/**
 * Adds the ability to test for blocks by ID
 */
public class CommandNewTestForBlock extends CommandTestForBlock {
	@Override
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
