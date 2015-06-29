package io.github.anon10w1z.craftPP.entities;

import io.github.anon10w1z.craftPP.items.CppItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Dynamite entity to go along-side dynamite item
 */
public class EntityDynamite extends EntityThrowable {
	/**
	 * Number of ticks required for this dynamite to dry off
	 */
	private static final int WET_TICKS = 20;
	/**
	 * Number of ticks this dynamite has been in water
	 */
	private int ticksWet = 0;
	/**
	 * Number of ticks it has been since this dynamite has been in water
	 */
	private int ticksSinceWet = WET_TICKS;

	@SuppressWarnings("unused")
	public EntityDynamite(World world) {
		super(world);
	}

	public EntityDynamite(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityDynamite(World world, EntityLivingBase entityLivingBase) {
		super(world, entityLivingBase);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.isWet()) {
			++this.ticksWet;
		} else
			this.ticksWet = 0;
		if (this.ticksWet == 0)
			++this.ticksSinceWet;
		else
			this.ticksSinceWet = 0;
		if (this.ticksSinceWet < WET_TICKS)
			for (int i = 0; i < 6; ++i) {
				float xOffset = (this.rand.nextFloat() * 2 - 1) * this.width * 0.5F;
				float zOffset = (this.rand.nextFloat() * 2 - 1) * this.width * 0.5F;
				this.worldObj.spawnParticle(EnumParticleTypes.DRIP_WATER, this.posX + xOffset, this.posY, this.posZ + zOffset, this.motionX, this.motionY, this.motionZ);
			}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingObjectPosition) {
		World world = this.worldObj;
		if (!world.isRemote)
			if (this.ticksSinceWet < WET_TICKS) {
				if (isNotCreativeThrower())
					this.dropItem(CppItems.dynamite, 1);
			} else
				world.createExplosion(this.getThrower(), this.posX, this.posY, this.posZ, 2.0F, true);
		this.setDead();
	}

	/**
	 * Returns whether or not this dynamite's thrower is in creative mode
	 *
	 * @return If this dynamite's thrower is in creative mode
	 */
	private boolean isNotCreativeThrower() {
		EntityLivingBase thrower = this.getThrower();
		return !(thrower instanceof EntityPlayer) || !((EntityPlayer) thrower).capabilities.isCreativeMode;
	}
}
