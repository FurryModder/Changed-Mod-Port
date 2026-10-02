package net.furrymodder.changed.client.renderer;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.client.renderer.layers.CustomEyesLayer;
import net.furrymodder.changed.client.renderer.layers.LatexParticlesLayer;
import net.furrymodder.changed.client.renderer.layers.TransfurCapeLayer;
import net.furrymodder.changed.client.renderer.model.DarkLatexDoubleYufengModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorLatexMaleDoubleHeadedWingedDragonModel;
import net.furrymodder.changed.client.renderer.model.armor.ArmorModelSet;
import net.furrymodder.changed.entity.beast.DarkLatexDoubleYufeng;
import net.furrymodder.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexDoubleYufengRenderer extends AdvancedHumanoidRenderer<DarkLatexDoubleYufeng, DarkLatexDoubleYufengModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/dark_latex_yufeng.png");

    public static final ArmorModelSet<DarkLatexDoubleYufeng, ArmorLatexMaleDoubleHeadedWingedDragonModel<DarkLatexDoubleYufeng>> ARMOR_MODEL_SET =
            ArmorModelSet.castOf(ArmorLatexMaleDoubleHeadedWingedDragonModel.MODEL_SET, ArmorLatexMaleDoubleHeadedWingedDragonModel::new);

    public DarkLatexDoubleYufengRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexDoubleYufengModel(context.bakeLayer(DarkLatexDoubleYufengModel.LAYER_LOCATION)), ARMOR_MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel(), model::isPartNotMask));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x242424))
                .withIris(CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
                        CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE))
                .build());
    }

    @Override
    public ResourceLocation getTextureLocation(DarkLatexDoubleYufeng entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}