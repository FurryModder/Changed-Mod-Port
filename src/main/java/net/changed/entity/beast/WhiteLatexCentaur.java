package net.changed.entity.beast;

import net.changed.entity.LatexTypeOld;
import net.changed.entity.TransfurMode;
import net.changed.entity.latex.LatexType;
import net.changed.entity.variant.EntityShape;
import net.changed.init.ChangedAttributes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WhiteLatexCentaur extends WhiteLatexKnight implements LatexTaur<WhiteLatexCentaur> {
    public WhiteLatexCentaur(EntityType<? extends WhiteLatexCentaur> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.2);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.9);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(30);
        attributes.getInstance(net.minecraft.world.entity.ai.attributes.Attributes.STEP_HEIGHT).setBaseValue(computeStepHeightOffset(1.1));
        attributes.getInstance(ChangedAttributes.JUMP_STRENGTH).setBaseValue(1.25);
        attributes.getInstance(ChangedAttributes.FALL_RESISTANCE).setBaseValue(2.5);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public void equipSaddle(ItemStack stack, @Nullable SoundSource p_21748_) {
        this.equipSaddle(this, stack, p_21748_);
    }

    @Override
    public boolean isSaddled() {
        return this.isSaddled(this);
    }

    protected void doPlayerRide(Player player) {
        this.doPlayerRide(this, player);
    }

    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + getTorsoYOffset(this) - (2.0 / 16.0);
    }

    public InteractionResult mobInteract(Player p_30713_, InteractionHand p_30714_) {
        if (isSaddled()) {
            this.doPlayerRide(p_30713_);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.TAUR;
    }
}
