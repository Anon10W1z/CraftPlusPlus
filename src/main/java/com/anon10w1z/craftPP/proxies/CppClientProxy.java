package com.anon10w1z.craftPP.proxies;

import com.anon10w1z.craftPP.blocks.CppBlocks;
import com.anon10w1z.craftPP.main.CppReferences;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

/**
 * The client-side proxy for Craft++
 *
 * @author Anon10W1z
 */
public class CppClientProxy extends CppCommonProxy {
	//used to not create a new instance every frame
	private static final Gui GUI_INSTANCE = new Gui();
	private static final ResourceLocation INVENTORY_RESOURCE_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");

	@Override
	public void registerBlockInventoryRenderers() {
		registerBlockInventoryRenderer(CppBlocks.flint_block, "flint");
		registerBlockInventoryRenderer(CppBlocks.sugar_block, "sugar");
		registerBlockInventoryRenderer(CppBlocks.charcoal_block, "charcoal");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void displayPotionEffects() {
		int xPos = 2;
		int yPos = 2;
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.renderEngine.bindTexture(INVENTORY_RESOURCE_LOCATION);
		//some constants for drawing textures
		final int POTION_ICON_SIZE = 18;
		final int POTION_ICON_SPACING = POTION_ICON_SIZE + 2;
		final int POTION_ICON_BASE_X_OFFSET = 0;
		final int POTION_ICON_BASE_Y_OFFSET = 198;
		final int POTION_ICONS_PER_ROW = 8;
		Collection<PotionEffect> potionEffects = minecraft.thePlayer.getActivePotionEffects();
		for (PotionEffect potionEffect : potionEffects) {
			Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
			if (potion.hasStatusIcon()) {
				int iconIndex = potion.getStatusIconIndex();
				GUI_INSTANCE.drawTexturedModalRect(xPos, yPos, POTION_ICON_BASE_X_OFFSET + iconIndex % POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_BASE_Y_OFFSET + iconIndex / POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_SIZE, POTION_ICON_SIZE);
				xPos += POTION_ICON_SPACING;
			}
		}
	}

	/**
	 * Registers a renderer for a block in the inventory
	 *
	 * @param block      The block
	 * @param namePrefix The name prefix of the block
	 */
	private void registerBlockInventoryRenderer(Block block, String namePrefix) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(CppReferences.MOD_ID + ":" + namePrefix + "_block", "inventory"));
	}
}
