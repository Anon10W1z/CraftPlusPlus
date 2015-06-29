package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

/**
 * Gives the wearer speed
 */
@SuppressWarnings("unused")
@LivingTickingEnchantment
public class EnchantmentNimble extends CppEnchantmentBase {
	private static UUID nimbleUUID = UUID.fromString("05b61a62-ae84-492e-8536-f365b7143296");
	private static float nimbleAmount = 1 / 5;

	public EnchantmentNimble() {
		super("nimble", 2, EnumEnchantmentType.ARMOR_FEET);
	}

	@Override
	public String getCppEnchantmentName() {
		return "nimble";
	}

	@Override
	public void performAction(EntityLivingBase entityLivingBase, Event baseEvent) {
		int enchantmentLevel = this.getEnchantmentLevel(entityLivingBase.getEquipmentInSlot(1));
		if (enchantmentLevel > 0)
			addSpeedBuff(entityLivingBase, enchantmentLevel);
		else
			removeSpeedBuff(entityLivingBase, enchantmentLevel);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	private void addSpeedBuff(EntityLivingBase entityLivingBase, int enchantmentLevel) {
		IAttributeInstance speedAttribute = entityLivingBase.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
		if (speedAttribute.getModifier(nimbleUUID) == null) {
			AttributeModifier speedModifier = new AttributeModifier(nimbleUUID, "NimbleBoots", (float) 1 / 5 * enchantmentLevel, 1);
			speedAttribute.applyModifier(speedModifier);
		}
	}

	private void removeSpeedBuff(EntityLivingBase entityLivingBase, int enchantmentLevel) {
		IAttributeInstance speedAttribute = entityLivingBase.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
		if (speedAttribute.getModifier(nimbleUUID) != null) {
			AttributeModifier speedModifier = new AttributeModifier(nimbleUUID, "NimbleBoots", (float) 1 / 5 * enchantmentLevel, 1);
			speedAttribute.removeModifier(speedModifier);
		}
	}
}
