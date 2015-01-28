package com.anon10w1z.craftPP.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCppStorage extends Block {
	protected BlockCppStorage(Material material, String namePrefix) {
		super(material);
		this.setUnlocalizedName(namePrefix + "Block");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
}
