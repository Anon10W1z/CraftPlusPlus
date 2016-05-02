package io.github.anon10w1z.cpp.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandler;

public class CapabilitySelfPlanting {

	 @CapabilityInject(SelfPlanting.class)
	 public static Capability<SelfPlanting> CAPABILITY_SELF_PLANTING = null;

	
	 public static void register() {
		 CapabilityManager.INSTANCE.register(SelfPlanting.class, new Capability.IStorage<SelfPlanting>() {

			@Override
			public NBTBase writeNBT(Capability<SelfPlanting> capability, SelfPlanting instance, EnumFacing side) {
				NBTTagCompound compound = new NBTTagCompound();
				
				//TODO: write data
				return compound;
			}

			@Override
			public void readNBT(Capability<SelfPlanting> capability, SelfPlanting instance, EnumFacing side,
					NBTBase nbt) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				//TODO read
				
			}
			 
		}, new Callable<SelfPlanting>() {

			@Override
			public SelfPlanting call() throws Exception {
				return new SelfPlantingHandler();
			}
		});
	 }
	
}
