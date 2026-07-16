package net.changed.init;

import net.changed.Changed;
import net.changed.entity.latex.LatexType;
import net.changed.entity.latex.SpreadingLatexType;
import net.changed.util.Cacheable;
import net.changed.world.LatexCoverState;
import net.minecraft.core.IdMapper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedLatexTypes {
    public static final DeferredRegister<LatexType> REGISTRY = ChangedRegistry.LATEX_TYPE.createDeferred(Changed.MODID);

    public static final DeferredHolder<LatexType, LatexType.None> NONE = REGISTRY.register("none", LatexType.None::new);
    public static final DeferredHolder<LatexType, SpreadingLatexType.DarkLatex> DARK_LATEX = REGISTRY.register("dark_latex", SpreadingLatexType.DarkLatex::new);
    public static final DeferredHolder<LatexType, SpreadingLatexType.WhiteLatex> WHITE_LATEX = REGISTRY.register("white_latex", SpreadingLatexType.WhiteLatex::new);

    public static final ResourceLocation LATEXCOVERSTATE_TO_ID = Changed.modResource("latexcoverstatetoid");
    public static final LatexCoverStateIdMapper LATEXCOVERSTATE_BY_ID = new LatexCoverStateIdMapper();

    public static IdMapper<LatexCoverState> getLatexCoverStateIDMap() {
        return LATEXCOVERSTATE_BY_ID;
    }

    public static class LatexCoverStateIdMapper extends IdMapper<LatexCoverState> {
        public void clear() {
            this.tToId.clear();
            this.idToT.clear();
            this.nextId = 0;
        }
    }
}
