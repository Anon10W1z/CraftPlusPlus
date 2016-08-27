package io.github.anon10w1z.cpp.dispenser;

import io.github.anon10w1z.cpp.main.CppUtils;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Dispenser behavior for falling blocks
 */
public class BehaviorDispenseBlockFalling extends BehaviorDefaultDispenseItem {
	@Override
	protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack) {
		World world = blockSource.getWorld();
		EnumFacing facing = blockSource.func_189992_e().getValue(BlockDispenser.FACING);
		itemstack.onItemUse(CppUtils.getFakePlayer(world), world, blockSource.getBlockPos(), EnumHand.MAIN_HAND, facing, 0, 0, 0);
		return itemstack;
	}
}
