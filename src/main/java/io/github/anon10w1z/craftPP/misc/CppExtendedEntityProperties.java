package io.github.anon10w1z.craftPP.misc;

import io.github.anon10w1z.craftPP.main.CppUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Adds extended properties to item entities
 */
public class CppExtendedEntityProperties implements IExtendedEntityProperties {
	/**
	 * The name of these extended properties
	 */
	public static final String EXTENDED_PROPERTIES_NAME = "CppExtendedEntityItem";

	/**
	 * The item entity these properties refer to
	 */
	public final EntityItem entityItem;

	/**
	 * The world these properties refer to
	 */
	public final World world;

	/**
	 * Number of ticks the item entity needs to be above the same block position
	 */
	public int minSteadyTicks;

	/**
	 * Number of ticks the item entity has been above the same block position
	 */
	public int steadyTicks = 0;

	public CppExtendedEntityProperties(EntityItem entityItem) {
		this.entityItem = entityItem;
		this.world = entityItem.worldObj;
	}

	/**
	 * Registers the Craft++ extended properties for the given item entity
	 *
	 * @param entityItem The item entity to register the properties for
	 */
	public static void registerExtendedProperties(Entity entityItem) {
		if (entityItem instanceof EntityItem)
			entityItem.registerExtendedProperties(EXTENDED_PROPERTIES_NAME, new CppExtendedEntityProperties((EntityItem) entityItem));
	}

	/**
	 * Returns the Craft++ extended properties for the given item entity
	 *
	 * @param entityItem The item entity to obtain the properties from
	 * @return The CppExtendedProperties of the given item entity
	 */
	public static CppExtendedEntityProperties getExtendedProperties(EntityItem entityItem) {
		return (CppExtendedEntityProperties) entityItem.getExtendedProperties(EXTENDED_PROPERTIES_NAME);
	}

	/**
	 * Saves the extended properties to NBT
	 *
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
	 *
	 * @param compound The NBT tag compound to read the properties from
	 */
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = compound.getCompoundTag(EXTENDED_PROPERTIES_NAME);
		this.minSteadyTicks = properties.getInteger("MinSteadyTicks");
		this.steadyTicks = properties.getInteger("SteadyTicks");
	}

	/**
	 * Initializes the extended property variables of the specified entity
	 *
	 * @param entity The entity attached to the properties
	 * @param world  The world the entity is in
	 */
	@Override
	public void init(Entity entity, World world) {
		this.minSteadyTicks = world.rand.nextInt(75) + 75;
	}

	/**
	 * Handles all automatic seed planting logic
	 */
	public void handlePlantingLogic() {
		Item item = this.entityItem.getEntityItem().getItem();
		if (item instanceof ItemSeeds || item instanceof ItemSeedFood) {
			++this.steadyTicks;
			BlockPos entityPosDown = new BlockPos(this.entityItem).down();
			BlockPos lastTickEntityPosDown = new BlockPos(this.entityItem.lastTickPosX, this.entityItem.lastTickPosY, this.entityItem.lastTickPosZ).down();
			if (entityPosDown.compareTo(lastTickEntityPosDown) != 0)
				this.steadyTicks = 0;
			if (this.steadyTicks >= this.minSteadyTicks)
				this.entityItem.getEntityItem().onItemUse(CppUtils.getFakePlayer(this.world), this.world, entityPosDown, EnumFacing.UP, 0, 0, 0);
		}
	}
}
