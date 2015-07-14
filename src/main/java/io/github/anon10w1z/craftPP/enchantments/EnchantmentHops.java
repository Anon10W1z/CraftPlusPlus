package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Gives the wearer jump boost
 */
@SuppressWarnings("unused")
public class EnchantmentHops extends CppEnchantmentBase {
	public EnchantmentHops() {
		super("hops", 2, EnumEnchantmentType.ARMOR_FEET);
	}

	@Override
	public void performAction(Entity entity, Event baseEvent) {
		int enchantmentLevel = this.getEnchantmentLevel(((EntityLivingBase) entity).getEquipmentInSlot(1));
		entity.motionY += (float) enchantmentLevel / 15;
	}

	@Override
	public int getMinimumEnchantability(int enchantmentLevel) {
		return 5 + (enchantmentLevel - 1) * 8;
	}

	@Override
	public int getMaximumEnchantability(int enchantmentLevel) {
		return enchantmentLevel * 10 + 51;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
