package net.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.renderer.animate.AnimatorPresets;
import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.model.LeglessModel;
import net.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorSnakeAbdomenModel<T extends ChangedEntity> extends LatexHumanoidArmorModel<T, ArmorSnakeAbdomenModel<T>> implements LeglessModel {
    public static final ArmorModelSet<ChangedEntity, ArmorSnakeAbdomenModel<ChangedEntity>> MODEL_SET =
            ArmorModelSet.of(Changed.modResource("armor_snake_abdomen"), ArmorSnakeAbdomenModel::createArmorLayer, ArmorSnakeAbdomenModel::new);

    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final ModelPart TailPrimary;
    private final HumanoidAnimator<T, ArmorSnakeAbdomenModel<T>> animator;

    public ArmorSnakeAbdomenModel(ModelPart modelPart, ArmorModel model) {
        super(modelPart, model);
        this.Abdomen = modelPart.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");

        this.TailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = TailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f)
                .addPreset(AnimatorPresets.snakeAbdomenArmor(
                        Abdomen, LowerAbdomen,
                        Tail, List.of(TailPrimary, tailSecondary, tailTertiary)));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 1F, -2.5F, 8.0F, 2.0F, 5.0F, layer.deformation.extend(-0.2F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(0, 8).addBox(-4.5F, 0.0F, -3.0F, 9.0F, 6.0F, 5.0F, layer.deformation), PartPose.offset(0.0F, 2.75F, 0.5F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, 0.25F, -3.0F, 8.0F, 6.0F, 4.0F, layer.dualDeformation.extend(0.25F)), PartPose.offset(0.0F, 5.5F, 0.5F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(40, 21).addBox(-3.5F, 0.25F, -3.0F, 7.0F, 5.0F, 4.0F, layer.altDeformation.extend(0.15F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, 0.25F, -3.0F, 6.0F, 5.0F, 4.0F, layer.altDeformation), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create().texOffs(44, 0).addBox(-2.0F, 0.25F, -2.5F, 4.0F, 3.0F, 3.0F, layer.altDeformation.extend(0.25F)), PartPose.offset(0.0F, 5.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public HumanoidAnimator<T, ArmorSnakeAbdomenModel<T>> getAnimator(T entity) {
        return animator;
    }

    @Override
    public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
        super.prepareVisibility(armorSlot, item);
        setAllPartsVisibility(TailPrimary, true);
        if (armorSlot == EquipmentSlot.LEGS) {
            setAllPartsVisibility(TailPrimary, false);
        }
    }

    @Override
    public void renderForSlot(T entity, RenderLayerParent<? super T, ?> parent, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.scaleForSlot(parent, slot, poseStack);

        switch (slot) {
            case LEGS, FEET -> Abdomen.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
        }

        poseStack.popPose();
    }

    public ModelPart getArm(HumanoidArm arm) {
        return null;
    }

    public ModelPart getLeg(HumanoidArm leg) {
        return null;
    }

    public ModelPart getHead() {
        return NULL_PART;
    }

    public ModelPart getTorso() {
        return NULL_PART;
    }

    @Override
    public ModelPart getAbdomen() {
        return Abdomen;
    }

    @Override
    public boolean shouldModelSit(T entity) {
        return super.shouldModelSit(entity) || LeglessModel.shouldLeglessSit(entity);
    }
}
