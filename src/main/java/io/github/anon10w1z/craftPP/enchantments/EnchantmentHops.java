package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Gives the wearer jump boost
 */
@TickingEnchantment
public class EnchantmentHops extends CppEnchantmentBase {
	public EnchantmentHops() {
		super("hops", 2, EnumEnchantmentType.ARMOR_FEET);
	}

	@Override
	public String getCppEnchantmentName() {
		return "hops";
	}

	@Override
	public void performAction(EntityPlayer player, Event baseEvent) {
		int enchantmentLevel = this.getEnchantmentLevel(player.getCurrentArmor(0));
		if (enchantmentLevel > 0)
			player.addPotionEffect(new PotionEffect(Potion.jump.id, 2, enchantmentLevel - 1));
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}
}
