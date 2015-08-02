package io.github.anon10w1z.cpp.entities;

import io.github.anon10w1z.cpp.items.CppItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Obsidian boat entity to go along-side obsidian boat item
 */
public class EntityObsidianBoat extends Entity {
	private boolean isBoatEmpty;
	private double speedMultiplier;
	private int boatPosRotationIncrements;
	private double boatX;
	private double boatY;
	private double boatZ;
	private double boatYaw;
	private double boatPitch;
	@SideOnly(Side.CLIENT)
	private double velocityX;
	@SideOnly(Side.CLIENT)
	private double velocityY;
	@SideOnly(Side.CLIENT)
	private double velocityZ;

	public EntityObsidianBoat(World world) {
		super(world);
		this.isBoatEmpty = true;
		this.speedMultiplier = 0.07;
		this.preventEntitySpawning = true;
		this.setSize(1.5F, 0.6F);
		this.isImmuneToFire = true;
	}

	public EntityObsidianBoat(World world, double x, double y, double z) {
		this(world);
		this.setPosition(x, y, z);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(17, 0);
		this.dataWatcher.addObject(18, 1);
		this.dataWatcher.addObject(19, 0F);
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public double getMountedYOffset() {
		return 0.6;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source))
			return false;
		if (!this.worldObj.isRemote && !this.isDead) {
			if (this.riddenByEntity != null && this.riddenByEntity == source.getEntity() && source instanceof EntityDamageSourceIndirect)
				return false;
			this.setForwardDirection(-this.getForwardDirection());
			this.setTimeSinceHit(10);
			this.setDamageTaken(this.getDamageTaken() + amount * 10);
			this.setBeenAttacked();
			boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;

			if (flag || this.getDamageTaken() > 40) {
				if (this.riddenByEntity != null)
					this.riddenByEntity.mountEntity(this);
				if (!flag)
					this.dropItem(CppItems.obsidian_boat, 1);
				this.setDead();
			}
			return true;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void performHurtAnimation() {
		this.setForwardDirection(-this.getForwardDirection());
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11);
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void func_180426_a(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
		if (p_180426_10_ && this.riddenByEntity != null) {
			this.prevPosX = this.posX = p_180426_1_;
			this.prevPosY = this.posY = p_180426_3_;
			this.prevPosZ = this.posZ = p_180426_5_;
			this.rotationYaw = p_180426_7_;
			this.rotationPitch = p_180426_8_;
			this.boatPosRotationIncrements = 0;
			this.setPosition(p_180426_1_, p_180426_3_, p_180426_5_);
			this.motionX = this.velocityX = 0;
			this.motionY = this.velocityY = 0;
			this.motionZ = this.velocityZ = 0;
		} else {
			if (this.isBoatEmpty)
				this.boatPosRotationIncrements = p_180426_9_ + 5;
			else {
				double d3 = p_180426_1_ - this.posX;
				double d4 = p_180426_3_ - this.posY;
				double d5 = p_180426_5_ - this.posZ;
				double d6 = d3 * d3 + d4 * d4 + d5 * d5;
				if (d6 <= 1.0D)
					return;
				this.boatPosRotationIncrements = 3;
			}
			this.boatX = p_180426_1_;
			this.boatY = p_180426_3_;
			this.boatZ = p_180426_5_;
			this.boatYaw = (double) p_180426_7_;
			this.boatPitch = (double) p_180426_8_;
			this.motionX = this.velocityX;
			this.motionY = this.velocityY;
			this.motionZ = this.velocityZ;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z) {
		this.velocityX = this.motionX = x;
		this.velocityY = this.motionY = y;
		this.velocityZ = this.motionZ = z;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onUpdate() {
		super.onUpdate();
		if (this.getTimeSinceHit() > 0)
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		if (this.getDamageTaken() > 0)
			this.setDamageTaken(this.getDamageTaken() - 1);
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		byte b0 = 5;
		double d0 = 0.0D;
		for (int i = 0; i < b0; ++i) {
			double d1 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double) (i) / (double) b0 - 0.125;
			double d3 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double) (i + 1) / (double) b0 - 0.125;
			AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.getEntityBoundingBox().minX, d1, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, d3, this.getEntityBoundingBox().maxZ);
			if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.lava))
				d0 += 1.0D / (double) b0;
		}
		double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		double d2;
		double d4;
		int j;
		if (d9 > 0.2975) {
			d2 = Math.cos((double) this.rotationYaw * Math.PI / 180.0D);
			d4 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D);
			for (j = 0; j < 1.0D + d9 * 60.0D; ++j) {
				double d5 = (double) (this.rand.nextFloat() * 2 - 1);
				double d6 = (double) (this.rand.nextInt(2) * 2 - 1) * 0.7;
				double d7;
				double d8;
				if (this.rand.nextBoolean()) {
					d7 = this.posX - d2 * d5 * 0.8 + d4 * d6;
					d8 = this.posZ - d4 * d5 * 0.8 - d2 * d6;
					this.worldObj.spawnParticle(EnumParticleTypes.DRIP_LAVA, d7, this.posY - 0.125, d8, this.motionX, this.motionY, this.motionZ);
				} else {
					d7 = this.posX + d2 + d4 * d5 * 0.7;
					d8 = this.posZ + d4 - d2 * d5 * 0.7;
					this.worldObj.spawnParticle(EnumParticleTypes.DRIP_LAVA, d7, this.posY - 0.125, d8, this.motionX, this.motionY, this.motionZ);
				}
			}
		}
		double d10;
		double d11;
		if (this.worldObj.isRemote && this.isBoatEmpty) {
			if (this.boatPosRotationIncrements > 0) {
				d2 = this.posX + (this.boatX - this.posX) / (double) this.boatPosRotationIncrements;
				d4 = this.posY + (this.boatY - this.posY) / (double) this.boatPosRotationIncrements;
				d10 = this.posZ + (this.boatZ - this.posZ) / (double) this.boatPosRotationIncrements;
				d11 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double) this.rotationYaw);
				this.rotationYaw = (float) ((double) this.rotationYaw + d11 / (double) this.boatPosRotationIncrements);
				this.rotationPitch = (float) ((double) this.rotationPitch + (this.boatPitch - (double) this.rotationPitch) / (double) this.boatPosRotationIncrements);
				--this.boatPosRotationIncrements;
				this.setPosition(d2, d4, d10);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				d2 = this.posX + this.motionX;
				d4 = this.posY + this.motionY;
				d10 = this.posZ + this.motionZ;
				this.setPosition(d2, d4, d10);
				if (this.onGround) {
					this.motionX *= 0.5;
					this.motionY *= 0.5;
					this.motionZ *= 0.5;
				}
				this.motionX *= 0.9900000095367432;
				this.motionY *= 0.949999988079071;
				this.motionZ *= 0.9900000095367432;
			}
		} else {
			if (d0 < 1.0D) {
				d2 = d0 * 2.0D - 1.0D;
				this.motionY += 0.03999999910593033 * d2;
			} else {
				if (this.motionY < 0.0D)
					this.motionY /= 2.0D;
				this.motionY += 0.007000000216066837;
			}
			if (this.riddenByEntity instanceof EntityLivingBase) {
				EntityLivingBase livingEntity = (EntityLivingBase) this.riddenByEntity;
				float f = this.riddenByEntity.rotationYaw + -livingEntity.moveStrafing * 90;
				this.motionX += -Math.sin((double) (f * (float) Math.PI / 180)) * this.speedMultiplier * (double) livingEntity.moveForward * 0.05000000074505806;
				this.motionZ += Math.cos((double) (f * (float) Math.PI / 180)) * this.speedMultiplier * (double) livingEntity.moveForward * 0.05000000074505806;
			}
			d2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if (d2 > 0.35) {
				d4 = 0.35 / d2;
				this.motionX *= d4;
				this.motionZ *= d4;
				d2 = 0.35;
			}
			if (d2 > d9 && this.speedMultiplier < 0.35) {
				this.speedMultiplier += (0.35 - this.speedMultiplier) / 35.0D;
				if (this.speedMultiplier > 0.35)
					this.speedMultiplier = 0.35;
			} else {
				this.speedMultiplier -= (this.speedMultiplier - 0.07) / 35.0D;

				if (this.speedMultiplier < 0.07)
					this.speedMultiplier = 0.07;
			}
			int l;
			for (l = 0; l < 4; ++l) {
				int i1 = MathHelper.floor_double(this.posX + ((double) (l % 2) - 0.5) * 0.8);
				j = MathHelper.floor_double(this.posZ + ((double) (l / 2) - 0.5) * 0.8);
				for (int j1 = 0; j1 < 2; ++j1) {
					int k = MathHelper.floor_double(this.posY) + j1;
					BlockPos blockpos = new BlockPos(i1, k, j);
					Block block = this.worldObj.getBlockState(blockpos).getBlock();
					if (block == Blocks.snow_layer) {
						this.worldObj.setBlockToAir(blockpos);
						this.isCollidedHorizontally = false;
					} else if (block == Blocks.waterlily) {
						this.worldObj.destroyBlock(blockpos, true);
						this.isCollidedHorizontally = false;
					}
				}
			}
			if (this.onGround) {
				this.motionX *= 0.5;
				this.motionY *= 0.5;
				this.motionZ *= 0.5;
			}
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			if (this.isCollidedHorizontally && d9 > 2) {
				if (!this.worldObj.isRemote && !this.isDead) {
					this.setDead();
					this.dropItem(CppItems.obsidian_boat, 1);
				}
			} else {
				this.motionX *= 0.9900000095367432;
				this.motionY *= 0.949999988079071;
				this.motionZ *= 0.9900000095367432;
			}
			this.rotationPitch = 0;
			d4 = (double) this.rotationYaw;
			d10 = this.prevPosX - this.posX;
			d11 = this.prevPosZ - this.posZ;
			if (d10 * d10 + d11 * d11 > 0.001)
				d4 = (double) ((float) (Math.atan2(d11, d10) * 180.0D / Math.PI));
			double d12 = Math.max(-20, Math.min(MathHelper.wrapAngleTo180_double(d4 - (double) this.rotationYaw), 20));
			if (d12 > 20.0D)
				d12 = 20.0D;
			if (d12 < -20.0D)
				d12 = -20.0D;
			this.rotationYaw = (float) ((double) this.rotationYaw + d12);
			this.setRotation(this.rotationYaw, this.rotationPitch);
			if (!this.worldObj.isRemote) {
				List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224, 0, 0.20000000298023224));
				entities.stream().filter(entity -> entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityObsidianBoat).forEach(entity -> entity.applyEntityCollision(this));
				if (this.riddenByEntity != null && this.riddenByEntity.isDead)
					this.riddenByEntity = null;
			}
		}
	}

	@Override
	public void updateRiderPosition() {
		if (this.riddenByEntity != null) {
			double d0 = Math.cos((double) this.rotationYaw * Math.PI / 180.0D) * 0.4;
			double d1 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D) * 0.4;
			this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
			return true;
		if (!this.worldObj.isRemote)
			player.mountEntity(this);
		return true;
	}

	@Override
	protected void func_180433_a(double p_180433_1_, boolean p_180433_3_, Block p_180433_4_, BlockPos p_180433_5_) {
		if (p_180433_3_) {
			if (this.fallDistance > 60) {
				this.fall(this.fallDistance, 1);
				if (!this.worldObj.isRemote && !this.isDead) {
					this.setDead();
					this.dropItem(CppItems.obsidian_boat, 1);
				}
				this.fallDistance = 0;
			}
		} else if (this.worldObj.getBlockState((new BlockPos(this)).down()).getBlock().getMaterial() != Material.lava && p_180433_1_ < 0.0D)
			this.fallDistance = (float) ((double) this.fallDistance - p_180433_1_);
	}

	public float getDamageTaken() {
		return this.dataWatcher.getWatchableObjectFloat(19);
	}

	public void setDamageTaken(float damageTaken) {
		this.dataWatcher.updateObject(19, damageTaken);
	}

	public int getTimeSinceHit() {
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	public void setTimeSinceHit(int timeSinceHit) {
		this.dataWatcher.updateObject(17, timeSinceHit);
	}

	public int getForwardDirection() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	public void setForwardDirection(int forwardDirection) {
		this.dataWatcher.updateObject(18, forwardDirection);
	}
}