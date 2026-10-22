package com.sweepingview.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class SweepTargeting {
    private SweepTargeting() {}

    public static @Nullable Entity findPointedEntity(
            final Player player, final float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();

        if (player == minecraft.player) {
            return minecraft.crosshairPickEntity;
        }

        return raycastEntity(player, partialTicks);
    }

    private static @Nullable Entity raycastEntity(final Player player, final float partialTicks) {
        double blockRange = player.blockInteractionRange();
        double entityRange = player.entityInteractionRange();
        double maxRange = Math.max(blockRange, entityRange);

        Vec3 from = player.getEyePosition(partialTicks);
        Vec3 view = player.getViewVector(partialTicks);
        Vec3 to = from.add(view.scale(maxRange));

        HitResult blockHit = player.pick(maxRange, partialTicks, false);
        double blockDistSq =
                blockHit.getType() == HitResult.Type.MISS
                        ? Double.MAX_VALUE
                        : blockHit.getLocation().distanceToSqr(from);

        AABB searchBox = player.getBoundingBox().expandTowards(view.scale(maxRange)).inflate(1.0);
        EntityHitResult entityHit =
                ProjectileUtil.getEntityHitResult(
                        player,
                        from,
                        to,
                        searchBox,
                        EntitySelector.CAN_BE_PICKED,
                        maxRange * maxRange);

        if (entityHit != null && entityHit.getLocation().distanceToSqr(from) < blockDistSq) {
            return entityHit.getEntity();
        }

        return null;
    }
}
