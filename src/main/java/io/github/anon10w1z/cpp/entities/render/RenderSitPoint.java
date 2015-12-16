package io.github.anon10w1z.cpp.entities.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The sit point renderer (renders nothing)
 */
@SideOnly(Side.CLIENT)
public class RenderSitPoint extends Render {
	public RenderSitPoint() {
		super(Minecraft.getMinecraft().getRenderManager());
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

	@Override
	public boolean shouldRender(Entity entity, ICamera camera, double camX, double camY, double camZ) {
		return false;
	}
}
