package io.github.anon10w1z.craftPP.proxies;

import io.github.anon10w1z.craftPP.blocks.CppBlocks;
import io.github.anon10w1z.craftPP.gui.GuiCppConfig;
import io.github.anon10w1z.craftPP.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.GuiIngameModOptions;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

/**
 * The client-side proxy for Craft++
 */
@SuppressWarnings("unused")
public class CppClientProxy extends CppCommonProxy {
	private static final Minecraft minecraft = Minecraft.getMinecraft(); //the Minecraft instance
	private static KeyBinding potionKey; //the key binding for toggling the potion overlay

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
		Collection<PotionEffect> potionEffects = minecraft.thePlayer.getActivePotionEffects();
		for (PotionEffect potionEffect : potionEffects) {
			Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
			if (potion.hasStatusIcon()) {
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glDisable(GL11.GL_LIGHTING);
				minecraft.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/inventory.png")); //draw from inventory.png
				int iconIndex = potion.getStatusIconIndex();

				//some constants for drawing textures, which all refer to inventory.png
				final int POTION_ICON_SIZE = 18;
				final int POTION_ICON_SPACING = POTION_ICON_SIZE + 2;
				final int POTION_ICON_BASE_X_OFFSET = 0;
				final int POTION_ICON_BASE_Y_OFFSET = 198;
				final int POTION_ICONS_PER_ROW = 8;

				new Gui().drawTexturedModalRect(xPos, yPos, POTION_ICON_BASE_X_OFFSET + iconIndex % POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_BASE_Y_OFFSET + iconIndex / POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_SIZE, POTION_ICON_SIZE);
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
	public void handleGuiOpen(Event event) {
		if (event instanceof GuiOpenEvent) {
			GuiOpenEvent guiOpenEvent = (GuiOpenEvent) event;
			if (guiOpenEvent.gui instanceof GuiIngameModOptions)
				guiOpenEvent.gui = new GuiCppConfig();
		}
	}

	/**
	 * Registers a renderer for a block in the inventory
	 * @param block      The block
	 * @param namePrefix The name prefix of the block
	 */
	private void registerBlockInventoryRenderer(Block block, String namePrefix) {
		minecraft.getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(CppModInfo.MOD_ID + ":" + namePrefix + "_block", "inventory"));
	}
}

