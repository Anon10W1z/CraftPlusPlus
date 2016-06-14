package io.github.anon10w1z.cpp.items;

import io.github.anon10w1z.cpp.main.CppModInfo;
import io.github.anon10w1z.cpp.main.CraftPlusPlus;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * A portable crafting pad that allows you to craft items on the go
 */
public class ItemCraftingPad extends Item {
	public ItemCraftingPad() {
		super();
		this.setUnlocalizedName("craftingPad");
		this.setRegistryName(CppModInfo.MOD_ID, "crafting_pad");
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote)
			player.openGui(CraftPlusPlus.instance, 0, world, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

}
