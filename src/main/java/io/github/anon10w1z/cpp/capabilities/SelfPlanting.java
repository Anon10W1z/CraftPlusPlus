package io.github.anon10w1z.cpp.capabilities;

import net.minecraft.entity.item.EntityItem;

public interface SelfPlanting {

	
	void setMinSteadyTicks(int minSteadyTicks);
	int getMinSteadyTicks();
	
	void setSteadyTicks(int steadyTicks);
	int getSteadyTicks();
	
	void handlePlantingLogic(EntityItem stack);
}
