package com.anon10w1z.craftPP.gui;

import com.anon10w1z.craftPP.handlers.CppConfigHandler;
import com.anon10w1z.craftPP.main.CppModInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * The config GUI for Craft++
 */
public class CppGuiConfig extends GuiConfig {
	public CppGuiConfig(GuiScreen parent) {
		super(parent, new ConfigElement(CppConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), CppModInfo.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(CppConfigHandler.config.toString()));
	}
}
