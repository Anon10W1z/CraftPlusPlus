package io.github.anon10w1z.craftPP.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Automatically smelts the drops of harvested blocks
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
		if (player != null && this.getEnchantmentLevel(player.getHeldItem()) > 0) {
			HarvestDropsEvent event = (HarvestDropsEvent) baseEvent;
			List<ItemStack> drops = event.drops;
			List<ItemStack> dropsCopy = new ArrayList<>(drops);
			drops.clear();
			for (ItemStack drop : dropsCopy)
				if (drop != null) {
					ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(drop);
					if (smeltResult != null) {
						smeltResult = smeltResult.copy();
						smeltResult.stackSize *= drop.stackSize;
						int fortuneLevel = event.fortuneLevel;
						if (!(smeltResult.getItem() instanceof ItemBlock))
							smeltResult.stackSize *= random.nextInt(fortuneLevel + 1) + 1;
						drops.add(smeltResult);
					} else
						drops.add(drop);
				}
		}
	}

	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return enchantment != Enchantment.silkTouch;
	}
}
