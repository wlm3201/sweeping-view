package com.sweepingview.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screens.Screen;

/**
 * Puts the settings screen behind Mod Menu's "config" button, which is the only way in - the mod
 * deliberately registers no command.
 *
 * <p>Mod Menu finds this through the {@code modmenu} entrypoint and only ever loads it when it is
 * itself installed. The dependency is declared as {@code suggests}, so neither mod needs the other
 * to launch.
 */
public class SweepingViewModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		// Mod Menu passes the mod list as the parent, so the back button returns there.
		// YACL screens are single use, so createScreen builds a fresh one per open.
		return SweepingViewConfig::createScreen;
	}
}
