package io.github.anon10w1z.cpp.proxies;

import io.github.anon10w1z.cpp.blocks.CppBlocks;
import io.github.anon10w1z.cpp.entities.EntityDynamite;
import io.github.anon10w1z.cpp.entities.EntityObsidianBoat;
import io.github.anon10w1z.cpp.entities.EntitySitPoint;
import io.github.anon10w1z.cpp.entities.EntityStoneBoat;
import io.github.anon10w1z.cpp.entities.render.RenderObsidianBoat;
import io.github.anon10w1z.cpp.entities.render.RenderSitPoint;
import io.github.anon10w1z.cpp.entities.render.RenderStoneBoat;
import io.github.anon10w1z.cpp.items.CppItems;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.input.Keyboard;

/**
 * The client-side proxy for Craft++
 */
@SuppressWarnings("unused")
public class CppClientProxy extends CppCommonProxy {
	private static final Minecraft minecraft = Minecraft.getMinecraft(); //the Minecraft instance
	private static KeyBinding potionKey; //the key binding for toggling the potion overlay

	@Override
	public void registerRenderers() {
		//Items
		this.registerItemInventoryRenderer(CppItems.dynamite, "dynamite");
		this.registerItemInventoryRenderer(CppItems.stone_boat, "stone_boat");
		this.registerItemInventoryRenderer(CppItems.obsidian_boat, "obsidian_boat");
		this.registerItemInventoryRenderer(CppItems.fried_egg, "egg_fried");
		this.registerItemInventoryRenderer(CppItems.sponge_wipe, "sponge_wipe");
		this.registerItemInventoryRenderer(CppItems.crafting_pad, "crafting_pad");
		this.registerItemInventoryRenderer(CppItems.binocular_lens, "binocular_lens");
		this.registerItemInventoryRenderer(CppItems.binoculars, "binoculars");
		//Blocks
		this.registerBlockInventoryRenderer(CppBlocks.flint_block, "flint");
		this.registerBlockInventoryRenderer(CppBlocks.sugar_block, "sugar");
		this.registerBlockInventoryRenderer(CppBlocks.charcoal_block, "charcoal");
		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, new RenderSnowball(minecraft.getRenderManager(), CppItems.dynamite, minecraft.getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityStoneBoat.class, new RenderStoneBoat());
		RenderingRegistry.registerEntityRenderingHandler(EntityObsidianBoat.class, new RenderObsidianBoat());
		RenderingRegistry.registerEntityRenderingHandler(EntitySitPoint.class, new RenderSitPoint());
	}

	@Override
	public void registerKeyBindings() {
		potionKey = new KeyBinding("key.potionDisplay", Keyboard.KEY_P, "key.categories.ui");
		ClientRegistry.registerKeyBinding(potionKey);
	}

	@Override
	public boolean isPotionKeyPressed() {
		return potionKey.isPressed();
	}

	@Override
	public boolean isGuiOpen() {
		return minecraft.currentScreen != null && !(minecraft.currentScreen instanceof GuiChat);
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

