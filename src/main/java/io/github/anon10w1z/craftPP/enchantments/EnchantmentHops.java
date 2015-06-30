package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

/**
 * Gives the wearer jump boost
 */
@SuppressWarnings("unused")
public class EnchantmentHops extends CppEnchantmentBase {
	private static UUID hopsUUID = UUID.fromString("ae3d3fe4-2b1a-46bb-a27d-4694c4aaca0a");

	public EnchantmentHops() {
		super("hops", 2, EnumEnchantmentType.ARMOR_FEET);
	}

	@Override
	public String getCppEnchantmentName() {
		return "hops";
	}

	@Override
	public void performAction(Entity entity, Event baseEvent) {
		int enchantmentLevel = this.getEnchantmentLevel(((EntityLivingBase) entity).getEquipmentInSlot(1));
		entity.motionY += (float) enchantmentLevel / 15;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
