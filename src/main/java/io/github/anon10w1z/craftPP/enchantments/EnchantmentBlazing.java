package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Random;

/**
 * Automatically smelts harvested blocks
 */
public class EnchantmentBlazing extends CppEnchantmentBase {
	private static Random random = new Random();

	public EnchantmentBlazing() {
		super("blazing", 1, EnumEnchantmentType.DIGGER);
	}

	@Override
	public String getCppEnchantmentName() {
		return "blazing";
	}

	@Override
	public void performAction(EntityPlayer player, Event baseEvent) {
		if (player != null && !player.capabilities.isCreativeMode && EnchantmentHelper.getEnchantmentLevel(this.effectId, player.getHeldItem()) > 0) {
			HarvestDropsEvent event = (HarvestDropsEvent) baseEvent;
			ItemStack drop = event.drops.size() == 0 ? null : event.drops.get(0);
			if (drop != null) {
				ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(drop);
				if (smeltResult != null) {
					smeltResult = smeltResult.copy();
					smeltResult.stackSize *= drop.stackSize;
					int fortuneModifier = EnchantmentHelper.getFortuneModifier(player);
					if (fortuneModifier > 0)
						smeltResult.stackSize *= random.nextInt(fortuneModifier + 1) + 1;
					event.drops.clear();
					event.drops.add(smeltResult);
				}
			}
		}
	}

	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return enchantment != Enchantment.silkTouch;
	}
}
