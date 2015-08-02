package io.github.anon10w1z.cpp.proxies;

import io.github.anon10w1z.cpp.blocks.CppBlocks;
import io.github.anon10w1z.cpp.entities.EntityDynamite;
import io.github.anon10w1z.cpp.entities.EntityObsidianBoat;
import io.github.anon10w1z.cpp.entities.RenderObsidianBoat;
import io.github.anon10w1z.cpp.items.CppItems;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

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
		RenderingRegistry.registerEntityRenderingHandler(EntityObsidianBoat.class, new RenderObsidianBoat());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void displayPotionEffects() {
		int xPos = 2;
		int yPos = 2;
		Collection<PotionEffect> potionEffects = minecraft.thePlayer.getActivePotionEffects();
		for (PotionEffect potionEffect : potionEffects) {
			Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
			if (potion.hasStatusIcon()) {
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glDisable(GL11.GL_LIGHTING);
				minecraft.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/inventory.png")); //draw from inventory.png
				int iconIndex = potion.getStatusIconIndex();
				//constants for drawing
				final int POTION_ICON_SIZE = 18;
				final int POTION_ICON_BASE_X_OFFSET = 0;
				final int POTION_ICON_BASE_Y_OFFSET = 198;
				final int POTION_ICONS_PER_ROW = 8;
				final int POTION_ICON_SPACING = POTION_ICON_SIZE + 2;

				new Gui().drawTexturedModalRect(xPos, yPos, POTION_ICON_BASE_X_OFFSET + iconIndex % POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_BASE_Y_OFFSET + iconIndex / POTION_ICONS_PER_ROW * POTION_ICON_SIZE, POTION_ICON_SIZE, POTION_ICON_SIZE);
				xPos += POTION_ICON_SPACING;
			}
		}
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
		return minecraft.currentScreen != null;
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

