package io.github.anon10w1z.craftPP.dispenser;

import io.github.anon10w1z.craftPP.entities.EntityObsidianBoat;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDispenseObsidianBoat extends BehaviorDefaultDispenseItem {
	@Override
	public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
		World world = source.getWorld();
		double d0 = source.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 1.125F);
		double d1 = source.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 1.125F);
		double d2 = source.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 1.125F);
		BlockPos blockpos = source.getBlockPos().offset(enumfacing);
		Material material = world.getBlockState(blockpos).getBlock().getMaterial();
		int d3;
		if (Material.lava.equals(material))
			d3 = 1;
		else {
			if (!Material.air.equals(material) || !Material.lava.equals(world.getBlockState(blockpos.down()).getBlock().getMaterial()))
				return new BehaviorDispenseBlockFalling().dispense(source, stack);
			d3 = 0;
		}
		EntityObsidianBoat obsidianBoat = new EntityObsidianBoat(world, d0, d1 + d3, d2);
		world.spawnEntityInWorld(obsidianBoat);
		stack.splitStack(1);
		return stack;
	}

	@Override
	protected void playDispenseSound(IBlockSource source) {
		source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
	}
}
