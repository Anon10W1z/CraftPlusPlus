package io.github.anon10w1z.craftPP.enchantments;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Optional;

/**
 * Base class for all of Craft++'s enchantments
 */
public abstract class CppEnchantmentBase extends Enchantment {
	/**
	 * A list of all of Craft++'s enchantments
	 */
	public static List<CppEnchantmentBase> cppEnchantments = Lists.newArrayList();

	public CppEnchantmentBase(String resourceName, int weight, EnumEnchantmentType type) {
		super(findFreeEnchantmentID(), new ResourceLocation(resourceName), weight, type);
		this.setName(getCppEnchantmentName());
		addToBookList(this);
		cppEnchantments.add(this);
	}

	/**
	 * Gets an enchantment by name
	 * @param name The name of the enchantment
	 * @return The enchantment with the specified name
	 */
	public static Optional<CppEnchantmentBase> getByName(String name) {
		return cppEnchantments.stream().filter(enchantment -> enchantment.name.equals(name)).findFirst();
	}

	/**
	 * Finds the first free enchantment ID to register this enchantment
	 * @return The enchantment ID for this enchantment to use
	 */
	private static int findFreeEnchantmentID() {
		int enchantmentID = 0;
		while (Enchantment.getEnchantmentById(enchantmentID) != null)
			++enchantmentID;
		return enchantmentID;
	}

	/**
	 * Gets the enchantment level of this enchantment on the specified ItemStack
	 *
	 * @param itemstack The ItemStack to check
	 * @return The enchantment level of this enchantment on the ItemStack
	 */
	protected int getEnchantmentLevel(ItemStack itemstack) {
		return EnchantmentHelper.getEnchantmentLevel(this.effectId, itemstack);
	}

	@Override
	public int getMinEnchantability(int level) {
		return 5 + (level - 1) * 10;
	}

	@Override
	public int getMaxEnchantability(int level) {
		return this.getMinEnchantability(level) + 20;
	}

	/**
	 * Gets the name of this enchantment
	 * @return The name of this enchantment
	 */
	public abstract String getCppEnchantmentName();

	/**
	 * Performs the action this enchantment does
	 * @param player The player related to the event
	 * @param baseEvent The event that relates to this enchantment
	 */
	public abstract void performAction(EntityPlayer player, Event baseEvent);
}
