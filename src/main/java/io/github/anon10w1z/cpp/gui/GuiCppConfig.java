package io.github.anon10w1z.cpp.gui;

import io.github.anon10w1z.cpp.handlers.CppConfigHandler;
import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * The config GUI for Craft++
 */
public class GuiCppConfig extends GuiConfig {
	public GuiCppConfig() {
		this(null);
	}

	public GuiCppConfig(GuiScreen parent) {
		super(parent, new ConfigElement(CppConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), CppModInfo.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(CppConfigHandler.config.toString()));
	}
}
