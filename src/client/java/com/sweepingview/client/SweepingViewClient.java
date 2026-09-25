package com.sweepingview.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class SweepingViewClient implements ClientModInitializer {
	@Override
	// 26.2 moved this event to LevelExtractionEvents, but that class does not exist yet in the
	// Fabric API built for 26.1.2 - and this source tree is compiled against both. The old
	// LevelRenderEvents field is a deprecated forwarder that points at the very same event on
	// 26.2, so going through it is what lets one code base serve both versions.
	@SuppressWarnings("deprecation")
	public void onInitializeClient() {
		// Read config/sweeping-view.json5 before anything asks for a value, so the very first frame
		// already uses the saved settings instead of the field defaults.
		SweepingViewConfig.HANDLER.load();

		// Gizmos are collected during the level extraction phase and rendered by the vanilla
		// LevelRenderer, so we simply submit our shapes while extraction is running.
		LevelRenderEvents.END_EXTRACTION.register(SweepRangeRenderer::extract);
	}
}
