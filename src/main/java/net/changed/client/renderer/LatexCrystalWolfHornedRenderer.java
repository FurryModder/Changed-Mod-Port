package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexCrystalWolfHornedModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.changed.entity.beast.CrystalWolfHorned;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrystalWolfHornedRenderer extends AdvancedHumanoidRenderer<CrystalWolfHorned, LatexCrystalWolfHornedModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/crystal_wolf_horned.png");

    public LatexCrystalWolfHornedRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrystalWolfHornedModel(context.bakeLayer(LatexCrystalWolfHornedModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x5a5a5a)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(CrystalWolfHorned entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}