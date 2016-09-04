package io.github.anon10w1z.cpp.entities;

import com.google.common.collect.Lists;
import io.github.anon10w1z.cpp.items.CppItems;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SuppressWarnings("unchecked")
public class EntityObsidianBoat extends EntityBoat {
	private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityObsidianBoat.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(EntityObsidianBoat.class, DataSerializers.VARINT);
	private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityObsidianBoat.class, DataSerializers.FLOAT);
	private static final DataParameter[] field_184468_e = new DataParameter[]{EntityDataManager.createKey(EntityObsidianBoat.class, DataSerializers.BOOLEAN), EntityDataManager.createKey(EntityObsidianBoat.class, DataSerializers.BOOLEAN)};
	private float[] field_184470_f;
	private float field_184474_h;
	private float field_184475_as;
	private int field_184476_at;
	private double boatPitch;
	private double field_184477_av;
	private double field_184478_aw;
	private double boatYaw;
	private double field_184479_ay;
	private boolean field_184480_az;
	private boolean field_184459_aA;
	private boolean field_184461_aB;
	private boolean field_184463_aC;
	private double field_184465_aD;
	/**
	 * How much the boat should glide given the slippery blocks it's currently gliding over.
	 * Halved every tick.
	 */
	private float boatGlide;
	private Status status;
	private Status previousStatus;
	private double field_184473_aH;

	public EntityObsidianBoat(World worldIn) {
		super(worldIn);
		this.field_184470_f = new float[2];
		this.preventEntitySpawning = true;
		this.setSize(1.375F, 0.5625F);
		this.isImmuneToFire = true;
	}

	public EntityObsidianBoat(World worldIn, double x, double y, double z) {
		this(worldIn);
		this.setPosition(x, y, z);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.isImmuneToFire = true;
	}

	public static float func_184456_a(IBlockState p_184456_0_, IBlockAccess p_184456_1_, BlockPos p_184456_2_) {
		int i = p_184456_0_.getValue(BlockLiquid.LEVEL);
		return (i & 7) == 0 && p_184456_1_.getBlockState(p_184456_2_.up()).getMaterial() == Material.LAVA ? 1.0F : 1.0F - BlockLiquid.getLiquidHeightPercent(i);
	}

	public static float func_184452_b(IBlockState p_184452_0_, IBlockAccess p_184452_1_, BlockPos p_184452_2_) {
		return (float) p_184452_2_.getY() + func_184456_a(p_184452_0_, p_184452_1_, p_184452_2_);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
		this.dataManager.register(TIME_SINCE_HIT, 0);
		this.dataManager.register(FORWARD_DIRECTION, 1);
		this.dataManager.register(DAMAGE_TAKEN, 0.0F);

		for (DataParameter aField_184468_e : field_184468_e)
			this.dataManager.register(aField_184468_e, false);
	}

	/**
	 * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
	 * pushable on contact, like boats or minecarts.
	 */
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return entityIn.getEntityBoundingBox();
	}

	/**
	 * Returns the collision bounding box for this entity
	 */
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	public boolean canBePushed() {
		return true;
	}

	/**
	 * Returns the Y offset from the entity's position for any entity riding this one.
	 */
	public double getMountedYOffset() {
		return 1;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (!this.worldObj.isRemote && !this.isDead) {
			if (source instanceof EntityDamageSourceIndirect && source.getEntity() != null && this.isPassenger(source.getEntity()))
				return false;
			else {
				this.setForwardDirection(-this.getForwardDirection());
				this.setTimeSinceHit(10);
				this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
				this.setBeenAttacked();
				boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;

				if (flag || this.getDamageTaken() > 40.0F) {
					if (!flag && this.worldObj.getGameRules().getBoolean("doEntityDrops"))
						this.dropItemWithOffset(this.getItemBoat(), 1, 0.0F);

					this.setDead();
				}

				return true;
			}
		} else {
			return true;
		}
	}

	public Item getItemBoat() {
		return CppItems.OBSIDIAN_BOAT;
	}

	/**
	 * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
	 */
	@SideOnly(Side.CLIENT)
	public void performHurtAnimation() {
		this.setForwardDirection(-this.getForwardDirection());
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11.0F);
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
		this.boatPitch = x;
		this.field_184477_av = y;
		this.field_184478_aw = z;
		this.boatYaw = (double) yaw;
		this.field_184479_ay = (double) pitch;
		this.field_184476_at = 10;
	}

	/**
	 * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into
	 * account.
	 */
	public EnumFacing getAdjustedHorizontalFacing() {
		return this.getHorizontalFacing().rotateY();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.previousStatus = this.status;
		this.status = this.getBoatStatus();

		if (this.status != Status.UNDER_WATER && this.status != Status.UNDER_FLOWING_WATER)
			this.field_184474_h = 0.0F;
		else
			++this.field_184474_h;

		if (!this.worldObj.isRemote && this.field_184474_h >= 60.0F)
			this.removePassengers();

		if (this.getTimeSinceHit() > 0)
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);

		if (this.getDamageTaken() > 0.0F)
			this.setDamageTaken(this.getDamageTaken() - 1.0F);

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (!this.worldObj.isRemote)
			this.setFlag(6, this.isGlowing());

		this.onEntityUpdate();
		this.func_184447_s();

		if (this.canPassengerSteer()) {
			if (this.getPassengers().size() == 0 || !(this.getPassengers().get(0) instanceof EntityPlayer))
				this.func_184445_a(false, false);

			this.updateMotion();

			if (this.worldObj.isRemote) {
				this.func_184443_x();
				this.worldObj.sendPacketToServer(new CPacketSteerBoat(this.func_184457_a(0), this.func_184457_a(1)));
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		} else {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		for (int i = 0; i <= 1; ++i)
			if (this.func_184457_a(i))
				this.field_184470_f[i] = (float) ((double) this.field_184470_f[i] + 0.01D);
			else
				this.field_184470_f[i] = 0.0F;

		this.doBlockCollisions();
		List<Entity> list = this.worldObj.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.getTeamCollisionPredicate(this));

		if (!list.isEmpty()) {
			boolean flag = !this.worldObj.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);

			list.stream().filter(aList -> !aList.isPassenger(this)).forEach(aList -> {
				if (flag && this.getPassengers().size() < 2 && !aList.isRiding() && aList.width < this.width && aList instanceof EntityLivingBase && !(aList instanceof EntityWaterMob) && !(aList instanceof EntityPlayer))
					aList.startRiding(this);
				else
					this.applyEntityCollision(aList);
			});
		}
	}

	private void func_184447_s() {
		if (this.field_184476_at > 0 && !this.canPassengerSteer()) {
			double d0 = this.posX + (this.boatPitch - this.posX) / (double) this.field_184476_at;
			double d1 = this.posY + (this.field_184477_av - this.posY) / (double) this.field_184476_at;
			double d2 = this.posZ + (this.field_184478_aw - this.posZ) / (double) this.field_184476_at;
			double d3 = MathHelper.wrapDegrees(this.boatYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.field_184476_at);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.field_184479_ay - (double) this.rotationPitch) / (double) this.field_184476_at);
			--this.field_184476_at;
			this.setPosition(d0, d1, d2);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
	}

	public void func_184445_a(boolean p_184445_1_, boolean p_184445_2_) {
		this.dataManager.set(field_184468_e[0], p_184445_1_);
		this.dataManager.set(field_184468_e[1], p_184445_2_);
	}

	@SideOnly(Side.CLIENT)
	public float func_184448_a(int p_184448_1_, float limbSwing) {
		return this.func_184457_a(p_184448_1_) ? (float) MathHelper.denormalizeClamp((double) this.field_184470_f[p_184448_1_] - 0.01D, (double) this.field_184470_f[p_184448_1_], (double) limbSwing) : 0.0F;
	}

	/**
	 * Determines whether the boat is in water, gliding on land, or in air
	 */
	private Status getBoatStatus() {
		Status entityboat$status = this.getUnderwaterStatus();

		if (entityboat$status != null) {
			this.field_184465_aD = this.getEntityBoundingBox().maxY;
			return entityboat$status;
		} else if (this.func_184446_u())
			return Status.IN_WATER;
		else {
			float f = this.getBoatGlide();

			if (f > 0.0F) {
				this.boatGlide = f;
				return Status.ON_LAND;
			} else
				return Status.IN_AIR;
		}
	}

	public float func_184451_k() {
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.ceiling_double_int(axisalignedbb.maxX);
		int k = MathHelper.floor_double(axisalignedbb.maxY);
		int l = MathHelper.ceiling_double_int(axisalignedbb.maxY - this.field_184473_aH);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.ceiling_double_int(axisalignedbb.maxZ);
		BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

		try {
			label78:

			for (int k1 = k; k1 < l; ++k1) {
				float f = 0.0F;
				int l1 = i;

				while (true) {
					if (l1 >= j) {
						if (f < 1.0F)
							return (float) blockpos$pooledmutableblockpos.getY() + f;

						break;
					}

					for (int i2 = i1; i2 < j1; ++i2) {
						blockpos$pooledmutableblockpos.setPos(l1, k1, i2);
						IBlockState iblockstate = this.worldObj.getBlockState(blockpos$pooledmutableblockpos);

						if (iblockstate.getMaterial() == Material.LAVA)
							f = Math.max(f, func_184456_a(iblockstate, this.worldObj, blockpos$pooledmutableblockpos));

						if (f >= 1.0F) {
							continue label78;
						}
					}

					++l1;
				}
			}

			return (float) (l + 1);
		} finally {
			blockpos$pooledmutableblockpos.release();
		}
	}

	/**
	 * Decides how much the boat should be gliding on the land (based on any slippery blocks)
	 */
	@SuppressWarnings("ConstantConditions")
	public float getBoatGlide() {
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		int i = MathHelper.floor_double(axisalignedbb1.minX) - 1;
		int j = MathHelper.ceiling_double_int(axisalignedbb1.maxX) + 1;
		int k = MathHelper.floor_double(axisalignedbb1.minY) - 1;
		int l = MathHelper.ceiling_double_int(axisalignedbb1.maxY) + 1;
		int i1 = MathHelper.floor_double(axisalignedbb1.minZ) - 1;
		int j1 = MathHelper.ceiling_double_int(axisalignedbb1.maxZ) + 1;
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
		float f = 0.0F;
		int k1 = 0;
		BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

		try {
			for (int l1 = i; l1 < j; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

					if (j2 != 2) {
						for (int k2 = k; k2 < l; ++k2) {
							if (j2 <= 0 || k2 != k && k2 != l - 1) {
								blockpos$pooledmutableblockpos.setPos(l1, k2, i2);
								IBlockState iblockstate = this.worldObj.getBlockState(blockpos$pooledmutableblockpos);
								iblockstate.addCollisionBoxToList(this.worldObj, blockpos$pooledmutableblockpos, axisalignedbb1, list, this);

								if (!list.isEmpty()) {
									f += iblockstate.getBlock().slipperiness;
									++k1;
								}

								list.clear();
							}
						}
					}
				}
			}
		} finally {
			blockpos$pooledmutableblockpos.release();
		}

		return f / (float) k1;
	}

	private boolean func_184446_u() {
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.ceiling_double_int(axisalignedbb.maxX);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.ceiling_double_int(axisalignedbb.minY + 0.001D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.ceiling_double_int(axisalignedbb.maxZ);
		boolean flag = false;
		this.field_184465_aD = Double.MIN_VALUE;
		BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

		try {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
						IBlockState iblockstate = this.worldObj.getBlockState(blockpos$pooledmutableblockpos);

						if (iblockstate.getMaterial() == Material.LAVA) {
							float f = func_184452_b(iblockstate, this.worldObj, blockpos$pooledmutableblockpos);
							this.field_184465_aD = Math.max((double) f, this.field_184465_aD);
							flag |= axisalignedbb.minY < (double) f;
						}
					}
				}
			}
		} finally {
			blockpos$pooledmutableblockpos.release();
		}

		return flag;
	}

	/**
	 * Decides whether the boat is currently underwater.
	 */
	private Status getUnderwaterStatus() {
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
		double d0 = axisalignedbb.maxY + 0.001D;
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.ceiling_double_int(axisalignedbb.maxX);
		int k = MathHelper.floor_double(axisalignedbb.maxY);
		int l = MathHelper.ceiling_double_int(d0);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.ceiling_double_int(axisalignedbb.maxZ);
		boolean flag = false;
		BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

		try {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
						IBlockState iblockstate = this.worldObj.getBlockState(blockpos$pooledmutableblockpos);

						if (iblockstate.getMaterial() == Material.LAVA && d0 < (double) func_184452_b(iblockstate, this.worldObj, blockpos$pooledmutableblockpos)) {
							if ((iblockstate.getValue(BlockLiquid.LEVEL) != 0)) {
								return Status.UNDER_FLOWING_WATER;
							}

							flag = true;
						}
					}
				}
			}
		} finally {
			blockpos$pooledmutableblockpos.release();
		}

		return flag ? Status.UNDER_WATER : null;
	}

	/**
	 * Update the boat's speed, based on momentum.
	 */
	private void updateMotion() {
		double d0 = -0.03999999910593033D;
		double d1 = d0;
		double d2 = 0.0D;
		/* How much of current speed to retain. Value zero to one. */
		float momentum = 0.05F;

		if (this.previousStatus == Status.IN_AIR && this.status != Status.IN_AIR && this.status != Status.ON_LAND) {
			this.field_184465_aD = this.getEntityBoundingBox().minY + (double) this.height;
			this.setPosition(this.posX, (double) (this.func_184451_k() - this.height) + 0.101D, this.posZ);
			this.motionY = 0.0D;
			this.field_184473_aH = 0.0D;
			this.status = Status.IN_WATER;
		} else {
			if (this.status == Status.IN_WATER) {
				d2 = (this.field_184465_aD - this.getEntityBoundingBox().minY) / (double) this.height;
				momentum = 0.9F;
			} else if (this.status == Status.UNDER_FLOWING_WATER) {
				d1 = -7.0E-4D;
				momentum = 0.9F;
			} else if (this.status == Status.UNDER_WATER) {
				d2 = 0.009999999776482582D;
				momentum = 0.45F;
			} else if (this.status == Status.IN_AIR) {
				momentum = 0.9F;
			} else if (this.status == Status.ON_LAND) {
				momentum = this.boatGlide;

				if (this.getControllingPassenger() instanceof EntityPlayer) {
					this.boatGlide /= 2.0F;
				}
			}

			this.motionX *= (double) momentum;
			this.motionZ *= (double) momentum;
			this.field_184475_as *= momentum;
			this.motionY += d1;

			if (d2 > 0.0D) {
				this.motionY += d2 * (-d0 / 0.65D);
				this.motionY *= 0.75D;
			}
		}
	}

	private void func_184443_x() {
		if (this.isBeingRidden()) {
			float f = 0.0F;

			if (this.field_184480_az) {
				this.field_184475_as += -1.0F;
			}

			if (this.field_184459_aA) {
				++this.field_184475_as;
			}

			if (this.field_184459_aA != this.field_184480_az && !this.field_184461_aB && !this.field_184463_aC) {
				f += 0.005F;
			}

			this.rotationYaw += this.field_184475_as;

			if (this.field_184461_aB) {
				f += 0.04F;
			}

			if (this.field_184463_aC) {
				f -= 0.005F;
			}

			this.motionX += (double) (MathHelper.sin(-this.rotationYaw * 0.017453292F) * f);
			this.motionZ += (double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * f);
			this.func_184445_a(this.field_184459_aA || this.field_184461_aB, this.field_184480_az || this.field_184461_aB);
		}
	}

	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			float f = 0.0F;
			float f1 = (float) ((this.isDead ? 0.009999999776482582D : this.getMountedYOffset()) + passenger.getYOffset());

			if (this.getPassengers().size() > 1) {
				int i = this.getPassengers().indexOf(passenger);

				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}

				if (passenger instanceof EntityAnimal) {
					f = (float) ((double) f + 0.2D);
				}
			}

			Vec3d vec3d = (new Vec3d((double) f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
			passenger.setPosition(this.posX + vec3d.xCoord, this.posY + (double) f1, this.posZ + vec3d.zCoord);
			passenger.rotationYaw += this.field_184475_as;
			passenger.setRotationYawHead(passenger.getRotationYawHead() + this.field_184475_as);
			this.applyYawToEntity(passenger);

			if (passenger instanceof EntityAnimal && this.getPassengers().size() > 1) {
				int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
				passenger.setRenderYawOffset(((EntityAnimal) passenger).renderYawOffset + (float) j);
				passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) j);
			}
		}
	}

	/**
	 * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
	 */
	protected void applyYawToEntity(Entity entityToUpdate) {
		entityToUpdate.setRenderYawOffset(this.rotationYaw);
		float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
		float f1 = MathHelper.clamp_float(f, -105.0F, 105.0F);
		entityToUpdate.prevRotationYaw += f1 - f;
		entityToUpdate.rotationYaw += f1 - f;
		entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
	}

	/**
	 * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
	 */
	@SideOnly(Side.CLIENT)
	public void applyOrientationToEntity(Entity entityToUpdate) {
		this.applyYawToEntity(entityToUpdate);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {

	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {

	}

	public boolean processInitialInteract(EntityPlayer player, ItemStack stack, EnumHand hand) {
		if (!this.worldObj.isRemote && !player.isSneaking() && this.field_184474_h < 60.0F) {
			player.startRiding(this);
		}

		return true;
	}

	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		this.field_184473_aH = this.motionY;

		if (!this.isRiding()) {
			if (onGroundIn) {
				if (this.fallDistance > 3.0F) {
					if (this.status != Status.ON_LAND) {
						this.fallDistance = 0.0F;
						return;
					}

					this.fall(this.fallDistance, 1.0F);

					if (!this.worldObj.isRemote && !this.isDead) {
						this.setDead();

						if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
							this.entityDropItem(new ItemStack(CppItems.OBSIDIAN_BOAT), 0.0F);
						}
					}
				}

				this.fallDistance = 0.0F;
			} else if (this.worldObj.getBlockState((new BlockPos(this)).down()).getMaterial() != Material.LAVA && y < 0.0D) {
				this.fallDistance = (float) ((double) this.fallDistance - y);
			}
		}
	}

	public boolean func_184457_a(int p_184457_1_) {
		return (boolean) this.dataManager.get(field_184468_e[p_184457_1_]) && this.getControllingPassenger() != null;
	}

	/**
	 * Gets the damage taken from the last hit.
	 */
	public float getDamageTaken() {
		return this.dataManager.get(DAMAGE_TAKEN);
	}

	/**
	 * Sets the damage taken from the last hit.
	 */
	public void setDamageTaken(float damageTaken) {
		this.dataManager.set(DAMAGE_TAKEN, damageTaken);
	}

	/**
	 * Gets the time since the last hit.
	 */
	public int getTimeSinceHit() {
		return this.dataManager.get(TIME_SINCE_HIT);
	}

	/**
	 * Sets the time to count down from since the last time entity was hit.
	 */
	public void setTimeSinceHit(int timeSinceHit) {
		this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
	}

	/**
	 * Gets the forward direction of the entity.
	 */
	public int getForwardDirection() {
		return this.dataManager.get(FORWARD_DIRECTION);
	}

	/**
	 * Sets the forward direction of the entity.
	 */
	public void setForwardDirection(int forwardDirection) {
		this.dataManager.set(FORWARD_DIRECTION, forwardDirection);
	}

	protected boolean canFitPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	/**
	 * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
	 * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
	 */
	public Entity getControllingPassenger() {
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	@SuppressWarnings("unused")
	@SideOnly(Side.CLIENT)
	public void func_184442_a(boolean p_184442_1_, boolean p_184442_2_, boolean p_184442_3_, boolean p_184442_4_) {
		this.field_184480_az = p_184442_1_;
		this.field_184459_aA = p_184442_2_;
		this.field_184461_aB = p_184442_3_;
		this.field_184463_aC = p_184442_4_;
	}

	public enum Status {
		IN_WATER,
		UNDER_WATER,
		UNDER_FLOWING_WATER,
		ON_LAND,
		IN_AIR
	}

}