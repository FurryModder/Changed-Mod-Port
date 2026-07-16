package net.changed.entity.beast;

import net.changed.ability.IAbstractChangedEntity;
import net.changed.entity.*;
import net.changed.entity.robot.Exoskeleton;
import net.changed.init.ChangedAttributes;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class LatexBenignOrca extends AbstractAquaticEntity {
    private boolean hasExoLast = false;

    public LatexBenignOrca(EntityType<? extends LatexBenignOrca> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(4.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.12);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.5);
        attributes.getInstance(ChangedAttributes.JUMP_STRENGTH).setBaseValue(0.5);
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);

        boolean hasExo = Exoskeleton.getEntityExoskeleton(this.maybeGetUnderlying()).isPresent();
        if (hasExoLast != hasExo) {
            var attributes = this.getAttributes();

            if (hasExo) {
                attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.075);
                attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.95);
                attributes.getInstance(ChangedAttributes.JUMP_STRENGTH).setBaseValue(1.0);
            }

            else {
                attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.15);
                attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.2);
                attributes.getInstance(ChangedAttributes.JUMP_STRENGTH).setBaseValue(0.5);
            }

            hasExoLast = hasExo;

            var instance = IAbstractChangedEntity.forEitherSafe(this.maybeGetUnderlying()).map(IAbstractChangedEntity::getTransfurVariantInstance).orElse(null);
            if (instance != null) {
                instance.visionType = hasExo ? VisionType.NORMAL : VisionType.BLIND;
                instance.itemUseMode = hasExo ? UseItemMode.NORMAL : UseItemMode.NONE;
                instance.miningStrength = hasExo ? MiningStrength.NORMAL : MiningStrength.WEAK;

                instance.refreshAttributes();
            }
        }
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#282828");
    }
}