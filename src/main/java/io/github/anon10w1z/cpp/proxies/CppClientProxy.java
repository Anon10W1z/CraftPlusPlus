package io.github.anon10w1z.cpp.proxies;

import io.github.anon10w1z.cpp.blocks.CppBlocks;
import io.github.anon10w1z.cpp.entities.*;
import io.github.anon10w1z.cpp.items.CppItems;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The client-side proxy for Craft++
 */
@SuppressWarnings("unused")
public class CppClientProxy extends CppCommonProxy {
	private static final Minecraft minecraft = Minecraft.getMinecraft(); //the Minecraft instance
	private static KeyBinding potionKey; //the key binding for toggling the potion overlay

	@Override
	public void registerRenderers() {
		//Items
		this.registerItemInventoryRenderer(CppItems.dynamite, "dynamite");
		this.registerItemInventoryRenderer(CppItems.obsidian_boat, "obsidian_boat");
		this.registerItemInventoryRenderer(CppItems.fried_egg, "egg_fried");
		this.registerItemInventoryRenderer(CppItems.sponge_wipe, "sponge_wipe");
		this.registerItemInventoryRenderer(CppItems.crafting_pad, "crafting_pad");
		this.registerItemInventoryRenderer(CppItems.binocular_lens, "binocular_lens");
		this.registerItemInventoryRenderer(CppItems.binoculars, "binoculars");
		//Blocks
		this.registerBlockInventoryRenderer(CppBlocks.flint_block, "flint");
		this.registerBlockInventoryRenderer(CppBlocks.sugar_block, "sugar");
		this.registerBlockInventoryRenderer(CppBlocks.charcoal_block, "charcoal");
		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, new RenderSnowball(minecraft.getRenderManager(), CppItems.dynamite, minecraft.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityObsidianBoat.class, new RenderObsidianBoat());
		RenderingRegistry.registerEntityRenderingHandler(EntitySitPoint.class, new RenderSitPoint());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void displayPotionEffects() {
		int xPos = 2;
		int yPos = 2;
		List<PotionEffect> potionEffects = new ArrayList<>(minecraft.thePlayer.getActivePotionEffects());
		Collections.sort(potionEffects, (potionEffect1, potionEffect2) -> potionEffect1.getDuration() - potionEffect2.getDuration());
		Gui gui = new Gui();
		for (PotionEffect potionEffect : potionEffects) {
			Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
			int potionDuration = potionEffect.getDuration();
			if (!potion.hasStatusIcon())
				continue;
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			minecraft.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/inventory.png")); //draw from inventory.png
			int iconIndex = potion.getStatusIconIndex();
			//constants for drawing/loading potion icons from inventory.png
			final int POTION_ICON_SIZE = 18;
			final int POTION_ICON_BASE_X_OFFSET = 0;
			final int POTION_ICON_BASE_Y_OFFSET = 198;
			final int POTION_ICONS_PER_ROW = 8;
			final int POTION_ICON_SPACING = POTION_ICON_SIZE + 2;
			if ((potionDuration > 200 || potionDuration <= 60 || potionDuration % 20 < 10) && (potionDuration > 60 || potionDuration % 10 < 5)) {
				GL11.glPushMatrix();
				GL11.glScalef(8 / 9F, 8 / 9F, 1);
				gui.drawTexturedModalRect(xPos, yPos, POTION_ICON_BASE_X_OFFSET + iconIndex % POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_BASE_Y_OFFSET + iconIndex / POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_SIZE, POTION_ICON_SIZE);
				GL11.glPopMatrix();
				if (potionEffect.getAmplifier() > 0) {
					FontRenderer fontRenderer = minecraft.fontRendererObj;
					String amplifierString = Integer.toString(potionEffect.getAmplifier() + 1);
					gui.drawString(fontRenderer, amplifierString, xPos + 17 - fontRenderer.getStringWidth(amplifierString), yPos + 9, 0xFFFFFF);
				}
			}
			xPos += POTION_ICON_SPACING;
		}
	}

	@Override
	public void registerKeyBindings() {
		potionKey = new KeyBinding("key.potionDisplay", Keyboard.KEY_P, "key.categories.ui");
		ClientRegistry.registerKeyBinding(potionKey);
	}

	@Override
	public boolean isPotionKeyPressed() {
		return potionKey.isPressed();
	}

	@Override
	public boolean isGuiOpen() {
		return minecraft.currentScreen != null && !(minecraft.currentScreen instanceof GuiChat);
	}

	private void registerItemInventoryRenderer(Item item, String name) {
		minecraft.getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(CppModInfo.MOD_ID + ":" + name, "inventory"));
	}

	/**
	 * Registers a renderer for a block in the inventory
	 *
	 * @param block      The block
	 * @param namePrefix The name prefix of the block
	 */
	private void registerBlockInventoryRenderer(Block block, String namePrefix) {
		minecraft.getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(CppModInfo.MOD_ID + ":" + namePrefix + "_block", "inventory"));
	}
}

