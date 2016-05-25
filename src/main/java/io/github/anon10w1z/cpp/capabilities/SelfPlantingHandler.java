package io.github.anon10w1z.cpp.capabilities;

import io.github.anon10w1z.cpp.main.CppUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public class SelfPlantingHandler implements SelfPlanting, INBTSerializable<NBTTagCompound>{

	private int minSteadyTicks, steadyTicks;
	
	private static final String MINSTEADYTICKS_KEY = "minsteadyticks";
	private static final String STEADYTICKS_KEY = "steadyticks";
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger(MINSTEADYTICKS_KEY, minSteadyTicks);
		compound.setInteger(STEADYTICKS_KEY, steadyTicks);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		minSteadyTicks = nbt.getInteger(MINSTEADYTICKS_KEY);
		steadyTicks = nbt.getInteger(STEADYTICKS_KEY);
	}
	
	@Override
	public int getMinSteadyTicks() {
		return minSteadyTicks;
	}
	
	@Override
	public void setMinSteadyTicks(int minSteadyTicks) {
		this.minSteadyTicks = minSteadyTicks;
	}

	
	@Override
	public int getSteadyTicks() {
		return steadyTicks;
	}
	
	@Override
	public void setSteadyTicks(int steadyTicks) {
		this.steadyTicks = steadyTicks;
	}
	
	@Override
	public void handlePlantingLogic(EntityItem entity) {
		
		Item item = entity.getEntityItem().getItem();
			++this.steadyTicks;
			BlockPos entityPosDown = new BlockPos(entity).down();
			BlockPos lastTickEntityPosDown = new BlockPos(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).down();
			if (entityPosDown.compareTo(lastTickEntityPosDown) != 0)
				this.steadyTicks = 0;
			if (this.steadyTicks >= this.minSteadyTicks)
				entity.getEntityItem().onItemUse(CppUtils.getFakePlayer(entity.worldObj), entity.worldObj, entityPosDown, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
		
	}
}
