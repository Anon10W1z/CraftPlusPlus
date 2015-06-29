package io.github.anon10w1z.craftPP.enchantments;

import io.github.anon10w1z.craftPP.misc.CppUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Random;

/**
 * Automatically smelts the drops of harvested blocks
 */
@SuppressWarnings("unused")
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
	public void performAction(EntityLivingBase entityLivingBase, Event baseEvent) {
		if (entityLivingBase != null && this.getEnchantmentLevel(entityLivingBase.getHeldItem()) > 0) {
			HarvestDropsEvent event = (HarvestDropsEvent) baseEvent;
			List<ItemStack> drops = event.drops;
			List<ItemStack> dropsCopy = CppUtils.copyList(event.drops);
			drops.clear();
			for (ItemStack drop : dropsCopy)
				if (drop != null) {
					ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(drop);
					if (smeltingResult != null) {
						smeltingResult = smeltingResult.copy();
						smeltingResult.stackSize *= drop.stackSize;
						int fortuneLevel = event.fortuneLevel;
						if (!(smeltingResult.getItem() instanceof ItemBlock))
							smeltingResult.stackSize *= random.nextInt(fortuneLevel + 1) + 1;
						drops.add(smeltingResult);
					} else
						drops.add(drop);
				}
		}
	}

	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return super.canApplyTogether(enchantment) && enchantment != Enchantment.silkTouch;
	}
}
