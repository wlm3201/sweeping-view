package com.sweepingview.client;

import net.minecraft.gizmos.Gizmo;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record CuboidGizmo(AABB aabb, GizmoStyle style) implements Gizmo {
    @Override
    public void emit(final GizmoPrimitives primitives, final float alphaMultiplier) {
        double x0 = this.aabb.minX;
        double y0 = this.aabb.minY;
        double z0 = this.aabb.minZ;
        double x1 = this.aabb.maxX;
        double y1 = this.aabb.maxY;
        double z1 = this.aabb.maxZ;

        if (this.style.hasFill()) {
            int color = this.style.multipliedFill(alphaMultiplier);
            doubleSidedQuad(
                    primitives,
                    new Vec3(x1, y0, z0),
                    new Vec3(x1, y1, z0),
                    new Vec3(x1, y1, z1),
                    new Vec3(x1, y0, z1),
                    color);
            doubleSidedQuad(
                    primitives,
                    new Vec3(x0, y0, z0),
                    new Vec3(x0, y0, z1),
                    new Vec3(x0, y1, z1),
                    new Vec3(x0, y1, z0),
                    color);
            doubleSidedQuad(
                    primitives,
                    new Vec3(x0, y0, z0),
                    new Vec3(x0, y1, z0),
                    new Vec3(x1, y1, z0),
                    new Vec3(x1, y0, z0),
                    color);
            doubleSidedQuad(
                    primitives,
                    new Vec3(x0, y0, z1),
                    new Vec3(x1, y0, z1),
                    new Vec3(x1, y1, z1),
                    new Vec3(x0, y1, z1),
                    color);
            doubleSidedQuad(
                    primitives,
                    new Vec3(x0, y1, z0),
                    new Vec3(x0, y1, z1),
                    new Vec3(x1, y1, z1),
                    new Vec3(x1, y1, z0),
                    color);
            doubleSidedQuad(
                    primitives,
                    new Vec3(x0, y0, z0),
                    new Vec3(x1, y0, z0),
                    new Vec3(x1, y0, z1),
                    new Vec3(x0, y0, z1),
                    color);
        }

        if (this.style.hasStroke()) {
            int color = this.style.multipliedStroke(alphaMultiplier);
            float width = this.style.strokeWidth();
            primitives.addLine(new Vec3(x0, y0, z0), new Vec3(x1, y0, z0), color, width);
            primitives.addLine(new Vec3(x0, y0, z0), new Vec3(x0, y1, z0), color, width);
            primitives.addLine(new Vec3(x0, y0, z0), new Vec3(x0, y0, z1), color, width);
            primitives.addLine(new Vec3(x1, y0, z0), new Vec3(x1, y1, z0), color, width);
            primitives.addLine(new Vec3(x1, y1, z0), new Vec3(x0, y1, z0), color, width);
            primitives.addLine(new Vec3(x0, y1, z0), new Vec3(x0, y1, z1), color, width);
            primitives.addLine(new Vec3(x0, y1, z1), new Vec3(x0, y0, z1), color, width);
            primitives.addLine(new Vec3(x0, y0, z1), new Vec3(x1, y0, z1), color, width);
            primitives.addLine(new Vec3(x1, y0, z1), new Vec3(x1, y0, z0), color, width);
            primitives.addLine(new Vec3(x0, y1, z1), new Vec3(x1, y1, z1), color, width);
            primitives.addLine(new Vec3(x1, y0, z1), new Vec3(x1, y1, z1), color, width);
            primitives.addLine(new Vec3(x1, y1, z0), new Vec3(x1, y1, z1), color, width);
        }
    }

    private static void doubleSidedQuad(
            final GizmoPrimitives primitives,
            final Vec3 a,
            final Vec3 b,
            final Vec3 c,
            final Vec3 d,
            final int color) {
        primitives.addQuad(a, b, c, d, color);
        primitives.addQuad(d, c, b, a, color);
    }
}
