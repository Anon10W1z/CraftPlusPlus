package io.github.anon10w1z.cpp.handlers;

import io.github.anon10w1z.cpp.blocks.CppBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

/**
 * The fuel handler for Craft++
 */
public class CppFuelHandler implements IFuelHandler {
	@Override
	public int getBurnTime(ItemStack fuel) {
		return fuel.getItem() == Item.getItemFromBlock(CppBlocks.charcoal_block) ? 16000 : 0;
	}
}
