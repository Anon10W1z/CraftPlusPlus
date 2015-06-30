package io.github.anon10w1z.craftPP.enchantments;

import io.github.anon10w1z.craftPP.handlers.CppConfigHandler;
import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Initializes Craft++'s enchantments
 */
public final class CppEnchantments {
	public static List<String> enchantmentNames = Arrays.asList("Nimble", "Hops", "Blazing", "Quickdraw", "Homing");

	/**
	 * Registers the enchantments for Craft++
	 */
	public static void registerEnchantments() {
		enchantmentNames.stream().filter(CppConfigHandler.enchantmentNameToEnable::get).forEach(CppEnchantments::instantiateEnchantment);
	}

	private static void instantiateEnchantment(String enchantmentName) {
		try {
			Class.forName(FilenameUtils.removeExtension(CppEnchantments.class.getName()) + ".Enchantment" + enchantmentName).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
