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
public class CppClientProxy extends CppCommonProxy {
	@Override
	public void registerBlockInventoryRenderers() {
		registerBlockInventoryRenderer(CppBlocks.flint_block, "flint_block");
		registerBlockInventoryRenderer(CppBlocks.sugar_block, "sugar_block");
		registerBlockInventoryRenderer(CppBlocks.charcoal_block, "charcoal_block");
	}

	/**
	 * Registers a renderer for a block in the inventory, regardless of metadata
	 *
	 * @param block     - The block
	 * @param blockName - The name of the block
	 */
	private void registerBlockInventoryRenderer(Block block, String blockName) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(CppReferences.MOD_ID + ":" + blockName, "inventory"));
	}
}
