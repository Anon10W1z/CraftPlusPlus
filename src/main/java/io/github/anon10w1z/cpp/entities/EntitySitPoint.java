package io.github.anon10w1z.cpp.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Dummy entity mounted on by the player for it to sit on stairs
 */
public class EntitySitPoint extends Entity {
	public BlockPos blockPos;

	@SuppressWarnings("unused")
	public EntitySitPoint(World world) {
		this(world, BlockPos.ORIGIN);
	}

	public EntitySitPoint(World world, BlockPos blockPos) {
		super(world);
		this.blockPos = blockPos;
		double x = blockPos.getX() + 0.5;
		double y = blockPos.getY();
		double z = blockPos.getZ() + 0.5;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		double d = this.width / 2;
		double d1 = this.height / 2;
		this.setEntityBoundingBox(new AxisAlignedBB(x - d, y, z - d, x + d, y + d1, z + d));
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {

	}

	@Override
	public void setPosition(double x, double y, double z) {

	}

	@Override
	public void onEntityUpdate() {
		if (this.getRidingEntity() == null)
			this.setDead();
	}

	@Override
	protected boolean shouldSetPosAfterLoading() {
		return false;
	}
}
