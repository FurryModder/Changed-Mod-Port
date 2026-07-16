package net.changed.entity.beast;

import net.changed.entity.ComplexRenderer;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurMode;
import net.changed.entity.ai.DarkLatexFavor;
import net.changed.util.Color3;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForgeMod;

public class DarkLatexWolfPartial extends AbstractDarkLatexEntity implements ComplexRenderer {
    public DarkLatexWolfPartial(EntityType<? extends DarkLatexWolfPartial> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.025);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.975);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public boolean isMaskless() {
        return true;
    }

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getSkinTextureLocation() {
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getSkin().texture();
        return DefaultPlayerSkin.get(this.getUUID()).texture();
    }

    @OnlyIn(Dist.CLIENT)
    public String getModelName() {
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getSkin().model().id();
        return DefaultPlayerSkin.get(this.getUUID()).model().id();
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.DARK;
    }

    @Override
    public boolean canDoFavor(DarkLatexFavor favor) {
        return super.canDoFavor(favor) && favor != DarkLatexFavor.SUIT_OWNER;
    }
}
