package net.changed.entity.beast;

import net.changed.ability.IAbstractChangedEntity;
import net.changed.ability.SirenSingAbilityInstance;
import net.changed.entity.Gender;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurMode;
import net.changed.entity.variant.EntityShape;
import net.changed.init.ChangedAbilities;
import net.changed.util.Color3;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

public class LatexSiren extends AbstractAquaticGenderedEntity {
    protected final SirenSingAbilityInstance sing;

    public LatexSiren(EntityType<? extends LatexSiren> type, Level level) {
        super(type, level);
        sing = registerAbility(ability -> this.wantsToSing(), new SirenSingAbilityInstance(ChangedAbilities.SIREN_SING.get(), IAbstractChangedEntity.forEntity(this)));
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.34);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(5.58);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(28);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    public boolean wantsToSing() {
        if (getTarget() == null)
            return false;
        var target = getTarget();
        return !target.isEyeInFluidType(Fluids.WATER.getFluidType());
    }

    @Override
    public boolean wantsToSurface() {
        return this.wantsToSing();
    }

    @Override
    public boolean isVisuallySwimming() {
        if (this.getUnderlyingPlayer() != null && this.getUnderlyingPlayer().isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()))
            return true;
        return super.isVisuallySwimming();
    }

    @Override
    public double getMyRidingOffset() {
        return 0.02;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#969696");
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.MER;
    }
}
