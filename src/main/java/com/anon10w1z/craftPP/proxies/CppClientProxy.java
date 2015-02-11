package com.anon10w1z.craftPP.proxies;

import com.anon10w1z.craftPP.blocks.CppBlocks;
import com.anon10w1z.craftPP.main.CppReferences;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * The client-side proxy for Craft++. Contains all of the rendering code.
 *
 * @author Anon10W1z
 */
@SuppressWarnings("unused")
public class CppClientProxy extends CppCommonProxy {
	@Override
	public void registerBlockInventoryRenderers() {
		registerBlockInventoryRenderer(CppBlocks.flint_block, "flint");
		registerBlockInventoryRenderer(CppBlocks.sugar_block, "sugar");
		registerBlockInventoryRenderer(CppBlocks.charcoal_block, "charcoal");
	}

	/**
	 * Registers a renderer for a block in the inventory
	 *
	 * @param block      - The block
	 * @param namePrefix - The name prefix of the block
	 */
	private void registerBlockInventoryRenderer(Block block, String namePrefix) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(CppReferences.MOD_ID + ":" + namePrefix + "_block", "inventory"));
	}
}
