package io.github.anon10w1z.craftPP.enchantments;

/**
 * Initializes Craft++'s enchantments
 */
public final class CppEnchantments {
	static {
		new EnchantmentNimble();
		new EnchantmentHops();
		new EnchantmentBlazing();
		new EnchantmentQuickdraw();
	}

	/**
	 * Registers the enchantments for Craft++
	 */
	public static void registerEnchantments() {
		//dummy method for static initializer
	}
}
