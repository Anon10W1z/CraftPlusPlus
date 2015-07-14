package io.github.anon10w1z.craftPP.commands;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.common.CommandReplaceItem;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Adds the ability to replace items by ID
 */
public class CommandNewReplaceItem extends CommandReplaceItem {
	@Override
	public void execute(ICommandSender commandSender, String[] arguments) throws CommandException {
		if (arguments.length >= 2)
			try {
				GameRegistry.UniqueIdentifier blockIdentifier = GameRegistry.findUniqueIdentifierFor(Block.getBlockById(Integer.parseInt(arguments[1])));
				if (blockIdentifier != null)
					arguments[1] = blockIdentifier.name;
			} catch (NumberFormatException ignored) {

			}
		super.execute(commandSender, arguments);
	}
}
