package io.github.anon10w1z.craftPP.enchantments;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Base class for all of Craft++'s enchantments
 */
public abstract class CppEnchantmentBase extends Enchantment {
	/**
	 * A list of all of Craft++'s enchantments
	 */
	public static List<CppEnchantmentBase> cppEnchantments = Lists.newArrayList();

	public CppEnchantmentBase(String name, int weight, EnumEnchantmentType type) {
		super(findFreeEnchantmentID(name), new ResourceLocation(name), weight, type);
		this.setName(name);
		addToBookList(this);
		cppEnchantments.add(this);
	}

	/**
	 * Finds the first free enchantment ID to register this enchantment
	 *
	 * @param enchantmentName the name of the enchantment
	 * @return The enchantment ID for this enchantment to use
	 */
	private static int findFreeEnchantmentID(String enchantmentName) {
		OptionalInt freeEnchantmentID = IntStream.range(0, 256).filter(i -> Enchantment.getEnchantmentById(i) == null).findFirst();
		if (!freeEnchantmentID.isPresent())
			throw new NoFreeEnchantmentIDException(enchantmentName);
		return freeEnchantmentID.getAsInt();
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
	public int getMinEnchantability(int enchantmentLevel) {
		return getMinimumEnchantability(enchantmentLevel);
	}

	@Override
	public int getMaxEnchantability(int enchantmentLevel) {
		return getMaximumEnchantability(enchantmentLevel);
	}

	/**
	 * Performs the action this enchantment does
	 *
	 * @param entity    The entity related to the event
	 * @param baseEvent The event that relates to this enchantment
	 */
	public abstract void performAction(Entity entity, Event baseEvent);

	public abstract int getMinimumEnchantability(int enchantmentLevel);

	public abstract int getMaximumEnchantability(int enchantmentLevel);

	private static class NoFreeEnchantmentIDException extends RuntimeException {
		private NoFreeEnchantmentIDException(String enchantmentName) {
			super("Could not find a free enchantment ID for " + StatCollector.translateToLocal("enchantment." + enchantmentName));
		}
	}
}
