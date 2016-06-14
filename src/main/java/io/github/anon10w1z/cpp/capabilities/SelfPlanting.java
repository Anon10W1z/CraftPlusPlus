package io.github.anon10w1z.cpp.capabilities;

import net.minecraft.entity.item.EntityItem;

public interface SelfPlanting {
	int getMinSteadyTicks();

	void setMinSteadyTicks(int minSteadyTicks);

	int getSteadyTicks();

	void setSteadyTicks(int steadyTicks);

	void handlePlantingLogic(EntityItem stack);
}
