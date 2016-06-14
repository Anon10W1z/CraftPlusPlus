package io.github.anon10w1z.cpp.entities.render;

import io.github.anon10w1z.cpp.entities.EntityObsidianBoat;
import net.minecraft.client.model.IMultipassModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderObsidianBoat extends Render<EntityObsidianBoat> {
	private static final ResourceLocation boatTexture = new ResourceLocation("textures/blocks/obsidian.png");
	protected ModelBase modelBoat = new ModelObsidianBoat();

	public RenderObsidianBoat(RenderManager renderManagerIn) {
		super(renderManagerIn);
		this.shadowSize = 0.5F;
	}

	public void doRender(EntityObsidianBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
		y += 0.5;
		GlStateManager.pushMatrix();
		this.func_188309_a(x, y, z);
		this.func_188311_a(entity, entityYaw, partialTicks);
		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		this.modelBoat.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public void func_188311_a(EntityBoat p_188311_1_, float p_188311_2_, float p_188311_3_) {
		GlStateManager.rotate(180.0F - p_188311_2_, 0.0F, 1.0F, 0.0F);
		float f = (float) p_188311_1_.getTimeSinceHit() - p_188311_3_;
		float f1 = p_188311_1_.getDamageTaken() - p_188311_3_;

		if (f1 < 0.0F)
			f1 = 0.0F;

		if (f > 0.0F)
			GlStateManager.rotate(MathHelper.sin(f) * f * f1 / 10.0F * (float) p_188311_1_.getForwardDirection(), 1.0F, 0.0F, 0.0F);

		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
	}

	public void func_188309_a(double p_188309_1_, double p_188309_3_, double p_188309_5_) {
		GlStateManager.translate((float) p_188309_1_, (float) p_188309_3_ + 0.375F, (float) p_188309_5_);
	}

	protected ResourceLocation getEntityTexture(EntityObsidianBoat entity) {
		return boatTexture;
	}

	public boolean isMultipass() {
		return true;
	}

	public void renderMultipass(EntityObsidianBoat p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_, float p_188300_8_, float p_188300_9_) {
		GlStateManager.pushMatrix();
		this.func_188309_a(p_188300_2_, p_188300_4_, p_188300_6_);
		this.func_188311_a(p_188300_1_, p_188300_8_, p_188300_9_);
		this.bindEntityTexture(p_188300_1_);
		((IMultipassModel) this.modelBoat).renderMultipass(p_188300_1_, p_188300_9_, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}