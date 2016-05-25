package io.github.anon10w1z.cpp.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
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
		super(Rarity.VERY_RARE, EnumEnchantmentType.BOW, EntityEquipmentSlot.MAINHAND);
	}

	@Override
	public void performAction(Entity entity, Event baseEvent) {
		EntityPlayer player = (EntityPlayer) entity;
		ItemStack heldItem = player.getHeldItemMainhand();
		if (heldItem != null && heldItem.getItem() instanceof ItemBow && this.getEnchantmentLevel(heldItem) > 0) {
			player.setItemInUse(heldItem, heldItem.getMaxItemUseDuration() / 3);
			
			((ArrowNockEvent) baseEvent).result = heldItem;
			baseEvent.setCanceled(true);
		}
	}

	@Override
	public int getMinimumEnchantability(int enchantmentLevel) {
		return 20;
	}

	@Override
	public int getMaximumEnchantability(int enchantmentLevel) {
		return 50;
	}

	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return super.canApplyTogether(enchantment) && enchantment != Enchantments.infinity;
	}
}
