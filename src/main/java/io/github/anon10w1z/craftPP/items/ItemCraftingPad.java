package io.github.anon10w1z.craftPP.items;

import io.github.anon10w1z.craftPP.main.CraftPlusPlus;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.world.World;

/**
 * A portable crafting pad that allows you to craft items on the go
 */
public class ItemCraftingPad extends Item {
	public ItemCraftingPad() {
		super();
		this.setUnlocalizedName("craftingPad");
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote)
			player.openGui(CraftPlusPlus.instance, 0, world, 0, 0, 0);
		return itemstack;
	}

	@Override
	public void onCreated(ItemStack itemstack, World world, EntityPlayer player) {
		player.triggerAchievement(AchievementList.buildWorkBench);
	}
}
