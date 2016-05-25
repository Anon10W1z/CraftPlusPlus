package io.github.anon10w1z.cpp.dispenser;

import io.github.anon10w1z.cpp.entities.EntityDynamite;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Dispenser behavior for dynamite
 */
public class BehaviorDispenseDynamite extends BehaviorProjectileDispense {
	

	@Override
	protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
		return new EntityDynamite(world, position.getX(), position.getY(), position.getZ());
	}
}
