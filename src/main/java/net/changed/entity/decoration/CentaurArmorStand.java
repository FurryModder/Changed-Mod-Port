package net.changed.entity.decoration;

import net.changed.entity.variant.EntityShape;
import net.changed.init.ChangedEntities;
import net.changed.init.ChangedItems;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CentaurArmorStand extends AbstractArmorStand {
    private static final Rotations DEFAULT_BACK_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
    private static final Rotations DEFAULT_BACK_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);

    public static final EntityDataAccessor<Rotations> DATA_BACK_LEFT_LEG_POSE = SynchedEntityData.defineId(CentaurArmorStand.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_BACK_RIGHT_LEG_POSE = SynchedEntityData.defineId(CentaurArmorStand.class, EntityDataSerializers.ROTATIONS);

    private Rotations backLeftLegPose = DEFAULT_BACK_LEFT_LEG_POSE;
    private Rotations backRightLegPose = DEFAULT_BACK_RIGHT_LEG_POSE;

    public CentaurArmorStand(EntityType<? extends CentaurArmorStand> entityType, Level level) {
        super(entityType, level);
    }

    public CentaurArmorStand(Level level, double x, double y, double z) {
        this(ChangedEntities.CENTAUR_ARMOR_STAND.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ChangedItems.CENTAUR_ARMOR_STAND.get());
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.TAUR;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BACK_LEFT_LEG_POSE, DEFAULT_BACK_LEFT_LEG_POSE);
        builder.define(DATA_BACK_RIGHT_LEG_POSE, DEFAULT_BACK_RIGHT_LEG_POSE);
    }

    public void setBackLeftLegPose(Rotations rotations) {
        this.backLeftLegPose = rotations;
        this.entityData.set(DATA_BACK_LEFT_LEG_POSE, rotations);
    }

    public void setBackRightLegPose(Rotations rotations) {
        this.backRightLegPose = rotations;
        this.entityData.set(DATA_BACK_RIGHT_LEG_POSE, rotations);
    }

    public Rotations getBackLeftLegPose() {
        return this.backLeftLegPose;
    }

    public Rotations getBackRightLegPose() {
        return this.backRightLegPose;
    }

    protected void readPose(CompoundTag tag) {
        ListTag leftLeg = tag.getList("BackLeftLeg", 5);
        this.setBackLeftLegPose(leftLeg.isEmpty() ? DEFAULT_BACK_LEFT_LEG_POSE : new Rotations(leftLeg));
        ListTag listtag5 = tag.getList("BackRightLeg", 5);
        this.setBackRightLegPose(listtag5.isEmpty() ? DEFAULT_BACK_RIGHT_LEG_POSE : new Rotations(listtag5));
    }

    protected @NotNull CompoundTag writePose() {
        CompoundTag tag = new CompoundTag();

        if (!DEFAULT_BACK_LEFT_LEG_POSE.equals(this.backLeftLegPose)) {
            tag.put("BackLeftLeg", this.backLeftLegPose.save());
        }

        if (!DEFAULT_BACK_RIGHT_LEG_POSE.equals(this.backRightLegPose)) {
            tag.put("BackRightLeg", this.backRightLegPose.save());
        }

        return tag;
    }
}
