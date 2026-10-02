package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexMutantBloodcellWolfModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.furrymodder.changed.entity.beast.LatexMutantBloodcellWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMutantBloodcellWolfRenderer extends AdvancedHumanoidRenderer<LatexMutantBloodcellWolf, LatexMutantBloodcellWolfModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_mutant_bloodcell_wolf.png");

    public LatexMutantBloodcellWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMutantBloodcellWolfModel(context.bakeLayer(LatexMutantBloodcellWolfModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMutantBloodcellWolf entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}