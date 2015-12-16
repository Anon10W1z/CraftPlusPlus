package io.github.anon10w1z.cpp.entities.render;

import io.github.anon10w1z.cpp.entities.EntityObsidianBoat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The obsidian boat renderer
 */
@SideOnly(Side.CLIENT)
public class RenderObsidianBoat extends Render {
	private static final ResourceLocation boatTextures = new ResourceLocation("minecraft:textures/blocks/obsidian.png");
	protected ModelBase modelBoat = new ModelBoat();

	public RenderObsidianBoat() {
		super(Minecraft.getMinecraft().getRenderManager());
		this.shadowSize = 0.5F;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return boatTextures;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks) {
		y += 0.3;
		EntityObsidianBoat boat = (EntityObsidianBoat) entity;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y + 0.25F, (float) z);
		GlStateManager.rotate(180.0F - f, 0.0F, 1.0F, 0.0F);
		float f2 = (float) boat.getTimeSinceHit() - partialTicks;
		float f3 = Math.max(0, boat.getDamageTaken() - partialTicks);

		if (f2 > 0.0F)
			GlStateManager.rotate(MathHelper.sin(f2) * f2 * f3 / 10.0F * (float) boat.getForwardDirection(), 1.0F, 0.0F, 0.0F);

		float f4 = 0.75F;
		GlStateManager.scale(f4, f4, f4);
		GlStateManager.scale(1.0F / f4, 1.0F / f4, 1.0F / f4);
		this.bindEntityTexture(boat);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.modelBoat.render(boat, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		super.doRender(boat, x, y, z, f, partialTicks);
	}
}
