package com.github.anon10w1z.craftPP.proxies;

import com.github.anon10w1z.craftPP.blocks.CppBlocks;
import com.github.anon10w1z.craftPP.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

/**
 * The client-side proxy for Craft++
 */
public class CppClientProxy extends CppCommonProxy {
	private static final Gui guiInstance = new Gui(); //used to draw potion icons
	private static final ResourceLocation inventoryResourceLocation = new ResourceLocation("textures/gui/container/inventory.png"); //location of potion icons texture file
	private static final Minecraft minecraft = Minecraft.getMinecraft(); //the Minecraft instance
	private static KeyBinding potionKey; //the key binding for toggling the potion overlay

	@Override
	@SuppressWarnings("unchecked")
	public void displayPotionEffects() {
		int xPos = 2;
		int yPos = 2;
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		minecraft.renderEngine.bindTexture(inventoryResourceLocation);
		//some constants for drawing textures, which all refer to inventory.png
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
				guiInstance.drawTexturedModalRect(xPos, yPos, POTION_ICON_BASE_X_OFFSET + iconIndex % POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_BASE_Y_OFFSET + iconIndex / POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_SIZE, POTION_ICON_SIZE);
				xPos += POTION_ICON_SPACING;
			}
		}
	}

	@Override
	public void registerKeyBindings() {
		potionKey = new KeyBinding(CppModInfo.MOD_ID + ".potionDisplay", Keyboard.KEY_P, "key.categories.ui");
		ClientRegistry.registerKeyBinding(potionKey);
	}

	@Override
	public boolean isPotionKeyPressed() {
		return potionKey.isPressed();
	}

	@Override
	public boolean isGuiOpen() {
		return minecraft.currentScreen != null;
	}

	@Override
	public void registerBlockInventoryRenderers() {
		registerBlockInventoryRenderer(CppBlocks.flint_block, "flint");
		registerBlockInventoryRenderer(CppBlocks.sugar_block, "sugar");
		registerBlockInventoryRenderer(CppBlocks.charcoal_block, "charcoal");
	}

	/**
	 * Registers a renderer for a block in the inventory
	 *
	 * @param block      The block
	 * @param namePrefix The name prefix of the block
	 */
	private void registerBlockInventoryRenderer(Block block, String namePrefix) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(CppModInfo.MOD_ID + ":" + namePrefix + "_block", "inventory"));
	}
}
