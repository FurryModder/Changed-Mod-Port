package net.changed.client;

import net.changed.ability.GrabEntityAbility;
import net.changed.block.WhiteLatexTransportInterface;
import net.changed.entity.ChangedEntity;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedAttributes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LocalTransfurVariantInstance<T extends ChangedEntity> extends ClientTransfurVariantInstance<T> {
    private final LocalPlayer host;

    public LocalTransfurVariantInstance(TransfurVariant<T> parent, LocalPlayer host) {
        super(parent, host);
        this.host = host;
    }

    @Override
    protected void tickTransfurProgress() {
        super.tickTransfurProgress();

        if (transfurProgression < 1f || this.ageAsVariant < 30 || !this.getItemUseMode().holdMainHand || GrabEntityAbility.getControllingEntity(this.host) != this.host) {
            ((LocalPlayerAccessor)host).setHandsBusy(true);
        } else if (host.getVehicle() == null && host.isHandsBusy()) {
            ((LocalPlayerAccessor)host).setHandsBusy(false);
        }
    }

    public static final ResourceLocation SPRINT_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath("changed", "sprinting_speed_boost");
    private static final ResourceLocation ENTITY_SPEED_MODIFIER_SPRINTING_ID = ResourceLocation.withDefaultNamespace("sprinting");

    public void handleSprintModifier(AttributeInstance movementSpeed) {
        if (movementSpeed.getModifier(ENTITY_SPEED_MODIFIER_SPRINTING_ID) != null) {
            // Vanilla sprint speed = MOVEMENT_SPEED + (0.3 * MOVEMENT_SPEED)
            var sprintMultiplier = host.getAttributeValue(ChangedAttributes.SPRINT_SPEED);
            var delta = (sprintMultiplier * 0.3) - 0.3;

            var sprintModifier = movementSpeed.getModifier(SPRINT_SPEED_MODIFIER);
            if (sprintModifier != null && sprintModifier.amount() == delta)
                return;
            if (sprintModifier == null && delta == 0.0)
                return;

            movementSpeed.removeModifier(SPRINT_SPEED_MODIFIER);
            if (delta != 0.0)
                movementSpeed.addTransientModifier(new AttributeModifier(SPRINT_SPEED_MODIFIER, delta, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (WhiteLatexTransportInterface.isEntityInWhiteLatex(host)) {
            ((LocalPlayerAccessor)host).setHandsBusy(true);
        }

        var movementSpeed = host.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null)
            return;

        this.handleSprintModifier(movementSpeed);
    }

    @Override
    public void unhookAll(Player player) {
        super.unhookAll(player);

        if (host.getVehicle() == null && host.isHandsBusy()) {
            ((LocalPlayerAccessor)host).setHandsBusy(false);
        }
    }
}
