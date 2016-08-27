package io.github.anon10w1z.cpp.items;

import io.github.anon10w1z.cpp.entities.EntityObsidianBoat;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Obsidian boat item to alongside obsidian boat entity
 */
public class ItemObsidianBoat extends Item {
	public ItemObsidianBoat() {
		this.setUnlocalizedName("boatObsidian");
		this.setRegistryName(CppModInfo.MOD_ID, "obsidian_boat");
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		this.setMaxStackSize(1);
	}

	@SuppressWarnings({"unchecked", "NullableProblems"})
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer player, EnumHand hand) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f + (double) player.getEyeHeight();
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		Vec3d vec3d1 = vec3d.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1, true);

		if (raytraceresult == null)
			return new ActionResult(EnumActionResult.PASS, itemstack);
		else {
			Vec3d vec3d2 = player.getLook(f);
			boolean flag = false;
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().addCoord(vec3d2.xCoord * d3, vec3d2.yCoord * d3, vec3d2.zCoord * d3).expandXyz(1.0D));

			for (Entity aList : list) {
				if (aList.canBeCollidedWith()) {
					AxisAlignedBB axisalignedbb = aList.getEntityBoundingBox().expandXyz((double) aList.getCollisionBorderSize());
					if (axisalignedbb.isVecInside(vec3d))
						flag = true;
				}
			}

			if (flag)
				return new ActionResult(EnumActionResult.PASS, itemstack);
			else if (raytraceresult.typeOfHit != Type.BLOCK)
				return new ActionResult(EnumActionResult.PASS, itemstack);
			else {
				Block block = world.getBlockState(raytraceresult.getBlockPos()).getBlock();
				boolean flag1 = block == Blocks.LAVA || block == Blocks.FLOWING_LAVA;
				EntityObsidianBoat entityobsidianboat = new EntityObsidianBoat(world, raytraceresult.hitVec.xCoord, flag1 ? raytraceresult.hitVec.yCoord - 0.12D : raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
				entityobsidianboat.rotationYaw = player.rotationYaw;

				if (!world.getCollisionBoxes(entityobsidianboat, entityobsidianboat.getEntityBoundingBox().expandXyz(-0.1D)).isEmpty())
					return new ActionResult(EnumActionResult.FAIL, itemstack);
				else {
					if (!world.isRemote)
						world.spawnEntityInWorld(entityobsidianboat);

					if (!player.capabilities.isCreativeMode)
						--itemstack.stackSize;

					//noinspection ConstantConditions
					player.addStat(StatList.getObjectUseStats(this));
					return new ActionResult(EnumActionResult.SUCCESS, itemstack);
				}
			}
		}
	}
}
