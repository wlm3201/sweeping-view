package com.sweepingview;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entry point. The mod is client only, so this exists just to own the mod id - which the
 * client side builds its config path from - and the shared logger.
 */
public class SweepingView implements ModInitializer {
	public static final String MOD_ID = "sweeping-view";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Sweeping View loaded.");
	}
}
