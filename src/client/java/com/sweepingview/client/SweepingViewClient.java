package com.sweepingview.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SweepingViewClient implements ClientModInitializer {
    public static final String MOD_ID = "sweepingview";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {
        SweepingViewConfig.HANDLER.load();

        LevelRenderEvents.END_EXTRACTION.register(SweepRangeRenderer::extract);
    }
}
