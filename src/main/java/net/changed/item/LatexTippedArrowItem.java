package net.changed.item;

import net.changed.Changed;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedRegistry;
import net.changed.init.ChangedTabs;
import net.changed.process.ProcessTransfur;
import net.changed.util.TagUtil;
import net.changed.util.UniversalDist;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber
public class LatexTippedArrowItem extends TippedArrowItem implements VariantHoldingBase {
    public static final String FORM_LOCATION = Changed.modResourceStr("form");

    public LatexTippedArrowItem() {
        super(new Properties());

        DispenserBlock.registerBehavior(this, new ProjectileDispenseBehavior(this));
    }

    protected static LatexAssimilationDecision<?> makeAssimilationDecision(Arrow arrow, LivingEntity target) {
        final var variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(TagUtil.getResourceLocation(arrow.getPersistentData(), FORM_LOCATION));
        return LatexAssimilationDecision.fromBlockOrItem(variant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE), 8.0f);
    }

    @SubscribeEvent
    public static void onLivingDamaged(LivingDamageEvent.Pre event) {
        if (event.getSource().getDirectEntity() instanceof Arrow arrow) {
            if (arrow.getPersistentData().contains(FORM_LOCATION)) {
                ProcessTransfur.progressTransfur(event.getEntity(), makeAssimilationDecision(arrow, event.getEntity()));
                arrow.remove(Entity.RemovalReason.DISCARDED);
            }
        }

    }

    public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, double p_36862_, double p_36863_, double p_36864_) {
        Arrow arrow = new Arrow(p_40513_, p_36862_, p_36863_, p_36864_, p_40514_.copy(), null);
        TagUtil.putResourceLocation(arrow.getPersistentData(), FORM_LOCATION, Syringe.getVariant(p_40514_).getFormId());
        return arrow;
    }

    public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
        Arrow arrow = new Arrow(p_40513_, p_40515_, p_40514_.copy(), null);
        TagUtil.putResourceLocation(arrow.getPersistentData(), FORM_LOCATION, Syringe.getVariant(p_40514_).getFormId());
        return arrow;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        AbstractArrow arrow = createArrow(level, stack, pos.x(), pos.y(), pos.z());
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }

    @Override
    public void appendHoverText(ItemStack p_43359_, TooltipContext context, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    public String getDescriptionId(ItemStack p_43364_) {
        return getOrCreateDescriptionId();
    }

    public @NotNull Rarity getRarity(ItemStack stack) {
        var tag = TagUtil.getCustomData(stack);
        if (tag == null)
            return Rarity.COMMON;
        return tag.contains("safe") ? (tag.getBoolean("safe") ? Rarity.RARE : Rarity.UNCOMMON) : Rarity.UNCOMMON;
    }

    @Override
    public Item getOriginalItem() {
        return Items.ARROW;
    }

    @Override
    public void fillItemList(Predicate<TransfurVariant<?>> predicate, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        TransfurVariant.getPublicTransfurVariants().filter(predicate).forEach(variant -> {
            output.accept(
                    Syringe.setOwner(
                            Syringe.setPureVariant(new ItemStack(this),
                                    variant.getFormId()),
                            UniversalDist.getLocalPlayer()));
        });
    }
}
