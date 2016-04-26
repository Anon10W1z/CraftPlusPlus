package io.github.anon10w1z.cpp.coremod;

import io.github.anon10w1z.cpp.handlers.CppConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

/**
 * Craft++'s coremod hooks
 */
@SuppressWarnings("unused")
public class CppCoremodHooks {
	/**
	 * Schedules a block update if the block is a Craft++ falling block
	 *
	 * @param world    The world
	 * @param blockPos The block position
	 * @param block    The block
	 */
	public static void scheduleBlockUpdate(World world, BlockPos blockPos, Block block) {
		if (isFallingBlock(block))
			world.scheduleUpdate(blockPos, block, block.tickRate(world));
	}

	/**
	 * Called on every update tick on every ticking block in the world (edited to make Craft++'s falling blocks fall)
	 *
	 * @param world    The world
	 * @param blockPos The block position
	 * @param block    The block
	 */
	public static void onUpdateTick(World world, BlockPos blockPos, Block block) {
		if (!world.isRemote && isFallingBlock(block)) {
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
				if (!BlockFalling.fallInstantly && world.isAreaLoaded(blockPos.add(-b, -b, -b), blockPos.add(b, b, b)))
					world.spawnEntityInWorld(new EntityFallingBlock(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, world.getBlockState(blockPos)));
				else {
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

	/**
	 * Returns whether the given cactus block can stay at the given block position in the given world
	 *
	 * @param world    The world
	 * @param blockPos The block position
	 * @param block    The block
	 * @return Whether the given cactus block can stay at the block position in the world
	 */
	public static boolean canCactusStay(World world, BlockPos blockPos, Block block) {
		return world.getBlockState(blockPos.down()).getBlock().canSustainPlant(world, blockPos.down(), EnumFacing.UP, (IPlantable) block);
	}

	/**
	 * Returns whether or not the given block is a Craft++ falling block
	 *
	 * @param block The block
	 * @return Whether or not the block is a Craft++ falling block
	 */
	public static boolean isFallingBlock(Block block) {
		return CppConfigHandler.additionalFallingBlocks.contains(block);
	}
}
