package com.sweepingview.client;

import com.sweepingview.SweepingView;

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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Persistent settings for the sweep range visualiser, plus the YACL screen that edits them.
 *
 * <p>Every option maps one to one onto a field, and each field is bound straight to that field
 * rather than to a copy. That is what makes YACL's reset button and its "cancel discards edits"
 * behaviour work: the bindings are what the screen writes through, and {@link #HANDLER} is what
 * {@code Done} saves.
 *
 * <p>Colours are {@link Color} because that is what YACL's {@code ColorController} is typed on. Its
 * {@code getRGB()} happens to already be the packed ARGB layout the gizmo API expects, so no
 * conversion is needed at render time.
 */
public class SweepingViewConfig {
	/**
	 * Factory defaults, held apart from the fields themselves. A binding needs the default as a
	 * separate argument, and reading it back off the field would hand the reset button whatever the
	 * player last saved instead.
	 */
	private static final boolean DEFAULT_BOX_STROKE_ENABLED = true;
	private static final boolean DEFAULT_BOX_FILL_ENABLED = true;
	private static final boolean DEFAULT_SPHERE_ENABLED = true;

	/**
	 * Factory default for the wire frame: opaque {@code #FF5050}, so the box edges stay crisp
	 * against whatever is behind them.
	 */
	private static final Color DEFAULT_STROKE_COLOR = new Color(255, 80, 80, 0xFF);

	/**
	 * Shared factory default for the faces and the reach sphere, {@code FF505040} written RGBA:
	 * {@code #FF5050} at alpha {@code 0x40}, roughly a quarter opaque. Dense enough to read the
	 * surface, thin enough to still make out the blocks and hitboxes behind it. {@link Color} is
	 * immutable, so one instance can back both fields.
	 */
	private static final Color DEFAULT_FILL_COLOR = new Color(255, 80, 80, 0x40);

	/**
	 * Reads and writes {@code config/sweeping-view.json5}. JSON5 rather than plain JSON so the file
	 * can keep the comments YACL writes next to each entry.
	 */
	public static final ConfigClassHandler<SweepingViewConfig> HANDLER = ConfigClassHandler
		.createBuilder(SweepingViewConfig.class)
		.id(Identifier.fromNamespaceAndPath(SweepingView.MOD_ID, "config"))
		.serializer(handler -> GsonConfigSerializerBuilder.create(handler)
			.setPath(FabricLoader.getInstance().getConfigDir().resolve(SweepingView.MOD_ID + ".json5"))
			.setJson5(true)
			.build())
		.build();

	/** Outline of the box the attack sweeps. */
	@SerialEntry(comment = "Draw the outline of the sweep box.")
	public boolean boxStrokeEnabled = DEFAULT_BOX_STROKE_ENABLED;

	@SerialEntry(comment = "ARGB colour of the sweep box outline.")
	public Color boxStrokeColor = DEFAULT_STROKE_COLOR;

	/** Translucent faces of the sweep box. */
	@SerialEntry(comment = "Fill the sweep box with a translucent colour.")
	public boolean boxFillEnabled = DEFAULT_BOX_FILL_ENABLED;

	@SerialEntry(comment = "ARGB colour of the sweep box faces. The alpha byte sets their density.")
	public Color boxFillColor = DEFAULT_FILL_COLOR;

	/** The 3 block reach limit around the attacker. */
	@SerialEntry(comment = "Draw the reach sphere around the attacker.")
	public boolean sphereEnabled = DEFAULT_SPHERE_ENABLED;

	@SerialEntry(comment = "ARGB colour of the reach sphere. The alpha byte sets its density.")
	public Color sphereColor = DEFAULT_FILL_COLOR;

	public static SweepingViewConfig get() {
		return HANDLER.instance();
	}

	/** Builds a fresh settings screen. YACL screens are single use, so this is called per open. */
	public static Screen createScreen(final Screen parent) {
		SweepingViewConfig config = get();

		return YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("sweeping-view.config.title"))
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("sweeping-view.config.category.box"))
				.group(OptionGroup.createBuilder()
					.name(Component.translatable("sweeping-view.config.group.stroke"))
					.option(tickBox("boxStrokeEnabled", DEFAULT_BOX_STROKE_ENABLED, () -> config.boxStrokeEnabled, value -> config.boxStrokeEnabled = value))
					.option(color("boxStrokeColor", DEFAULT_STROKE_COLOR, () -> config.boxStrokeColor, value -> config.boxStrokeColor = value))
					.build())
				.group(OptionGroup.createBuilder()
					.name(Component.translatable("sweeping-view.config.group.fill"))
					.option(tickBox("boxFillEnabled", DEFAULT_BOX_FILL_ENABLED, () -> config.boxFillEnabled, value -> config.boxFillEnabled = value))
					.option(color("boxFillColor", DEFAULT_FILL_COLOR, () -> config.boxFillColor, value -> config.boxFillColor = value))
					.build())
				.build())
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("sweeping-view.config.category.sphere"))
				.option(tickBox("sphereEnabled", DEFAULT_SPHERE_ENABLED, () -> config.sphereEnabled, value -> config.sphereEnabled = value))
				.option(color("sphereColor", DEFAULT_FILL_COLOR, () -> config.sphereColor, value -> config.sphereColor = value))
				.build())
			.save(HANDLER::save)
			.build()
			.generateScreen(parent);
	}

	private static Option<Boolean> tickBox(final String key, final boolean defaultValue, final Supplier<Boolean> getter, final Consumer<Boolean> setter) {
		return Option.<Boolean>createBuilder()
			.name(Component.translatable("sweeping-view.option." + key))
			.description(OptionDescription.of(Component.translatable("sweeping-view.option." + key + ".desc")))
			.binding(defaultValue, getter, setter)
			.controller(TickBoxControllerBuilder::create)
			.build();
	}

	private static Option<Color> color(final String key, final Color defaultValue, final Supplier<Color> getter, final Consumer<Color> setter) {
		return Option.<Color>createBuilder()
			.name(Component.translatable("sweeping-view.option." + key))
			.description(OptionDescription.of(Component.translatable("sweeping-view.option." + key + ".desc")))
			.binding(defaultValue, getter, setter)
			.controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
			.build();
	}
}
