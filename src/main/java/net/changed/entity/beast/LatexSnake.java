package net.changed.entity.beast;

import net.changed.entity.ChangedEntity;
import net.changed.entity.LatexTypeOld;
import net.changed.entity.TransfurMode;
import net.changed.entity.latex.LatexType;
import net.changed.entity.variant.EntityShape;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

public class LatexSnake extends ChangedEntity {
    public LatexSnake(EntityType<? extends LatexSnake> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(1.5);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(26);
        attributes.getInstance(net.minecraft.world.entity.ai.attributes.Attributes.STEP_HEIGHT).setBaseValue(computeStepHeightOffset(1.1));
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public boolean isMovingSlowly() {
        return this.isCrouching();
    }

    @Override
    public double getMyRidingOffset() {
        return -0.1175;
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.NAGA;
    }
}