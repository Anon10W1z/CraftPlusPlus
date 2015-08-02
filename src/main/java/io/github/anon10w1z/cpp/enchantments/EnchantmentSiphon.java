package io.github.anon10w1z.cpp.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gives you the drops directly when harvesting blocks
 */
@SuppressWarnings("unused")
public class EnchantmentSiphon extends CppEnchantmentBase {
	public EnchantmentSiphon() {
		super("siphon", 2, EnumEnchantmentType.DIGGER);
	}

	@Override
	public void performAction(Entity entity, Event baseEvent) {
		if (entity != null && this.getEnchantmentLevel(((EntityLivingBase) entity).getHeldItem()) > 0) {
			HarvestDropsEvent event = (HarvestDropsEvent) baseEvent;
			List<ItemStack> drops = event.drops;
			drops.removeAll(drops.stream().filter(event.harvester.inventory::addItemStackToInventory).collect(Collectors.toList()));
		}
	}

	@Override
	public int getMinimumEnchantability(int enchantmentLevel) {
		return 15;
	}

	@Override
	public int getMaximumEnchantability(int enchantmentLevel) {
		return 61;
	}
}
