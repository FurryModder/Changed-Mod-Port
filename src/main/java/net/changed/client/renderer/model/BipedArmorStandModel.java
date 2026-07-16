package net.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.renderer.model.armor.ArmorModel;
import net.changed.client.renderer.model.armor.ArmorModelSet;
import net.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.changed.entity.decoration.BipedArmorStand;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class BipedArmorStandModel extends EntityModel<BipedArmorStand> {
    public static final ModelLayerLocation ARMOR_STAND = new ModelLayerLocation(Changed.modResource("biped_armor_stand"), "main");
    public static final ArmorModelSet<BipedArmorStand, Armor> MODEL_SET = ArmorModelSet.of(Changed.modResource("armor_biped_armor_stand"),
        BipedArmorStandModel::createArmorLayer, Armor::new);
    private final ModelPart Baseplate;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;

    public BipedArmorStandModel(ModelPart root) {
        this.Baseplate = root.getChild("Baseplate");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.LeftLeg = root.getChild("LeftLeg");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftArm = root.getChild("LeftArm");
        this.RightArm = root.getChild("RightArm");
    }

    @Override
    public void setupAnim(BipedArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head.xRot = ((float)Math.PI / 180F) * entity.getHeadPose().getX();
        this.Head.yRot = ((float)Math.PI / 180F) * entity.getHeadPose().getY();
        this.Head.zRot = ((float)Math.PI / 180F) * entity.getHeadPose().getZ();
        this.Torso.xRot = ((float)Math.PI / 180F) * entity.getBodyPose().getX();
        this.Torso.yRot = ((float)Math.PI / 180F) * entity.getBodyPose().getY();
        this.Torso.zRot = ((float)Math.PI / 180F) * entity.getBodyPose().getZ();
        this.LeftArm.xRot = ((float)Math.PI / 180F) * entity.getLeftArmPose().getX();
        this.LeftArm.yRot = ((float)Math.PI / 180F) * entity.getLeftArmPose().getY();
        this.LeftArm.zRot = ((float)Math.PI / 180F) * entity.getLeftArmPose().getZ();
        this.RightArm.xRot = ((float)Math.PI / 180F) * entity.getRightArmPose().getX();
        this.RightArm.yRot = ((float)Math.PI / 180F) * entity.getRightArmPose().getY();
        this.RightArm.zRot = ((float)Math.PI / 180F) * entity.getRightArmPose().getZ();
        this.LeftLeg.xRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getX();
        this.LeftLeg.yRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getY();
        this.LeftLeg.zRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getZ();
        this.RightLeg.xRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getX();
        this.RightLeg.yRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getY();
        this.RightLeg.zRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getZ();
        
        this.LeftArm.visible = entity.isShowArms();
        this.RightArm.visible = entity.isShowArms();
        this.Baseplate.visible = !entity.isNoBasePlate();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        Baseplate.render(poseStack, buffer, packedLight, packedOverlay);
    }

    public static class Armor extends ArmorStandArmorModel<BipedArmorStand> {
        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart LeftLeg;
        private final ModelPart RightLeg;
        private final ModelPart LeftArm;
        private final ModelPart RightArm;

        public Armor(ModelPart root, ArmorModel layer) {
            this.Head = root.getChild("Head");
            this.Torso = root.getChild("Torso");
            this.LeftLeg = root.getChild("LeftLeg");
            this.RightLeg = root.getChild("RightLeg");
            this.LeftArm = root.getChild("LeftArm");
            this.RightArm = root.getChild("RightArm");
        }

        @Override
        public void renderForSlot(BipedArmorStand entity, RenderLayerParent<? super BipedArmorStand, ?> parent, ItemStack stack, EquipmentSlot slot,
                                           PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            switch (slot) {
                case HEAD -> Head.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                case CHEST -> {
                    Torso.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    LeftArm.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    RightArm.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                }
                case LEGS -> {
                    Torso.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    RightLeg.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                }
                case FEET -> {
                    LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    RightLeg.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                }
            }
        }

        @Override
        public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
            if (armorSlot == EquipmentSlot.LEGS) {
                LatexHumanoidArmorModel.prepareUnifiedLegsForArmor(item, LeftLeg, RightLeg);
            }
        }

        @Override
        public void setupAnim(BipedArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.Head.xRot = ((float)Math.PI / 180F) * entity.getHeadPose().getX();
            this.Head.yRot = ((float)Math.PI / 180F) * entity.getHeadPose().getY();
            this.Head.zRot = ((float)Math.PI / 180F) * entity.getHeadPose().getZ();
            this.Torso.xRot = ((float)Math.PI / 180F) * entity.getBodyPose().getX();
            this.Torso.yRot = ((float)Math.PI / 180F) * entity.getBodyPose().getY();
            this.Torso.zRot = ((float)Math.PI / 180F) * entity.getBodyPose().getZ();
            this.LeftArm.xRot = ((float)Math.PI / 180F) * entity.getLeftArmPose().getX();
            this.LeftArm.yRot = ((float)Math.PI / 180F) * entity.getLeftArmPose().getY();
            this.LeftArm.zRot = ((float)Math.PI / 180F) * entity.getLeftArmPose().getZ();
            this.RightArm.xRot = ((float)Math.PI / 180F) * entity.getRightArmPose().getX();
            this.RightArm.yRot = ((float)Math.PI / 180F) * entity.getRightArmPose().getY();
            this.RightArm.zRot = ((float)Math.PI / 180F) * entity.getRightArmPose().getZ();
            this.LeftLeg.xRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getX();
            this.LeftLeg.yRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getY();
            this.LeftLeg.zRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getZ();
            this.RightLeg.xRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getX();
            this.RightLeg.yRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getY();
            this.RightLeg.zRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getZ();
        }
    }

    public static LayerDefinition createStandLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Baseplate = partdefinition.addOrReplaceChild("Baseplate", CubeListBuilder.create().texOffs(0, 32).addBox(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 26).addBox(-6.0F, 0.0F, -1.5F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(16, 0).addBox(-3.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 16).addBox(1.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-4.0F, 10.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 16).mirror().addBox(0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 0.5F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.5F, 0.0F));

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 9.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(24, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-0.99F, 0.625F, -1.9F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(48, 16).addBox(-1.0F, -8.025F, 0.275F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create(), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 9.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(32, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.01F, 0.625F, -1.9F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(48, 16).addBox(-1.0F, -8.025F, 0.275F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create(), PartPose.offset(0.0F, 4.325F, -4.425F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        LatexHumanoidArmorModel.addUnifiedLegs(partdefinition, layer);

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.dualDeformation), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation).mirror(false), PartPose.offset(5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
