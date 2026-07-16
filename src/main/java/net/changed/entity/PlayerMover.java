package net.changed.entity;

import net.changed.Changed;
import net.changed.block.DuctBlock;
import net.changed.entity.latex.LatexSwimMover;
import net.changed.init.ChangedRegistry;
import net.changed.util.InputWrapper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class PlayerMover<T extends PlayerMoverInstance<?>> {
    public abstract T createInstance();

    private static class DefaultMover extends PlayerMover<DefaultMover.DefaultInstance> {
        private static class DefaultInstance extends PlayerMoverInstance<DefaultMover> {
            public DefaultInstance(DefaultMover parent) {
                super(parent);
            }

            @Override
            public void aiStep(Player player, InputWrapper input, LogicalSide side) {

            }

            @Override
            public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {

            }

            @Override
            public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
                return true;
            }
        }

        @Override
        public DefaultInstance createInstance() {
            return new DefaultInstance(this);
        }
    }

    public static DeferredRegister<PlayerMover<?>> REGISTRY = ChangedRegistry.PLAYER_MOVER.createDeferred(Changed.MODID);

    public static DeferredHolder<PlayerMover<?>, DefaultMover> DEFAULT_MOVER = REGISTRY.register("default", DefaultMover::new);
    public static DeferredHolder<PlayerMover<?>, DuctBlock.DuctMover> DUCT_MOVER = REGISTRY.register("duct", DuctBlock.DuctMover::new);
    public static DeferredHolder<PlayerMover<?>, LatexSwimMover> LATEX_SWIM = REGISTRY.register("latex_swim", LatexSwimMover::new);
}
