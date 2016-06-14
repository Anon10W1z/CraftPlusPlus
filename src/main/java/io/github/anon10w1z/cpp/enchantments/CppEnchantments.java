package io.github.anon10w1z.cpp.enchantments;

import io.github.anon10w1z.cpp.handlers.CppConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Initializes Craft++'s enchantments
 */
public final class CppEnchantments {
	public static List<String> enchantmentNames = Arrays.asList("Nimble", "Hops", "Vigor", "Veteran", "Blazing", "Siphon", "Homing");

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

	/**
	 * Gets an enchantment by name
	 *
	 * @param enchantmentName The name of the enchantment
	 * @return The enchantment with the specified name
	 */
	public static Optional<CppEnchantmentBase> getByName(String enchantmentName) {
		return CppEnchantmentBase.cppEnchantments.stream().filter(enchantment -> enchantment.getName().replaceFirst("enchantment\\.", "").equals(enchantmentName)).findFirst();
	}

	/**
	 * Performs the action of the enchantment with the specified name
	 *
	 * @param enchantmentName The name of the enchantment
	 * @param entity          The entity to go along with the enchantment
	 * @param baseEvent       The event to go along with the enchantment
	 */
	public static void performAction(String enchantmentName, Entity entity, Event baseEvent) {
		Optional<CppEnchantmentBase> cppEnchantment = getByName(enchantmentName);
		if (cppEnchantment.isPresent())
			cppEnchantment.get().performAction(entity, baseEvent);
	}
}
