package net.changed.client.renderer;

import net.changed.Changed;
import net.changed.client.renderer.layers.CustomEyesLayer;
import net.changed.client.renderer.layers.GasMaskLayer;
import net.changed.client.renderer.layers.TransfurCapeLayer;
import net.changed.client.renderer.model.LatexCrystalWolfModel;
import net.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.changed.entity.beast.CrystalWolf;
import net.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrystalWolfRenderer extends AdvancedHumanoidRenderer<CrystalWolf, LatexCrystalWolfModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/crystal_wolf.png");

    public LatexCrystalWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrystalWolfModel(context.bakeLayer(LatexCrystalWolfModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x5a5a5a)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(CrystalWolf entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}