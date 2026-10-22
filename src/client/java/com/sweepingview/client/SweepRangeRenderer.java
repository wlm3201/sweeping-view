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

public final class SweepRangeRenderer {
    private static final double SWEEP_HORIZONTAL = 1.0;

    private static final double SWEEP_VERTICAL = 0.25;

    private static final double SWEEP_RADIUS = 3.0;

    private static final float STROKE_WIDTH = 2.5F;

    private static final double MAX_RENDER_DISTANCE_SQ = 96.0 * 96.0;

    private SweepRangeRenderer() {}

    public static void extract(final LevelExtractionContext context) {
        ClientLevel level = context.level();

        if (level == null || context.camera().isPanoramicMode()) {
            return;
        }

        try {
            renderSweepRanges(level, context);
        } catch (IllegalStateException e) {
        }
    }

    private static void renderSweepRanges(
            final ClientLevel level, final LevelExtractionContext context) {
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

            if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.SWORDS)) {
                continue;
            }

            Entity target = SweepTargeting.findPointedEntity(player, partialTicks);

            if (target == null || target == player) {
                continue;
            }

            Vec3 targetOffset = target.getPosition(partialTicks).subtract(target.position());
            AABB sweepBox =
                    target.getBoundingBox()
                            .move(targetOffset)
                            .inflate(SWEEP_HORIZONTAL, SWEEP_VERTICAL, SWEEP_HORIZONTAL);

            if (config.boxStrokeEnabled || config.boxFillEnabled) {
                GizmoStyle style =
                        GizmoStyle.strokeAndFill(
                                config.boxStrokeEnabled ? config.boxStrokeColor.getRGB() : 0,
                                STROKE_WIDTH,
                                config.boxFillEnabled ? config.boxFillColor.getRGB() : 0);
                Gizmos.addGizmo(new CuboidGizmo(sweepBox, style));
            }

            if (config.sphereEnabled) {
                Gizmos.addGizmo(
                        new SphereGizmo(
                                player.getPosition(partialTicks),
                                (float) SWEEP_RADIUS,
                                config.sphereColor.getRGB()));
            }
        }
    }
}
