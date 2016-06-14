package io.github.anon10w1z.cpp.items;

import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Zooms in the FOV when worn
 */
public class ItemBinoculars extends ItemArmor {
	public ItemBinoculars() {
		super(EnumHelper.addArmorMaterial("BINOCULARS", "", 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.item_armor_equip_iron), 0, EntityEquipmentSlot.HEAD);
		this.setUnlocalizedName("binoculars");
		this.setRegistryName(CppModInfo.MOD_ID, "binoculars");
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return CppModInfo.MOD_ID + ":textures/armor/binoculars.png";
	}
}
