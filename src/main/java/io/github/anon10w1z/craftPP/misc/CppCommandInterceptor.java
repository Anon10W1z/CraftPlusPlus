package io.github.anon10w1z.craftPP.misc;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Allows you to use numerical IDs as an alternative to names in commands (for blocks and items)
 */
@SuppressWarnings("unused")
public class CppCommandInterceptor {
	public static Item getItemByText(ICommandSender commandSender, String text) throws CommandException {
		Item item = Item.getByNameOrId(text);
		if (item == null)
			throw new NumberInvalidException("commands.give.notFound", new ResourceLocation(text));
		return item;
	}

	public static Block getBlockByText(ICommandSender commandSender, String text) throws CommandException {
		Block block = Block.getBlockFromName(text);
		if (block == null)
			try {
				block = Block.getBlockById(Integer.parseInt(text));
			} catch (NumberFormatException ignored) {

			}
		if (block == null)
			throw new NumberInvalidException("commands.give.notFound", new ResourceLocation(text));
		return block;
	}
}
