package net.changed.entity.projectile;

import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.ai.NonLatexAssimilationDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedRegistry;
import net.changed.item.GasMaskItem;
import net.changed.process.ProcessTransfur;
import net.changed.util.Color3;
import net.changed.util.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class GasParticle extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(GasParticle.class, EntityDataSerializers.INT);
    public static final int DISSIPATE_TIME = 15;
    public TransfurVariant<?> variant = null;

    public GasParticle(EntityType<? extends GasParticle> type, Level level) {
        super(type, level);
    }

    public GasParticle setVariant(TransfurVariant<?> variant) {
        this.variant = variant; return this;
    }

    public GasParticle setColor(Color3 color) {
        this.entityData.set(COLOR, color.toInt()); return this;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        TagUtil.putResourceLocation(tag, "LatexVariant", variant.getFormId());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("LatexVariant"))
            this.variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(TagUtil.getResourceLocation(tag, "LatexVariant"));
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (entity instanceof LivingEntity livingEntity)
            return TransfurVariant.getEntityVariant(livingEntity) == null;
        else
            return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > DISSIPATE_TIME)
            this.discard();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.015;
    }

    protected NonLatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return NonLatexAssimilationDecision.fromBlockOrItem(variant, TransfurCause.FACE_HAZARD, (int)Mth.lerp((float)this.tickCount / DISSIPATE_TIME, 3.5f, 0.5f), 1.0f);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        if (variant != null && result.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof GasMaskItem)
                return;

            ProcessTransfur.progressTransfur(livingEntity, this.makeAssimilationDecision(livingEntity));
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOR, Color3.WHITE.toInt());
    }

    public Color3 getColor() {
        return Color3.fromInt(this.getEntityData().get(COLOR));
    }
}
