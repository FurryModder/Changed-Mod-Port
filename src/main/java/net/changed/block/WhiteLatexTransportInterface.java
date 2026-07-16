package net.changed.block;

import net.changed.ability.AbstractAbility;
import net.changed.entity.*;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.latex.LatexSwimMover;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.*;
import net.changed.process.ProcessTransfur;
import net.changed.util.UniversalDist;
import net.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;

public interface WhiteLatexTransportInterface {
    static boolean isEntityInWhiteLatex(LivingEntity entity) {
        if (entity instanceof PlayerDataExtension ext)
            return ext.getPlayerMover() != null && ext.getPlayerMover().is(PlayerMover.LATEX_SWIM.get());
        return false;
    }

    default boolean allowTransport(BlockState blockState) {
        return true;
    }

    static void entityEnterLatex(LivingEntity entity, BlockPos pos) {
        if (entity.level().isClientSide)
            return;

        if (TransfurVariant.getEntityVariant(entity) != null && !(TransfurVariant.getEntityVariant(entity).getEntityType().is(ChangedTags.EntityTypes.WHITE_LATEX_SWIMMING)))
            return;

        if (isEntityInWhiteLatex(entity) || entity.isDeadOrDying())
            return;

        if (entity instanceof Player player && player.isSpectator())
            return;

        if (AbstractAbility.getAbilityInstanceSafe(entity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                .map(grabAbility -> !grabAbility.suited && grabAbility.grabbedEntity != null).orElse(false))
            return;

        ProcessTransfur.progressTransfur(entity, LatexAssimilationDecision.strong(LatexAssimilationDecision.Method.ABSORPTION,
                ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(),
                TransfurContext.hazard(TransfurCause.WHITE_LATEX), 8.0f));

        if (entity instanceof PlayerDataExtension ext && (!entity.level().isClientSide || UniversalDist.isLocalPlayer(entity)))
            ext.setPlayerMoverType(PlayerMover.LATEX_SWIM.get());
        else {
            entity.refreshDimensions();
            entity.setInvulnerable(true);

            entity.playSound(ChangedSounds.ENTITY_ENTER_LATEX.get(), 1.0f, 1.0f);
        }

        final Vec3 center = new Vec3(0.5D, 0.5D, 0.5D);
        Vec3 surface = LatexCoverState.getAt(entity.level(), pos).findClosestSurface(center, null);
        Vec3 delta = surface.subtract(center);
        final Direction closestDirection = center.equals(surface) ? null : Direction.getNearest(delta.x, delta.y, delta.z);
        final Vec3 surfaceNormal = closestDirection == null ? null : new Vec3(closestDirection.getNormal().getX(), closestDirection.getNormal().getY(), closestDirection.getNormal().getZ())
                .multiply(-1, -1, -1);
        surface = closestDirection == null ? surface : switch (closestDirection) {
            case NORTH, SOUTH, EAST, WEST -> surface.add(surfaceNormal.multiply(LatexSwimMover.SIZE_RADIUS, LatexSwimMover.SIZE_RADIUS, LatexSwimMover.SIZE_RADIUS));
            case UP -> surface.add(surfaceNormal.multiply(LatexSwimMover.SIZE_HEIGHT, LatexSwimMover.SIZE_HEIGHT, LatexSwimMover.SIZE_HEIGHT));
            default -> surface;
        };

        entity.teleportTo(pos.getX() + surface.x, pos.getY() + surface.y, pos.getZ() + surface.z);
    }

    static Optional<BlockPos> isBoundingBoxInWhiteLatex(LivingEntity entity) {
        AABB testHitbox = entity.getBoundingBox().inflate(-0.15);
        return BlockPos.betweenClosedStream(testHitbox).filter(blockPos -> {
            final BlockState blockState = entity.level().getBlockState(blockPos);
            if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface)
                return transportInterface.allowTransport(blockState);

            return false;
        }).findFirst();
    }

    @EventBusSubscriber
    class EventSubscriber {
        protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
            return LatexAssimilationDecision.fromBlockOrItem(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX), 4.8f);
        }

        @SubscribeEvent
        static void onPlayerTick(PlayerTickEvent.Post event) {
            var player = event.getEntity();

            if (isEntityInWhiteLatex(player))
                return;

            isBoundingBoxInWhiteLatex(player).ifPresent(latexPosition -> {
                ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                    if (variant.getLatexType() == ChangedLatexTypes.WHITE_LATEX.get())
                        entityEnterLatex(player, latexPosition);
                    else if (ChangedLatexTypes.WHITE_LATEX.get().isHostileTo(variant.getLatexType()))
                        player.hurt(ChangedDamageSources.WHITE_LATEX.source(player.level().registryAccess()), 2.0f);
                }, () -> {
                    ProcessTransfur.progressTransfur(player, LatexAssimilationDecision.fromBlockOrItem(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX), 4.8f, newEntity -> {
                        entityEnterLatex(newEntity.getEntity(), latexPosition);
                    }));
                });
            });
        }
    }
}
