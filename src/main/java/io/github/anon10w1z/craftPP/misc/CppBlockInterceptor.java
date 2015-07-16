package io.github.anon10w1z.craftPP.misc;

import io.github.anon10w1z.craftPP.handlers.CppConfigHandler;
import net.bytebuddy.implementation.bind.annotation.This;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Makes Craft++'s falling blocks fall
 */
@SuppressWarnings("unused")
public class CppBlockInterceptor {
	public static void onBlockAdded(World world, BlockPos blockPos, IBlockState blockState, @This Block block) {
		if (CppConfigHandler.additionalFallingBlocks.contains(block))
			world.scheduleUpdate(blockPos, block, block.tickRate(world));
	}

	public static void onNeighborBlockChange(World world, BlockPos blockPos, IBlockState blockState, Block neighborBlock, @This Block block) {
		if (CppConfigHandler.additionalFallingBlocks.contains(block))
			world.scheduleUpdate(blockPos, block, block.tickRate(world));
	}

	public static void updateTick(World world, BlockPos blockPos, IBlockState blockState, Random random, @This Block block) {
		if (!world.isRemote && CppConfigHandler.additionalFallingBlocks.contains(block)) {
			boolean canFallInto;
			if (world.isAirBlock(blockPos.down()))
				canFallInto = true;
			else {
				Block block1 = world.getBlockState(blockPos.down()).getBlock();
				Material material = block1.getMaterial();
				canFallInto = block1 == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
			}
			if (canFallInto && blockPos.getY() >= 0) {
				byte b = 32;
				if (!BlockFalling.fallInstantly && world.isAreaLoaded(blockPos.add(-b, -b, -b), blockPos.add(b, b, b))) {
					EntityFallingBlock fallingBlock = new EntityFallingBlock(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, world.getBlockState(blockPos));
					world.spawnEntityInWorld(fallingBlock);
				} else {
					world.setBlockToAir(blockPos);
					BlockPos blockPos1;
					blockPos1 = blockPos.down();
					while (canFallInto && blockPos1.getY() > 0) {
						blockPos1 = blockPos1.down();
						if (!world.isAirBlock(blockPos1)) {
							Block block1 = world.getBlockState(blockPos).getBlock();
							Material material = block1.getMaterial();
							canFallInto = block1 == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
						}
					}
					if (blockPos1.getY() > 0)
						world.setBlockState(blockPos1.up(), block.getDefaultState());
				}
			}
		}
	}

	public static int tickRate(World world, @This Block block) {
		if (CppConfigHandler.additionalFallingBlocks.contains(block))
			return 2;
		return 10;
	}
}
