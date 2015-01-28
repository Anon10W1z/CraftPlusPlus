package com.anon10w1z.craftPP.handlers;

import com.anon10w1z.craftPP.blocks.CppBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CppFuelHandler implements IFuelHandler {
	@Override
	public int getBurnTime(ItemStack fuel) {
		return fuel.getItem() == Item.getItemFromBlock(CppBlocks.charcoal_block) ? GameRegistry.getFuelValue(new ItemStack(Blocks.coal_block)) : 0;
	}
}
