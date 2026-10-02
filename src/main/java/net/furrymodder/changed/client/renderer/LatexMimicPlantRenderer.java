package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.GasMaskLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.LatexMimicPlantModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.furrymodder.changed.entity.beast.LatexMimicPlant;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMimicPlantRenderer extends AdvancedHumanoidRenderer<LatexMimicPlant, LatexMimicPlantModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_mimic_plant.png");

    public LatexMimicPlantRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMimicPlantModel(context.bakeLayer(LatexMimicPlantModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xd5cf3c)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMimicPlant entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}