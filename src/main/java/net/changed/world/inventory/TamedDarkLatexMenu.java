package net.changed.world.inventory;

import net.changed.entity.ai.DarkLatexFavor;
import net.changed.entity.beast.AbstractDarkLatexEntity;
import net.changed.init.ChangedMenus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class TamedDarkLatexMenu extends AbstractContainerMenu implements UpdateableMenu {
    public AbstractDarkLatexEntity tamedDarkLatex;
    public final Player player;

    public TamedDarkLatexMenu(int id, Inventory inventory, AbstractDarkLatexEntity tamedDarkLatex) {
        super(ChangedMenus.TAMED_DARK_LATEX.get(), id);
        this.tamedDarkLatex = tamedDarkLatex;
        this.player = inventory.player;
    }

    public TamedDarkLatexMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ChangedMenus.TAMED_DARK_LATEX.get(), id);
        this.player = inv.player;

        if (extraData == null)
            return;

        this.tamedDarkLatex = (AbstractDarkLatexEntity) inv.player.level().getEntity(extraData.readInt());
    }

    @Override
    public ItemStack quickMoveStack(Player viewer, int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player viewer) {
        if (this.tamedDarkLatex.isRemoved()) {
            return false;
        } else if (this.tamedDarkLatex.getOwner() != viewer) {
            return false;
        } else {
            return !(viewer.distanceToSqr(this.tamedDarkLatex) > 64.0D);
        }
    }

    @Override
    public int getId() {
        return containerId;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver == LogicalSide.SERVER && origin == this.tamedDarkLatex.getOwner()) {
            switch (payload.getString("command")) {
                case "view_inventory" -> {
                    NetworkHooks.openScreen((ServerPlayer) this.player, new SimpleMenuProvider(
                            (id, inv, viewer) -> new TamedDarkLatexInventoryMenu(id, this.player, this.tamedDarkLatex),
                            this.tamedDarkLatex.getDisplayName()
                    ), extraData -> {
                        extraData.writeInt(this.tamedDarkLatex.getId());
                    });
                }
                case "cycle_follow" -> {
                    this.tamedDarkLatex.setFollowOwner(!this.tamedDarkLatex.isFollowingOwner());
                    this.tamedDarkLatex.setJumping(false);
                    this.tamedDarkLatex.getNavigation().stop();
                }
                case "cycle_target_type" -> {
                    this.tamedDarkLatex.setTargetType(this.tamedDarkLatex.getTargetType().cycle());
                    this.tamedDarkLatex.setTarget(null);
                }
                case "cycle_attack_type" -> {
                    this.tamedDarkLatex.setAttackType(this.tamedDarkLatex.getAttackType().cycle());
                    this.tamedDarkLatex.updateHeldItemChoice();
                }
                case "cycle_attack_condition" -> {
                    this.tamedDarkLatex.setAttackCondition(this.tamedDarkLatex.getAttackCondition().cycle());
                    this.tamedDarkLatex.setTarget(null);
                }
                case "favor_fishing" -> {
                    this.tamedDarkLatex.setFavor(this.tamedDarkLatex.getCurrentFavor() != DarkLatexFavor.FISHING ?
                            DarkLatexFavor.FISHING : DarkLatexFavor.NONE);
                }
                case "favor_caving" -> {
                    this.tamedDarkLatex.setFavor(this.tamedDarkLatex.getCurrentFavor() != DarkLatexFavor.CAVING ?
                            DarkLatexFavor.CAVING : DarkLatexFavor.NONE);
                }
                case "favor_suit_owner" -> {
                    this.tamedDarkLatex.setFavor(this.tamedDarkLatex.getCurrentFavor() != DarkLatexFavor.SUIT_OWNER ?
                            DarkLatexFavor.SUIT_OWNER : DarkLatexFavor.NONE);
                }
            }
        }
    }
}
