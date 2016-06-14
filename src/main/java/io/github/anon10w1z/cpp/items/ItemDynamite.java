package io.github.anon10w1z.cpp.items;

import io.github.anon10w1z.cpp.entities.EntityDynamite;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * Dynamite item to go along-side dynamite entity
 */
public class ItemDynamite extends Item {
	public ItemDynamite() {
		this.setUnlocalizedName("dynamite");
		this.setRegistryName(CppModInfo.MOD_ID, "dynamite");
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setMaxStackSize(16);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!player.capabilities.isCreativeMode)
			--itemStack.stackSize;
		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.entity_snowball_throw, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		if (!world.isRemote) {
			EntityDynamite dynamite = new EntityDynamite(world, player);
			dynamite.func_184538_a(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 0);
			world.spawnEntityInWorld(dynamite);
		}
		player.addStat(StatList.func_188057_b(this));
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
	}
}
