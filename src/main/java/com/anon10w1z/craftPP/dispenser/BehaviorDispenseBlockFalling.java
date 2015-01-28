package com.anon10w1z.craftPP.dispenser;

import com.anon10w1z.craftPP.main.CppUtils;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDispenseBlockFalling extends BehaviorDefaultDispenseItem {
	/**
	 * Dispense the specified stack, play the dispense sound and spawn particles.
	 */
	@Override
	protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
		World world = par1IBlockSource.getWorld();
		BlockPos blockPos = par1IBlockSource.getBlockPos();
		EnumFacing facing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
		par2ItemStack.onItemUse(CppUtils.getFakePlayer(world), world, blockPos, facing, 0, 0, 0);
		return par2ItemStack;
	}
}
