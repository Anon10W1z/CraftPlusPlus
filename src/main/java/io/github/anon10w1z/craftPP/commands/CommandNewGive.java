package io.github.anon10w1z.craftPP.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandGive;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

/**
 * Adds the ability to give items by ID
 */
public class CommandNewGive extends CommandGive {
	@Override
	public void execute(ICommandSender commandSender, String[] arguments) throws CommandException {
		if (arguments.length >= 2)
			try {
				UniqueIdentifier itemIdentifier = GameRegistry.findUniqueIdentifierFor(Item.getItemById(Integer.parseInt(arguments[1])));
				if (itemIdentifier != null)
					arguments[1] = itemIdentifier.name;
			} catch (NumberFormatException ignored) {

			}
		super.execute(commandSender, arguments);
	}
}
