package io.github.anon10w1z.craftPP.enchantments;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Optional;

/**
 * Base class for all of Craft++'s enchantments
 */
public abstract class CppEnchantmentBase extends Enchantment {
	public static List<CppEnchantmentBase> cppEnchantments = Lists.newArrayList();

	public CppEnchantmentBase(String resourceName, int weight, EnumEnchantmentType type) {
		super(findFreeEnchantmentID(), new ResourceLocation(resourceName), weight, type);
		this.setName(getCppEnchantmentName());
		addToBookList(this);
		cppEnchantments.add(this);
	}

	public static Optional<CppEnchantmentBase> getByName(String name) {
		return cppEnchantments.stream().filter(enchantment -> enchantment.name.equals(name)).findFirst();
	}

	private static int findFreeEnchantmentID() {
		int enchantmentID = 0;
		while (Enchantment.getEnchantmentById(enchantmentID) != null)
			++enchantmentID;
		return enchantmentID;
	}

	@Override
	public int getMinEnchantability(int level) {
		return 5 + (level - 1) * 10;
	}

	@Override
	public int getMaxEnchantability(int level) {
		return this.getMinEnchantability(level) + 20;
	}

	public abstract String getCppEnchantmentName();

	public abstract void performAction(EntityPlayer player, Event baseEvent);
}
