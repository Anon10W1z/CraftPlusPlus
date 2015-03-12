package com.anon10w1z.craftPP.misc;

import com.anon10w1z.craftPP.main.CppReferences;
import com.anon10w1z.craftPP.main.CraftPlusPlus;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Contains the key bindings for Craft++
 */
public class CppKeyBindings {
	@SideOnly(Side.CLIENT)
	public static KeyBinding potionKey;

	/**
	 * Registers the key bindings for Craft++
	 */
	public static void registerKeyBindings() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			potionKey = new KeyBinding(CppReferences.MOD_ID + ".potionDisplay", Keyboard.KEY_P, "key.categories.ui");
			ClientRegistry.registerKeyBinding(potionKey);
		} else
			CraftPlusPlus.logInfo("Did not register key bindings; we are on the server side!");
	}
}
