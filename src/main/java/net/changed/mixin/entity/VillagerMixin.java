package net.changed.mixin.entity;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.process.ProcessTransfur;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {
    protected VillagerMixin(EntityType<? extends AbstractVillager> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @WrapMethod(method = "getPlayerReputation")
    protected int andApplyPenalty(Player player, Operation<Integer> original) {
        int total = original.call(player);
        if (player.isCreative())
            return total;

        int penalty = ProcessTransfur.getPlayerTransfurVariantSafe(player)
                .filter(variant -> {
                    if (variant.getParent().scares == null)
                        return false;
                    return ((TransfurVariantInstance)variant).getParent().scares.test(variant.getChangedEntity(), this);
                }).isPresent() ? -120 : 0;
        return total + penalty;
    }
}
