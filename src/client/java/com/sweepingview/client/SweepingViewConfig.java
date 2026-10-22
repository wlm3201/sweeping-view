package com.sweepingview.client;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SweepingViewConfig {
    private static final boolean DEFAULT_BOX_STROKE_ENABLED = true;

    private static final boolean DEFAULT_BOX_FILL_ENABLED = true;
    private static final boolean DEFAULT_SPHERE_ENABLED = true;

    private static final Color DEFAULT_STROKE_COLOR = new Color(255, 80, 80, 0xFF);

    private static final Color DEFAULT_FILL_COLOR = new Color(255, 80, 80, 0x40);

    public static final ConfigClassHandler<SweepingViewConfig> HANDLER =
            ConfigClassHandler.createBuilder(SweepingViewConfig.class)
                    .id(Identifier.fromNamespaceAndPath(SweepingViewClient.MOD_ID, "config"))
                    .serializer(
                            handler ->
                                    GsonConfigSerializerBuilder.create(handler)
                                            .setPath(
                                                    FabricLoader.getInstance()
                                                            .getConfigDir()
                                                            .resolve(
                                                                    SweepingViewClient.MOD_ID
                                                                            + ".json5"))
                                            .setJson5(true)
                                            .build())
                    .build();

    @SerialEntry(comment = "Draw the outline of the sweep box.")
    public boolean boxStrokeEnabled = DEFAULT_BOX_STROKE_ENABLED;

    @SerialEntry(comment = "ARGB colour of the sweep box outline.")
    public Color boxStrokeColor = DEFAULT_STROKE_COLOR;

    @SerialEntry(comment = "Fill the sweep box with a translucent colour.")
    public boolean boxFillEnabled = DEFAULT_BOX_FILL_ENABLED;

    @SerialEntry(comment = "ARGB colour of the sweep box faces. The alpha byte sets their density.")
    public Color boxFillColor = DEFAULT_FILL_COLOR;

    @SerialEntry(comment = "Draw the reach sphere around the attacker.")
    public boolean sphereEnabled = DEFAULT_SPHERE_ENABLED;

    @SerialEntry(comment = "ARGB colour of the reach sphere. The alpha byte sets its density.")
    public Color sphereColor = DEFAULT_FILL_COLOR;

    public static SweepingViewConfig get() {
        return HANDLER.instance();
    }

    public static Screen createScreen(final Screen parent) {
        SweepingViewConfig config = get();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("sweepingview.config.title"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable("sweepingview.config.category.box"))
                                .group(
                                        OptionGroup.createBuilder()
                                                .name(
                                                        Component.translatable(
                                                                "sweepingview.config.group.stroke"))
                                                .option(
                                                        tickBox(
                                                                "boxStrokeEnabled",
                                                                DEFAULT_BOX_STROKE_ENABLED,
                                                                () -> config.boxStrokeEnabled,
                                                                value ->
                                                                        config.boxStrokeEnabled =
                                                                                value))
                                                .option(
                                                        color(
                                                                "boxStrokeColor",
                                                                DEFAULT_STROKE_COLOR,
                                                                () -> config.boxStrokeColor,
                                                                value ->
                                                                        config.boxStrokeColor =
                                                                                value))
                                                .build())
                                .group(
                                        OptionGroup.createBuilder()
                                                .name(
                                                        Component.translatable(
                                                                "sweepingview.config.group.fill"))
                                                .option(
                                                        tickBox(
                                                                "boxFillEnabled",
                                                                DEFAULT_BOX_FILL_ENABLED,
                                                                () -> config.boxFillEnabled,
                                                                value ->
                                                                        config.boxFillEnabled =
                                                                                value))
                                                .option(
                                                        color(
                                                                "boxFillColor",
                                                                DEFAULT_FILL_COLOR,
                                                                () -> config.boxFillColor,
                                                                value ->
                                                                        config.boxFillColor =
                                                                                value))
                                                .build())
                                .build())
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable("sweepingview.config.category.sphere"))
                                .option(
                                        tickBox(
                                                "sphereEnabled",
                                                DEFAULT_SPHERE_ENABLED,
                                                () -> config.sphereEnabled,
                                                value -> config.sphereEnabled = value))
                                .option(
                                        color(
                                                "sphereColor",
                                                DEFAULT_FILL_COLOR,
                                                () -> config.sphereColor,
                                                value -> config.sphereColor = value))
                                .build())
                .save(HANDLER::save)
                .build()
                .generateScreen(parent);
    }

    private static Option<Boolean> tickBox(
            final String key,
            final boolean defaultValue,
            final Supplier<Boolean> getter,
            final Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(Component.translatable("sweepingview.option." + key))
                .description(
                        OptionDescription.of(
                                Component.translatable("sweepingview.option." + key + ".desc")))
                .binding(defaultValue, getter, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<Color> color(
            final String key,
            final Color defaultValue,
            final Supplier<Color> getter,
            final Consumer<Color> setter) {
        return Option.<Color>createBuilder()
                .name(Component.translatable("sweepingview.option." + key))
                .description(
                        OptionDescription.of(
                                Component.translatable("sweepingview.option." + key + ".desc")))
                .binding(defaultValue, getter, setter)
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build();
    }
}
