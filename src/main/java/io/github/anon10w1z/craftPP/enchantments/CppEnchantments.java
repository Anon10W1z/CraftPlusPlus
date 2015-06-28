package io.github.anon10w1z.craftPP.enchantments;

import io.github.anon10w1z.craftPP.handlers.CppConfigHandler;

import java.util.Arrays;
import java.util.List;

/**
 * Initializes Craft++'s enchantments
 */
public final class CppEnchantments {
	public static List<String> enchantmentNames = Arrays.asList("Nimble", "Hops", "Blazing", "Quickdraw");

	/**
	 * Registers the enchantments for Craft++
	 */
	public static void registerEnchantments() {
		try {
			enchantmentNames.stream().filter(CppConfigHandler.enchantmentNameToEnable::get).forEach(CppEnchantments::instantiateEnchantment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void instantiateEnchantment(String enchantmentName) {
		try {
			Class.forName("io.github.anon10w1z.craftPP.enchantments.Enchantment" + enchantmentName).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
