package com.sweepingview.client;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Visualises the vanilla sweeping attack area for every player that is holding a sword and aiming
 * at an entity.
 *
 * <p>Vanilla ({@code Player#doSweepAttack}) gathers the victims with
 * {@code target.getBoundingBox().inflate(1.0, 0.25, 1.0)} and keeps the ones that are also within a
 * 3 block radius of the attacker ({@code this.distanceToSqr(nearby) < 9.0}). Since
 * {@code Entity#distanceToSqr(Entity)} compares {@code position()} of both entities, that second
 * constraint is a sphere of radius 3 centred on the attacker's <em>feet</em>, not a flat circle.
 *
 * <p>This renderer therefore draws both constraints: the inflated target box (wire frame +
 * translucent faces) and that reach sphere as a solid ball. Which of those is drawn, and in what
 * colour, comes from {@link SweepingViewConfig}.
 */
public final class SweepRangeRenderer {
	/** Horizontal / vertical inflation applied to the attacked entity's bounding box. */
	private static final double SWEEP_HORIZONTAL = 1.0;
	private static final double SWEEP_VERTICAL = 0.25;
	/** Radius (in blocks) of the reach sphere around the attacker. */
	private static final double SWEEP_RADIUS = 3.0;

	private static final float STROKE_WIDTH = 2.5F;

	/** Players further away than this (squared) from the camera are not considered. */
	private static final double MAX_RENDER_DISTANCE_SQ = 96.0 * 96.0;

	private SweepRangeRenderer() {
	}

	public static void extract(final LevelExtractionContext context) {
		ClientLevel level = context.level();

		if (level == null || context.camera().isPanoramicMode()) {
			return;
		}

		try {
			renderSweepRanges(level, context);
		} catch (IllegalStateException e) {
			// Vanilla only accepts gizmos while its per-frame collector is active. A few off-screen
			// render paths (such as the debug panorama screenshot) run extraction without one.
		}
	}

	private static void renderSweepRanges(final ClientLevel level, final LevelExtractionContext context) {
		float partialTicks = context.deltaTracker().getGameTimeDeltaPartialTick(false);
		Vec3 cameraPos = context.camera().position();
		SweepingViewConfig config = SweepingViewConfig.get();

		for (Player player : level.players()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}

			if (player.position().distanceToSqr(cameraPos) > MAX_RENDER_DISTANCE_SQ) {
				continue;
			}

			// A sweep attack can only happen while a sword is held in the main hand.
			if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.SWORDS)) {
				continue;
			}

			Entity target = SweepTargeting.findPointedEntity(player, partialTicks);

			if (target == null || target == player) {
				continue;
			}

			// Interpolate the target so the box follows it smoothly between ticks. Unlike vanilla's
			// cuboid gizmo this one keeps its faces when the camera moves inside the box, so the
			// sweep volume can be inspected by simply walking into it.
			Vec3 targetOffset = target.getPosition(partialTicks).subtract(target.position());
			AABB sweepBox = target.getBoundingBox().move(targetOffset).inflate(SWEEP_HORIZONTAL, SWEEP_VERTICAL, SWEEP_HORIZONTAL);

			// GizmoStyle reads a zero colour as "absent", so a half that is switched off just passes
			// 0 and the box ends up emitted with only the other half.
			if (config.boxStrokeEnabled || config.boxFillEnabled) {
				GizmoStyle style = GizmoStyle.strokeAndFill(
					config.boxStrokeEnabled ? config.boxStrokeColor.getRGB() : 0,
					STROKE_WIDTH,
					config.boxFillEnabled ? config.boxFillColor.getRGB() : 0
				);
				Gizmos.addGizmo(new CuboidGizmo(sweepBox, style));
			}

			// The 3 block reach limit is measured from the attacker's feet position, so the sphere is
			// half buried in the ground. Unlike vanilla's always-on-top gizmos it is left on the
			// regular depth test, so terrain occludes it the same way it occludes the sweep box.
			//
			// It is drawn from every viewpoint, being inside it included. The sphere patches are
			// double sided, so standing in your own sphere still shows the surface from within, and
			// that is what makes the reach boundary readable: where it cuts through blocks, and
			// whether a mob's hitbox actually sits inside it.
			if (config.sphereEnabled) {
				Gizmos.addGizmo(new SphereGizmo(player.getPosition(partialTicks), (float) SWEEP_RADIUS, config.sphereColor.getRGB()));
			}
		}
	}
}
