package net.changed.server;

import net.changed.Changed;
import net.changed.ability.GrabEntityAbility;
import net.changed.ability.tree.AbilityCounter;
import net.changed.entity.ChangedEntity;
import net.changed.entity.PlayerDataExtension;
import net.changed.entity.variant.TransfurVariant;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.init.ChangedAbilities;
import net.changed.init.ChangedCriteriaTriggers;
import net.changed.init.ChangedEffects;
import net.changed.init.ChangedTags;
import net.changed.process.ProcessTransfur;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.changed.compat.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerTransfurVariantInstance<T extends ChangedEntity> extends TransfurVariantInstance<T> {
    private final ServerPlayer host;

    public ServerTransfurVariantInstance(TransfurVariant<T> parent, ServerPlayer host) {
        super(parent, host);
        this.host = host;
    }

    @Override
    public boolean checkForTemporary() {
        final var grabber = GrabEntityAbility.getGrabber(this.host);

        if (super.checkForTemporary())
            return true;
        else if (isTemporaryFromSuit) {
            if (grabber == null || grabber.getEntity().isDeadOrDying() || grabber.getEntity().isRemoved()) { // Remove variant if grabber doesn't exist
                ProcessTransfur.removePlayerTransfurVariant(this.host);
                return true;
            }

            var ability = grabber.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability == null || ability.grabbedEntity != this.host) {
                ProcessTransfur.removePlayerTransfurVariant(this.host);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void tickTransfurProgress() {
        super.tickTransfurProgress();

        if (transfurProgressionO < 1f && transfurProgression >= 1f) {
            if (!willSurviveTransfur)
                this.getParent().replaceEntity(host, transfurContext.source());
        }

        if (transfurProgression >= 1f && !isTemporaryFromSuit()) {
            transfurContext = transfurContext.withoutSource();
            if (willSurviveTransfur)
                ChangedCriteriaTriggers.TRANSFUR.trigger(host, this);
        }
    }

    @Override
    protected void tickFlying() {
        super.tickFlying();

        if (this.canCreativeFly() && shouldApplyAbilities()) {
            if (!host.isSpectator() && host.getAbilities().flying)
                ChangedCriteriaTriggers.FLYING.trigger(host, ticksFlying);
        }

        this.entity.setChangedEntityFlag(ChangedEntity.FLAG_IS_FLYING, host.getAbilities().flying &&
                host.getVehicle() == null);
    }

    @Override
    protected void tickBreathing(LivingBreatheEvent event) {
        super.tickBreathing(event);

        if (host.isAlive() && breatheMode.canBreatheWater() && shouldApplyAbilities() && host.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
            ChangedCriteriaTriggers.AQUATIC_BREATHE.trigger(host, this.ticksBreathingUnderwater);
        }
    }

    public final Map<Attribute, ResourceLocation> attributesById = new HashMap<>();

    private static ResourceLocation modifierIdFor(Attribute attribute) {
        var key = ForgeRegistries.ATTRIBUTES.getKey(attribute);
        if (key == null)
            return Changed.modResource("ability_tree/unknown");
        return Changed.modResource("ability_tree/" + key.getNamespace() + "/" + key.getPath());
    }

    @Override
    public void tick() {
        AbilityCounter counter = new AbilityCounter(this);
        var abilityTree = ((PlayerDataExtension)host).getAbilityTree();
        abilityTree.updateTrees();
        abilityTree.applyEffects(counter);

        var attributes = host.getAttributes();
        counter.getAttributeAdders().forEach((attribute, value) -> {
            var holder = ForgeRegistries.ATTRIBUTES.getHolder(attribute).orElse(null);
            if (holder == null)
                return;
            var modifierId = attributesById.computeIfAbsent(attribute, ServerTransfurVariantInstance::modifierIdFor);
            var instance = attributes.getInstance(holder);
            if (instance == null)
                return;
            var existing = instance.getModifier(modifierId);
            if (existing != null && existing.amount() == value)
                return;

            if (existing == null && value == 0.0)
                return;

            instance.removeModifier(modifierId);
            if (value != 0.0)
                instance.addTransientModifier(new AttributeModifier(modifierId, value, AttributeModifier.Operation.ADD_VALUE));
        });

        super.tick();

        if (parent.getEntityType().is(ChangedTags.EntityTypes.LATEX))
            host.removeEffect(ChangedEffects.HYPERCOAGULATION);

        this.tickScare();
    }

    public void tickScare() {
        if (this.parent.scares == null)
            return;

        final double distance = 8D;
        final double farRunSpeed = 1.0D;
        final double nearRunSpeed = 1.2D;

        if (host.isCreative() || host.isSpectator())
            return;

        List<PathfinderMob> entitiesScared = host.level().getEntitiesOfClass(
                PathfinderMob.class,
                host.getBoundingBox().inflate(distance, 6D, distance),
                target -> {
                    return this.parent.scares.test(this.entity, target) && target.hasLineOfSight(host);
                });

        for (var v : entitiesScared) {
            final double speedScale = (v instanceof AbstractVillager) ? 0.5D : 1.0D;

            //if the creature has no path, or the target path is < distance, make the creature run.
            if (v.getNavigation().getPath() == null || host.distanceToSqr(v.getNavigation().getTargetPos().getX(), v.getNavigation().getTargetPos().getY(), v.getNavigation().getTargetPos().getZ()) < distance * distance) {
                Vec3 vector3d = DefaultRandomPos.getPosAway(v, 16, 7, new Vec3(host.getX(), host.getY(), host.getZ()));

                if (vector3d != null && host.distanceToSqr(vector3d) > host.distanceToSqr(v)) {
                    Path path = v.getNavigation().createPath(vector3d.x, vector3d.y, vector3d.z, 0);

                    if (path != null) {
                        double speed = v.distanceToSqr(host) < 49D ? nearRunSpeed : farRunSpeed;
                        v.getNavigation().moveTo(path, speed * speedScale);
                    }
                }
            }
            else {
                double speed = v.distanceToSqr(host) < 49D ? nearRunSpeed : farRunSpeed;
                v.getNavigation().setSpeedModifier(speed * speedScale);
            }

            if (v.getTarget() == host)
                v.setTarget(null);
        }
    }

    @Override
    public void unhookAll(Player player) {
        super.unhookAll(player);
        attributesById.forEach((attribute, modifierId) -> {
            var holder = ForgeRegistries.ATTRIBUTES.getHolder(attribute).orElse(null);
            if (holder == null)
                return;
            var instance = player.getAttributes().getInstance(holder);
            if (instance == null)
                return;

            instance.removeModifier(modifierId);
        });
    }
}
