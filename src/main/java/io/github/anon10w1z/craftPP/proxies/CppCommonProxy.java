package io.github.anon10w1z.craftPP.proxies;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * The common (dual-side) proxy for Craft++
 */
public class CppCommonProxy {
	/**
	 * Registers the block inventory renderers for Craft++'s blocks
	 */
	public void registerBlockInventoryRenderers() {

	}

	/**
	 * Draws potion effect icons in the top-left corner
	 */
	public void displayPotionEffects() {

	}

	/**
	 * Registers the key bindings for Craft++
	 */
	public void registerKeyBindings() {

	}

	/**
	 * @return Whether or not the potion overlay toggle key is pressed
	 */
	public boolean isPotionKeyPressed() {
		return false;
	}

	/**
	 * @return Whether or not any GUI is open in the current Minecraft window
	 */
	public boolean isGuiOpen() {
		return false;
	}

	/**
	 * Handles the opening of any GUI
	 * @param event The event to be handled
	 */
	public void handleGuiOpen(Event event) {

	}
}
