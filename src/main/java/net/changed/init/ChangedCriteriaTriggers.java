package net.changed.init;

import net.changed.Changed;
import net.changed.advancements.critereon.AquaticBreatheTrigger;
import net.changed.advancements.critereon.BeehiveSleepTrigger;
import net.changed.advancements.critereon.FlyingTrigger;
import net.changed.advancements.critereon.TameLatexTrigger;
import net.changed.advancements.critereon.TransfurTrigger;
import net.changed.advancements.critereon.WhiteLatexFuseTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ChangedCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> REGISTRY =
            DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, Changed.MODID);

    public static final TransfurTrigger TRANSFUR = new TransfurTrigger();
    public static final AquaticBreatheTrigger AQUATIC_BREATHE = new AquaticBreatheTrigger();
    public static final WhiteLatexFuseTrigger WHITE_LATEX_FUSE = new WhiteLatexFuseTrigger();
    public static final BeehiveSleepTrigger BEEHIVE_SLEEP = new BeehiveSleepTrigger();
    public static final TameLatexTrigger TAME_LATEX = new TameLatexTrigger();
    public static final FlyingTrigger FLYING = new FlyingTrigger();

    public static final DeferredHolder<CriterionTrigger<?>, TransfurTrigger> TRANSFUR_REGISTRY =
            REGISTRY.register("transfur", () -> TRANSFUR);
    public static final DeferredHolder<CriterionTrigger<?>, AquaticBreatheTrigger> AQUATIC_BREATHE_REGISTRY =
            REGISTRY.register("aquatic_breathe", () -> AQUATIC_BREATHE);
    public static final DeferredHolder<CriterionTrigger<?>, WhiteLatexFuseTrigger> WHITE_LATEX_FUSE_REGISTRY =
            REGISTRY.register("white_latex_fuse", () -> WHITE_LATEX_FUSE);
    public static final DeferredHolder<CriterionTrigger<?>, BeehiveSleepTrigger> BEEHIVE_SLEEP_REGISTRY =
            REGISTRY.register("beehive_sleep", () -> BEEHIVE_SLEEP);
    public static final DeferredHolder<CriterionTrigger<?>, TameLatexTrigger> TAME_LATEX_REGISTRY =
            REGISTRY.register("tame_latex", () -> TAME_LATEX);
    public static final DeferredHolder<CriterionTrigger<?>, FlyingTrigger> FLYING_REGISTRY =
            REGISTRY.register("flying", () -> FLYING);
}
