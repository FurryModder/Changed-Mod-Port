package net.changed.entity.beast;

import com.mojang.authlib.GameProfile;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.entity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class LatexHuman extends ChangedEntity implements ComplexRenderer {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_PLAYER = SynchedEntityData.defineId(LatexHuman.class, EntityDataSerializers.OPTIONAL_UUID);

    public LatexHuman(EntityType<? extends LatexHuman> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PLAYER, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.entityData.get(DATA_PLAYER).ifPresent(uuid -> {
            tag.putUUID("RepresentPlayer", uuid);
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("RepresentPlayer")) {
            this.entityData.set(DATA_PLAYER, Optional.of(tag.getUUID("RepresentPlayer")));
        }
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    public UUID getRepresentUUID() {
        return this.entityData.get(DATA_PLAYER).orElseGet(() -> {
            if (this.getUnderlyingPlayer() != null) {
                return this.getUnderlyingPlayer().getUUID();
            } else {
                return this.getUUID();
            }
        });
    }

    public Optional<GameProfile> getGameProfile() {
        return this.entityData.get(DATA_PLAYER).map(uuid -> new GameProfile(uuid, null));
    }

    private boolean pendingTextures = false;
    @Nullable
    public UUID modelUUID = null;
    @Nullable
    public ResourceLocation skinTextureLocation = null;
    @Nullable
    public String modelName = null;

    public void setRepresentPlayer(@Nullable UUID uuid) {
        this.entityData.set(DATA_PLAYER, Optional.ofNullable(uuid));
    }

    @OnlyIn(Dist.CLIENT)
    protected void registerTextures() {
        var profile = getGameProfile();
        if (profile.isEmpty())
            return;
        if (profile.get().getId().equals(modelUUID)) {
            if (skinTextureLocation != null && modelName != null)
                return;
        }

        synchronized(this) {
            if (!this.pendingTextures) {
                this.pendingTextures = true;
                var skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile.get());
                this.modelUUID = profile.get().getId();
                this.skinTextureLocation = skin.texture();
                this.modelName = skin.model().id();
                this.pendingTextures = false;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getSkinTextureLocation() {
        this.registerTextures();

        if (skinTextureLocation != null)
            return skinTextureLocation;
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getSkin().texture();
        return DefaultPlayerSkin.get(this.getRepresentUUID()).texture();
    }

    @OnlyIn(Dist.CLIENT)
    public String getModelName() {
        this.registerTextures();

        if (modelName != null)
            return modelName;
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getSkin().model().id();
        return DefaultPlayerSkin.get(this.getUUID()).model().id();
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        switch (pose) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK:
                return 0.4F;
            case CROUCHING:
                return 1.27F;
            default:
                return 1.62F;
        }
    }

    @Override
    public void onReplicateOther(IAbstractChangedEntity other) {
        super.onReplicateOther(other);

        if (other.getChangedEntity() instanceof LatexHuman human) {
            human.setRepresentPlayer(this.getRepresentUUID());
        }
    }

    @Override
    public void onSuitOther(IAbstractChangedEntity other) {
        super.onSuitOther(other);

        if (other.getChangedEntity() instanceof LatexHuman human) {
            human.setRepresentPlayer(this.getRepresentUUID());
        }
    }

    @Override
    public void copyTraitsFrom(IAbstractChangedEntity entity) {
        super.copyTraitsFrom(entity);

        if (entity.getChangedEntity() instanceof LatexHuman human) {
            this.setRepresentPlayer(human.getRepresentUUID());
        }
    }

    @Override
    public CompoundTag savePlayerVariantData() {
        var tag = super.savePlayerVariantData();
        this.entityData.get(DATA_PLAYER).ifPresent(uuid -> {
            tag.putUUID("RepresentPlayer", uuid);
        });
        return tag;
    }

    @Override
    public void readPlayerVariantData(CompoundTag tag) {
        super.readPlayerVariantData(tag);
        if (tag.contains("RepresentPlayer")) {
            this.entityData.set(DATA_PLAYER, Optional.of(tag.getUUID("RepresentPlayer")));
        }
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.playerLike(attributes);
    }
}
