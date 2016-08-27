package io.github.anon10w1z.cpp.blocks;

import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;

public class BlockSugar extends BlockFalling {
	public BlockSugar() {
		super();
		this.setUnlocalizedName("sugarBlock");
		this.setRegistryName(CppModInfo.MOD_ID, "sugar_block");
		this.setSoundType(SoundType.SAND);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
	}
}
