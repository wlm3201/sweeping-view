package com.sweepingview.client;

import net.minecraft.gizmos.Gizmo;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public record SphereGizmo(Vec3 center, float radius, int color) implements Gizmo {
    private static final int SEGMENTS = 48;

    private static final int PARALLEL_BANDS = 12;

    private static final float[][] CORNERS = corners();

    @Override
    public void emit(final GizmoPrimitives primitives, final float alphaMultiplier) {
        int tinted = ARGB.multiplyAlpha(this.color, alphaMultiplier);
        double x = this.center.x;
        double y = this.center.y;
        double z = this.center.z;
        float radius = this.radius;

        for (float[] patch : CORNERS) {
            Vec3 topA = corner(x, y, z, radius, patch, 0);
            Vec3 topB = corner(x, y, z, radius, patch, 3);
            Vec3 bottomB = corner(x, y, z, radius, patch, 6);
            Vec3 bottomA = corner(x, y, z, radius, patch, 9);

            primitives.addQuad(topA, topB, bottomB, bottomA, tinted);
            primitives.addQuad(bottomA, bottomB, topB, topA, tinted);
        }
    }

    private static Vec3 corner(
            final double x,
            final double y,
            final double z,
            final float radius,
            final float[] patch,
            final int offset) {
        return new Vec3(
                x + patch[offset] * radius,
                y + patch[offset + 1] * radius,
                z + patch[offset + 2] * radius);
    }

    private static float[][] corners() {
        float[][] table = new float[PARALLEL_BANDS * SEGMENTS][];

        for (int band = 0; band < PARALLEL_BANDS; band++) {
            double polarA = Math.PI * band / PARALLEL_BANDS;
            double polarB = Math.PI * (band + 1) / PARALLEL_BANDS;

            for (int segment = 0; segment < SEGMENTS; segment++) {
                double azimuthA = Math.TAU * segment / SEGMENTS;
                double azimuthB = Math.TAU * (segment + 1) / SEGMENTS;

                float[] patch = new float[12];
                put(patch, 0, polarA, azimuthA);
                put(patch, 3, polarA, azimuthB);
                put(patch, 6, polarB, azimuthB);
                put(patch, 9, polarB, azimuthA);
                table[band * SEGMENTS + segment] = patch;
            }
        }

        return table;
    }

    private static void put(
            final float[] into, final int offset, final double polar, final double azimuth) {
        double sin = Math.sin(polar);
        into[offset] = (float) (sin * Math.cos(azimuth));
        into[offset + 1] = (float) Math.cos(polar);
        into[offset + 2] = (float) (sin * Math.sin(azimuth));
    }
}
