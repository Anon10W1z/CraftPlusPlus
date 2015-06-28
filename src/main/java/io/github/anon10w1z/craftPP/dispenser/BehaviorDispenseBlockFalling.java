package io.github.anon10w1z.craftPP.dispenser;

import io.github.anon10w1z.craftPP.misc.CppUtils;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Dispenser behavior for falling blocks
 */
public class BehaviorDispenseBlockFalling extends BehaviorDefaultDispenseItem {
	@Override
	protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack) {
		World world = blockSource.getWorld();
		EnumFacing facing = BlockDispenser.getFacing(blockSource.getBlockMetadata());
		itemstack.onItemUse(CppUtils.getFakePlayer(world), world, blockSource.getBlockPos(), facing, 0, 0, 0);
		return itemstack;
	}
}
