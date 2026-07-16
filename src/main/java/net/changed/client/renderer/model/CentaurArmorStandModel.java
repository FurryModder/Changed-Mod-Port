package net.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.renderer.model.armor.ArmorModel;
import net.changed.client.renderer.model.armor.ArmorModelSet;
import net.changed.entity.decoration.CentaurArmorStand;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class CentaurArmorStandModel extends EntityModel<CentaurArmorStand> {
    public static final ModelLayerLocation ARMOR_STAND = new ModelLayerLocation(Changed.modResource("centaur_armor_stand"), "main");
    public static final ArmorModelSet<CentaurArmorStand, CentaurArmorStandModel.ArmorUpper> MODEL_SET_UPPER = ArmorModelSet.of(Changed.modResource("armor_upper_centaur_armor_stand"),
            CentaurArmorStandModel::createArmorUpperLayer, CentaurArmorStandModel.ArmorUpper::new);
    public static final ArmorModelSet<CentaurArmorStand, CentaurArmorStandModel.ArmorLower> MODEL_SET_LOWER = ArmorModelSet.of(Changed.modResource("armor_lower_centaur_armor_stand"),
            CentaurArmorStandModel::createArmorLowerLayer, CentaurArmorStandModel.ArmorLower::new);
    private final ModelPart Baseplate;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LowerTorso;
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackRightLeg;
    private final ModelPart BackLeftLeg;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;

    public CentaurArmorStandModel(ModelPart root) {
        this.Baseplate = root.getChild("Baseplate");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.LowerTorso = root.getChild("LowerTorso");
        this.FrontLeftLeg = LowerTorso.getChild("LeftLeg");
        this.FrontRightLeg = LowerTorso.getChild("RightLeg");
        this.BackLeftLeg = LowerTorso.getChild("LeftLeg2");
        this.BackRightLeg = LowerTorso.getChild("RightLeg2");
        this.LeftArm = root.getChild("LeftArm");
        this.RightArm = root.getChild("RightArm");
    }

    @Override
    public void setupAnim(CentaurArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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

        this.FrontLeftLeg.xRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getX();
        this.FrontLeftLeg.yRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getY();
        this.FrontLeftLeg.zRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getZ();
        this.FrontRightLeg.xRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getX();
        this.FrontRightLeg.yRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getY();
        this.FrontRightLeg.zRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getZ();
        this.BackLeftLeg.xRot = ((float)Math.PI / 180F) * entity.getBackLeftLegPose().getX();
        this.BackLeftLeg.yRot = ((float)Math.PI / 180F) * entity.getBackLeftLegPose().getY();
        this.BackLeftLeg.zRot = ((float)Math.PI / 180F) * entity.getBackLeftLegPose().getZ();
        this.BackRightLeg.xRot = ((float)Math.PI / 180F) * entity.getBackRightLegPose().getX();
        this.BackRightLeg.yRot = ((float)Math.PI / 180F) * entity.getBackRightLegPose().getY();
        this.BackRightLeg.zRot = ((float)Math.PI / 180F) * entity.getBackRightLegPose().getZ();

        this.LeftArm.visible = entity.isShowArms();
        this.RightArm.visible = entity.isShowArms();
        this.Baseplate.visible = !entity.isNoBasePlate();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Head.render(poseStack, buffer, packedLight, packedOverlay, color);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, color);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, color);
        Baseplate.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public static class ArmorUpper extends ArmorStandArmorModel<CentaurArmorStand> {
        private final ModelPart RightArm;
        private final ModelPart LeftArm;
        private final ModelPart Head;
        private final ModelPart Torso;

        public ArmorUpper(ModelPart root, ArmorModel layer) {
            this.Head = root.getChild("Head");
            this.Torso = root.getChild("Torso");
            this.RightArm = root.getChild("RightArm");
            this.LeftArm = root.getChild("LeftArm");
        }

        @Override
        public void renderForSlot(CentaurArmorStand entity, RenderLayerParent<? super CentaurArmorStand, ?> parent, ItemStack stack, EquipmentSlot slot,
                                  PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            switch (slot) {
                case HEAD -> Head.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                case CHEST -> {
                    Torso.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    LeftArm.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    RightArm.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                }
            }
        }

        @Override
        public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {

        }

        @Override
        public void setupAnim(CentaurArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        }
    }

    public static class ArmorLower extends ArmorStandArmorModel<CentaurArmorStand> {
        private final ModelPart Torso;
        private final ModelPart FrontRightLeg;
        private final ModelPart FrontLeftLeg;
        private final ModelPart BackRightLeg;
        private final ModelPart BackLeftLeg;
        private final ModelPart LowerTorso;

        public ArmorLower(ModelPart root, ArmorModel layer) {
            this.Torso = root.getChild("Torso");
            this.LowerTorso = root.getChild("LowerTorso");
            this.FrontRightLeg = LowerTorso.getChild("RightLeg");
            this.FrontLeftLeg = LowerTorso.getChild("LeftLeg");
            this.BackRightLeg = LowerTorso.getChild("RightLeg2");
            this.BackLeftLeg = LowerTorso.getChild("LeftLeg2");
        }

        @Override
        public void renderForSlot(CentaurArmorStand entity, RenderLayerParent<? super CentaurArmorStand, ?> parent, ItemStack stack, EquipmentSlot slot,
                                  PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            switch (slot) {
                case LEGS -> {
                    Torso.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                    LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                }
                case FEET -> {
                    LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
                }
            }
        }

        @Override
        public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {

        }

        @Override
        public void setupAnim(CentaurArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.Torso.xRot = ((float)Math.PI / 180F) * entity.getBodyPose().getX();
            this.Torso.yRot = ((float)Math.PI / 180F) * entity.getBodyPose().getY();
            this.Torso.zRot = ((float)Math.PI / 180F) * entity.getBodyPose().getZ();

            this.FrontLeftLeg.xRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getX();
            this.FrontLeftLeg.yRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getY();
            this.FrontLeftLeg.zRot = ((float)Math.PI / 180F) * entity.getLeftLegPose().getZ();
            this.FrontRightLeg.xRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getX();
            this.FrontRightLeg.yRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getY();
            this.FrontRightLeg.zRot = ((float)Math.PI / 180F) * entity.getRightLegPose().getZ();
            this.BackLeftLeg.xRot = ((float)Math.PI / 180F) * entity.getBackLeftLegPose().getX();
            this.BackLeftLeg.yRot = ((float)Math.PI / 180F) * entity.getBackLeftLegPose().getY();
            this.BackLeftLeg.zRot = ((float)Math.PI / 180F) * entity.getBackLeftLegPose().getZ();
            this.BackRightLeg.xRot = ((float)Math.PI / 180F) * entity.getBackRightLegPose().getX();
            this.BackRightLeg.yRot = ((float)Math.PI / 180F) * entity.getBackRightLegPose().getY();
            this.BackRightLeg.zRot = ((float)Math.PI / 180F) * entity.getBackRightLegPose().getZ();
        }
    }

    public static LayerDefinition createStandLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Baseplate = partdefinition.addOrReplaceChild("Baseplate", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -1.0F, -11.0F, 12.0F, 1.0F, 22.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 23).addBox(-6.0F, 0.0F, -1.5F, 12.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(8, 37).addBox(-3.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(16, 37).addBox(1.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.5F, -7.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(44, 35).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.5F, -7.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(28, 35).addBox(0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(5.0F, -0.5F, -7.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(36, 35).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, -0.5F, -7.0F));

        PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create().texOffs(28, 29).addBox(-4.0F, -2.0F, 13.5F, 8.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(28, 29).addBox(-4.0F, -2.0F, -1.5F, 8.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 23).addBox(-3.0F, -2.0F, 1.5F, 2.0F, 2.0F, 12.0F, CubeDeformation.NONE)
                .texOffs(0, 23).addBox(1.0F, -2.0F, 1.5F, 2.0F, 2.0F, 12.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 9.5F, -7.0F));

        PartDefinition LeftLeg = LowerTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, -1.7F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(16, 46).addBox(-1.0F, -5.89F, -3.2461F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7848F, 3.7028F));

        PartDefinition LeftLowerLeg_r1 = LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, 4.1138F, 3.7342F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 5.7152F, -4.3278F));

        PartDefinition RightLeg = LowerTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -1.7F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(16, 46).addBox(-8.5F, -5.89F, -3.2461F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(7.75F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.7848F, 3.7028F));

        PartDefinition RightLowerLeg_r1 = RightLowerLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, 4.1138F, 3.7342F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.25F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7152F, -4.3278F));

        PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-3.75F, 0.0F, 16.0F));

        PartDefinition RightThigh_r1 = RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(44, 44).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-0.99F, 0.625F, -1.9F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(16, 37).addBox(-1.0F, -8.025F, 0.275F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create(), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(3.75F, 0.0F, 16.0F));

        PartDefinition LeftThigh_r1 = LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(8, 46).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-0.99F, 0.625F, -1.9F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(16, 37).addBox(-1.0F, -8.025F, 0.275F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create(), PartPose.offset(0.0F, 4.325F, -4.425F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    public static LayerDefinition createArmorUpperLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.dualDeformation), PartPose.offset(0.0F, -2.5F, -7.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, -2.5F, -7.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(-5.0F, -0.5F, -7.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation).mirror(false), PartPose.offset(5.0F, -0.5F, -7.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static LayerDefinition createArmorLowerLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(35, 11).addBox(-4.0F, 6.0F, -2.0F, 8.0F, 4.0F, 4.0F, layer.dualDeformation.extend(-0.1F)), PartPose.offset(0.0F, -2.5F, -7.0F));

        PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 6.0F, 19.0F, layer.altDeformation), PartPose.offset(0.0F, 9.5F, -7.0F));

        PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, 16.375F));

        PartDefinition LeftThigh_r1 = LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(35, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(0, 10).mirror().addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation.extend(0.005F)).mirror(false), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-3.5F, 0.0F, 16.375F));

        PartDefinition RightThigh_r1 = RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, layer.altDeformation.extend(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, layer.deformation).mirror(false), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = LowerTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, -1.7F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(35, 0).mirror().addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)).mirror(false), PartPose.offsetAndRotation(0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7848F, 3.7028F));

        PartDefinition LeftLowerLeg_r1 = LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(35, 0).mirror().addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(0, 25).addBox(-1.95F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F, layer.deformation), PartPose.offset(0.0F, 5.7152F, -4.3278F));

        PartDefinition RightLeg = LowerTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -1.7F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-9.5F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.1F)), PartPose.offsetAndRotation(7.75F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.7848F, 3.7028F));

        PartDefinition RightLowerLeg_r1 = RightLowerLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(35, 0).addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.25F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-2.025F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F, layer.deformation).mirror(false), PartPose.offset(0.25F, 5.7152F, -4.3278F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
