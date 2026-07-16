package net.changed.init;

import net.changed.Changed;
import net.changed.fluid.*;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.fluids.FluidType;
import net.changed.compat.DistExecutor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChangedFluids {
    public static final DeferredRegister<FluidType> REGISTRY_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Changed.MODID);
    public static final DeferredRegister<Fluid> REGISTRY_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Changed.MODID);

    public static final Collection<Runnable> APPLY_RENDER_LAYERS = new ArrayList<>();

    public static final DeferredHolder<FluidType, FluidType> DARK_LATEX_FLUID = REGISTRY_TYPES.register("dark_latex", DarkLatexFluid::createFluidType);
    public static final DeferredHolder<FluidType, FluidType> WHITE_LATEX_FLUID = REGISTRY_TYPES.register("white_latex", WhiteLatexFluid::createFluidType);

    public static final DeferredHolder<FluidType, FluidType> WOLF_TRANSFUR_GAS = REGISTRY_TYPES.register("wolf_transfur_gas", WolfGas::createFluidType);
    public static final DeferredHolder<FluidType, FluidType> TIGER_TRANSFUR_GAS = REGISTRY_TYPES.register("tiger_transfur_gas", TigerGas::createFluidType);
    public static final DeferredHolder<FluidType, FluidType> SKUNK_TRANSFUR_GAS = REGISTRY_TYPES.register("skunk_transfur_gas", SkunkGas::createFluidType);

    public static final DeferredHolder<Fluid, AbstractLatexFluid> DARK_LATEX = REGISTRY_FLUIDS.register("dark_latex", DarkLatexFluid.Source::new);
    public static final DeferredHolder<Fluid, AbstractLatexFluid> DARK_LATEX_FLOWING = REGISTRY_FLUIDS.register("dark_latex_flowing", DarkLatexFluid.Flowing::new);
    public static final DeferredHolder<Fluid, AbstractLatexFluid> WHITE_LATEX = REGISTRY_FLUIDS.register("white_latex", WhiteLatexFluid.Source::new);
    public static final DeferredHolder<Fluid, AbstractLatexFluid> WHITE_LATEX_FLOWING = REGISTRY_FLUIDS.register("white_latex_flowing", WhiteLatexFluid.Flowing::new);

    public static final DeferredHolder<Fluid, TransfurGas> WOLF_GAS = register("wolf_gas", WolfGas.Source::new, ChangedFluids::translucentRenderer);
    public static final DeferredHolder<Fluid, TransfurGas> WOLF_GAS_FLOWING = register("wolf_gas_flowing", WolfGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static final DeferredHolder<Fluid, TransfurGas> TIGER_GAS = register("tiger_gas", TigerGas.Source::new, ChangedFluids::translucentRenderer);
    public static final DeferredHolder<Fluid, TransfurGas> TIGER_GAS_FLOWING = register("tiger_gas_flowing", TigerGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static final DeferredHolder<Fluid, TransfurGas> SKUNK_GAS = register("skunk_gas", SkunkGas.Source::new, ChangedFluids::translucentRenderer);
    public static final DeferredHolder<Fluid, TransfurGas> SKUNK_GAS_FLOWING = register("skunk_gas_flowing", SkunkGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static void cutoutRenderer(Fluid fluid) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(fluid, net.minecraft.client.renderer.RenderType.cutout()));
    }

    public static void translucentRenderer(Fluid fluid) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(fluid, net.minecraft.client.renderer.RenderType.translucent()));
    }

    public static <T extends Fluid, F extends T> DeferredHolder<Fluid, T> register(String name, Supplier<F> fluid) {
        return register(name, fluid, null);
    }

    public static <T extends Fluid, F extends T> DeferredHolder<Fluid, T> register(String name, Supplier<F> fluid, @Nullable Consumer<T> renderLayer) {
        var obj = REGISTRY_FLUIDS.<T>register(name, fluid);
        if (renderLayer != null)
            APPLY_RENDER_LAYERS.add(() -> renderLayer.accept(obj.get()));
        return obj;
    }
}
