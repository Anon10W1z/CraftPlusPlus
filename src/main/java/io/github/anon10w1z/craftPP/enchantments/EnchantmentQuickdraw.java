package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Instantly draws back a bow
 */
@SuppressWarnings("unused")
public class EnchantmentQuickdraw extends CppEnchantmentBase {
	public EnchantmentQuickdraw() {
		super("quickdraw", 1, EnumEnchantmentType.BOW);
	}

	@Override
	public String getCppEnchantmentName() {
		return "quickdraw";
	}

	@Override
	public void performAction(EntityLivingBase entityLivingBase, Event baseEvent) {
		ItemStack heldItem = entityLivingBase.getHeldItem();
		if (heldItem != null && heldItem.getItem() instanceof ItemBow && getEnchantmentLevel(heldItem) > 0) {
			((EntityPlayer) entityLivingBase).setItemInUse(entityLivingBase.getHeldItem(), heldItem.getMaxItemUseDuration() / 3);
			((ArrowNockEvent) baseEvent).result = heldItem;
			baseEvent.setCanceled(true);
		}
	}

	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return super.canApplyTogether(enchantment) && enchantment != Enchantment.infinity && enchantment != Enchantment.flame;
	}
}
