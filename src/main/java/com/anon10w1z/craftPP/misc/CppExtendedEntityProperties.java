package com.anon10w1z.craftPP.misc;

import com.anon10w1z.craftPP.main.CppUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Adds extended properties to item entities
 */
public class CppExtendedEntityProperties implements IExtendedEntityProperties {
	public static final String EXTENDED_PROPERTIES_NAME = "CppExtendedEntityItem";
	public final EntityItem entityItem;
	public final World world;

	public int minSteadyTicks = 0;
	public int steadyTicks = 0;

	public CppExtendedEntityProperties(EntityItem entityItem) {
		this.entityItem = entityItem;
		this.world = entityItem.worldObj;
		this.minSteadyTicks = this.world.rand.nextInt(51) + 50; //2.5 to 5 seconds
	}

	/**
	 * Saves the extended properties to NBT
	 * @param compound The NBT tag compound to write the properties to
	 */
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger("MinSteadyTicks", minSteadyTicks);
		properties.setInteger("SteadyTicks", steadyTicks);
		compound.setTag(EXTENDED_PROPERTIES_NAME, properties);
	}

	/**
	 * Loads the extended properties from NBT
	 * @param compound The NBT tag compound to read the properties from
	 */
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXTENDED_PROPERTIES_NAME);
		this.minSteadyTicks = properties.getInteger("MinSteadyTicks");
		this.steadyTicks = properties.getInteger("SteadyTicks");
	}

	@Override
	public void init(Entity entity, World world) {
		//do nothing
	}

	/**
	 * Registers the extended properties for the given item entity
	 * @param entityItem The item entity to register the properties for
	 */
	public static void registerExtendedProperties(EntityItem entityItem) {
		entityItem.registerExtendedProperties(EXTENDED_PROPERTIES_NAME, new CppExtendedEntityProperties(entityItem));
	}

	/**
	 * Returns the extended properties for the given item entity
	 * @param entityItem The item entity to obtain the properties from
	 * @return The CppExtendedProperties of the given item entity
	 */
	public static CppExtendedEntityProperties getExtendedProperties(EntityItem entityItem) {
		return (CppExtendedEntityProperties) entityItem.getExtendedProperties(EXTENDED_PROPERTIES_NAME);
	}

	/**
	 * Handles all automatic seed planting logic
	 */
	public void handlePlantingLogic() {
		++this.steadyTicks;
		BlockPos entityPosDown = new BlockPos(this.entityItem).down();
		BlockPos lastTickEntityPosDown = new BlockPos(this.entityItem.lastTickPosX, this.entityItem.lastTickPosY, this.entityItem.lastTickPosZ).down();
		if (entityPosDown.compareTo(lastTickEntityPosDown) != 0)
			this.steadyTicks = 0;
		if (this.steadyTicks >= this.minSteadyTicks)
			this.entityItem.getEntityItem().onItemUse(CppUtils.getFakePlayer(this.world), this.world, entityPosDown, EnumFacing.UP, 0, 0, 0);
	}
}
