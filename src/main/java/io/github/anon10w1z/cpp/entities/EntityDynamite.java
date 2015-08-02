package io.github.anon10w1z.cpp.entities;

import io.github.anon10w1z.cpp.items.CppItems;
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
	 * Datawatcher ID for ticks wet
	 */
	private static final int TICKS_WET_ID = 5;
	/**
	 * Datawatcher ID for ticks since wet
	 */
	private static final int TICKS_SINCE_WET_ID = 6;

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
	protected void entityInit() {
		this.dataWatcher.addObject(TICKS_WET_ID, 0);
		this.dataWatcher.addObject(TICKS_SINCE_WET_ID, WET_TICKS);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!this.worldObj.isRemote) {
			if (this.isWet()) {
				this.setTicksWet(this.getTicksWet() + 1);
			} else
				this.setTicksWet(0);
			if (this.getTicksWet() == 0)
				this.setTicksSinceWet(this.getTicksSinceWet() + 1);
			else
				this.setTicksSinceWet(0);
		}
		if (this.getTicksSinceWet() < WET_TICKS && !this.isInWater())
			for (int i = 0; i < 3; ++i) {
				float xOffset = (this.rand.nextFloat() * 2 - 1) * this.width * 0.5F;
				float zOffset = (this.rand.nextFloat() * 2 - 1) * this.width * 0.5F;
				this.worldObj.spawnParticle(EnumParticleTypes.DRIP_WATER, this.posX + xOffset, this.posY, this.posZ + zOffset, this.motionX, this.motionY, this.motionZ);
			}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingObjectPosition) {
		World world = this.worldObj;
		if (!world.isRemote)
			if (this.getTicksSinceWet() < WET_TICKS) {
				if (isNotCreativeThrower())
					this.dropItem(CppItems.dynamite, 1);
			} else
				world.createExplosion(this, this.posX, this.posY, this.posZ, 2F, true);
		this.setDead();
	}

	/**
	 * Gets the number of ticks wet
	 *
	 * @return The number of ticks wet
	 */
	private int getTicksWet() {
		return this.dataWatcher.getWatchableObjectInt(TICKS_WET_ID);
	}

	/**
	 * Sets the number of ticks wet
	 *
	 * @param ticksWet The number of ticks wet
	 */
	private void setTicksWet(int ticksWet) {
		this.dataWatcher.updateObject(TICKS_WET_ID, ticksWet);
	}

	/**
	 * Gets the number of ticks since wet
	 *
	 * @return The number of ticks since wet
	 */
	private int getTicksSinceWet() {
		return this.dataWatcher.getWatchableObjectInt(TICKS_SINCE_WET_ID);
	}

	/**
	 * Sets the number of ticks since wet
	 *
	 * @param ticksSinceWet The number of ticks since wet
	 */
	private void setTicksSinceWet(int ticksSinceWet) {
		this.dataWatcher.updateObject(TICKS_SINCE_WET_ID, ticksSinceWet);
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
