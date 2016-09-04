package io.github.anon10w1z.cpp.proxies;

import io.github.anon10w1z.cpp.blocks.CppBlocks;
import io.github.anon10w1z.cpp.entities.EntityDynamite;
import io.github.anon10w1z.cpp.entities.EntityObsidianBoat;
import io.github.anon10w1z.cpp.entities.render.RenderObsidianBoat;
import io.github.anon10w1z.cpp.items.CppItems;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/**
 * The client-side proxy for Craft++
 */
@SuppressWarnings("unused")
public class CppClientProxy extends CppCommonProxy {
	private static final Minecraft minecraft = Minecraft.getMinecraft(); //the Minecraft instance

	@SuppressWarnings({"deprecation", "unchecked"})
	@Override
	public void registerRenderers() {
		//Items
		this.registerItemInventoryRenderer(CppItems.DYNAMITE, "dynamite");
		this.registerItemInventoryRenderer(CppItems.STONE_BOAT, "stone_boat");
		this.registerItemInventoryRenderer(CppItems.OBSIDIAN_BOAT, "obsidian_boat");
		this.registerItemInventoryRenderer(CppItems.FRIED_EGG, "egg_fried");
		this.registerItemInventoryRenderer(CppItems.CRAFTING_PAD, "crafting_pad");
		this.registerItemInventoryRenderer(CppItems.BINOCULAR_LENS, "binocular_lens");
		this.registerItemInventoryRenderer(CppItems.BINOCULARS, "binoculars");
		//Blocks
		this.registerBlockInventoryRenderer(CppBlocks.FLINT_BLOCK, "flint");
		this.registerBlockInventoryRenderer(CppBlocks.SUGAR_BLOCK, "sugar");
		this.registerBlockInventoryRenderer(CppBlocks.CHARCOAL_BLOCK, "charcoal");
		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, new RenderSnowball(minecraft.getRenderManager(), CppItems.DYNAMITE, minecraft.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityObsidianBoat.class, new RenderObsidianBoat(minecraft.getRenderManager()));
	}

	private void registerItemInventoryRenderer(Item item, String name) {
		minecraft.getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(CppModInfo.MOD_ID + ":" + name, "inventory"));
	}

	/**
	 * Registers a renderer for a block in the inventory
	 *
	 * @param block      The block
	 * @param namePrefix The name prefix of the block
	 */
	private void registerBlockInventoryRenderer(Block block, String namePrefix) {
		minecraft.getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(CppModInfo.MOD_ID + ":" + namePrefix + "_block", "inventory"));
	}
}

