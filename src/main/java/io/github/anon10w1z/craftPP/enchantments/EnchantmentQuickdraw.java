package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Instantly draws back a bow
 */
public class EnchantmentQuickdraw extends CppEnchantmentBase {
	public EnchantmentQuickdraw() {
		super("quickdraw", 1, EnumEnchantmentType.BOW);
	}

	@Override
	public String getCppEnchantmentName() {
		return "quickdraw";
	}

	@Override
	public void performAction(EntityPlayer player, Event baseEvent) {
		ItemStack heldItem = player.getHeldItem();
		if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
			player.setItemInUse(player.getHeldItem(), heldItem.getMaxItemUseDuration() / 3);
			((ArrowNockEvent) baseEvent).result = heldItem;
			baseEvent.setCanceled(true);
		}
	}

	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return enchantment != Enchantment.infinity && enchantment != Enchantment.flame;
	}
}
