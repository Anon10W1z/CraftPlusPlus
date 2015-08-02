package io.github.anon10w1z.cpp.handlers;

import io.github.anon10w1z.cpp.gui.ContainerCraftingPad;
import io.github.anon10w1z.cpp.gui.GuiCraftingPad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * The GUI handler for Craft++
 */
public class CppGuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return id == 0 ? new ContainerCraftingPad(player.inventory, world) : null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return id == 0 ? new GuiCraftingPad(player.inventory, world) : null;
	}
}
