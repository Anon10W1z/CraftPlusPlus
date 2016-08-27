package io.github.anon10w1z.cpp.blocks;

import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * A base storage block class for Craft++'s storage blocks
 */
public class BlockCppStorage extends Block {
	protected BlockCppStorage(Material material, String namePrefix) {
		super(material);
		this.setUnlocalizedName(namePrefix + "Block");
		this.setRegistryName(CppModInfo.MOD_ID, namePrefix + "_block");
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public Block setSoundType(SoundType sound) {
		return super.setSoundType(sound);
	}
}
