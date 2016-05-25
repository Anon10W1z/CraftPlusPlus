package io.github.anon10w1z.cpp.items;

import io.github.anon10w1z.cpp.entities.EntityDynamite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Dynamite item to go along-side dynamite entity
 */
public class ItemDynamite extends Item {
	public ItemDynamite() {
		super();
		this.setUnlocalizedName("dynamite");
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setMaxStackSize(16);
		this.setFull3D();
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player,
			EnumHand hand) {
	
		if (!player.capabilities.isCreativeMode)
			--itemStack.stackSize;
		player.playSound( SoundEvents.entity_arrow_shoot, 0.5F, 0.4F);
		if (!world.isRemote)
			world.spawnEntityInWorld(new EntityDynamite(world, player));
		//Im not sure which achievement you want here TODO: put in right achievment
		player.addStat(AchievementList.);
		player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
		
	}

	}
