package net.changed.init;

import net.changed.Changed;
import net.changed.ability.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedAbilities {
    public static final DeferredRegister<AbstractAbility<?>> REGISTRY = ChangedRegistry.ABILITY.createDeferred(Changed.MODID);

    public static DeferredHolder<AbstractAbility<?>, SwitchTransfurModeAbility> SWITCH_TRANSFUR_MODE = REGISTRY.register("switch_transfur_mode", SwitchTransfurModeAbility::new);
    public static DeferredHolder<AbstractAbility<?>, SimpleCreateItemAbility> CREATE_COBWEB = REGISTRY.register("create_cobweb",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.COBWEB), 5.0f, 6.0f));
    public static DeferredHolder<AbstractAbility<?>, SimpleCreateItemAbility> CREATE_INKBALL = REGISTRY.register("create_inkball",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(ChangedItems.LATEX_INKBALL.get()), 5.0f, 6.0f));
    public static DeferredHolder<AbstractAbility<?>, SimpleCreateItemAbility> CREATE_HONEYCOMB = REGISTRY.register("create_honeycomb",
            () -> new SimpleCreateItemAbility(() -> new ItemStack(Items.HONEYCOMB), 5.0f, 6.0f));
    public static DeferredHolder<AbstractAbility<?>, SwitchHandsAbility> SWITCH_HANDS = REGISTRY.register("switch_hands", SwitchHandsAbility::new);
    public static DeferredHolder<AbstractAbility<?>, AccessChestAbility> ACCESS_CHEST = REGISTRY.register("access_chest", AccessChestAbility::new);
    public static DeferredHolder<AbstractAbility<?>, SwitchGenderAbility> SWITCH_GENDER = REGISTRY.register("switch_gender", SwitchGenderAbility::new);
    public static DeferredHolder<AbstractAbility<?>, SlitherAbility> SLITHER = REGISTRY.register("slither", SlitherAbility::new);
    public static DeferredHolder<AbstractAbility<?>, SelectHairstyleAbility> SELECT_HAIRSTYLE = REGISTRY.register("select_hairstyle", SelectHairstyleAbility::new);
    public static DeferredHolder<AbstractAbility<?>, SummonSharksAbility> SUMMON_SHARKS = REGISTRY.register("summon_sharks", SummonSharksAbility::new);
    public static DeferredHolder<AbstractAbility<?>, HypnosisAbility> HYPNOSIS = REGISTRY.register("hypnosis", HypnosisAbility::new);
    public static DeferredHolder<AbstractAbility<?>, SirenSingAbility> SIREN_SING = REGISTRY.register("siren_sing", SirenSingAbility::new);
    public static DeferredHolder<AbstractAbility<?>, PuddleAbility> PUDDLE = REGISTRY.register("puddle", PuddleAbility::new);
    public static DeferredHolder<AbstractAbility<?>, GrabEntityAbility> GRAB_ENTITY_ABILITY = REGISTRY.register("grab_entity", GrabEntityAbility::new);
    public static DeferredHolder<AbstractAbility<?>, ToggleNightVisionAbility> TOGGLE_NIGHT_VISION = REGISTRY.register("toggle_night_vision", ToggleNightVisionAbility::new);
    public static DeferredHolder<AbstractAbility<?>, ToggleWaveVisionAbility> TOGGLE_WAVE_VISION = REGISTRY.register("toggle_wave_vision", ToggleWaveVisionAbility::new);

    public static AbstractAbility<?> getAbility(ResourceLocation location) {
        return ChangedRegistry.ABILITY.get().getValue(location);
    }
}
