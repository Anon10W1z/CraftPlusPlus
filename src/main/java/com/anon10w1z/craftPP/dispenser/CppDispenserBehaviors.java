package com.anon10w1z.craftPP.dispenser;

import com.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.anon10w1z.craftPP.main.CppUtils;
import com.anon10w1z.craftPP.main.CraftPlusPlus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.util.StatCollector;

public class CppDispenserBehaviors {
	/**
	 * Registers the dispenser behaviors for Craft++
	 */
	@SuppressWarnings("unchecked")
	public static void registerDispenserBehaviors() {
		for (Item item : CppUtils.getArray(Item.itemRegistry, Item.class)) {
			//Falling Blocks
			if (item instanceof ItemBlock) {
				Block block = ((ItemBlock) item).block;
				Material blockMaterial = block.getMaterial();
				if (block instanceof BlockFalling && (blockMaterial == Material.sand || blockMaterial == Material.snow || blockMaterial == Material.craftedSnow || blockMaterial == Material.clay))
					registerDispenserBehavior(item, new BehaviorDispenseBlockFalling());
			}
			//Flint And Steel (default behavior)
			if (item instanceof ItemFlintAndSteel && !CppConfigHandler.enableFlintAndSteelDispenserBehavior)
				registerDispenserBehavior(item, new BehaviorDefaultDispenseItem());
		}
	}

	/**
	 * Registers a dispenser behavior for an item with the game
	 *
	 * @param item              The item dispensed by the dispenser
	 * @param dispenserBehavior The dispenser behavior carried out for the item
	 */
	private static void registerDispenserBehavior(Item item, BehaviorDefaultDispenseItem dispenserBehavior) {
		String localizedName = StatCollector.translateToLocal(item.getUnlocalizedName() + ".name");
		if (dispenserBehavior.getClass() != BehaviorDefaultDispenseItem.class)
			CraftPlusPlus.logInfo("Registering dispenser behavior for " + localizedName);
		else
			CraftPlusPlus.logInfo("Registering default dispenser behavior for " + localizedName);
		BlockDispenser.dispenseBehaviorRegistry.putObject(item, dispenserBehavior);
	}
}
