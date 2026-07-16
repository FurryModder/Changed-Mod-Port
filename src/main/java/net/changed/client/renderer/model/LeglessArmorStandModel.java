package net.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.renderer.model.armor.ArmorModel;
import net.changed.client.renderer.model.armor.ArmorModelSet;
import net.changed.entity.decoration.CentaurArmorStand;
import net.changed.entity.decoration.LeglessArmorStand;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class LeglessArmorStandModel extends EntityModel<LeglessArmorStand> {
    public static final ModelLayerLocation ARMOR_STAND = new ModelLayerLocation(Changed.modResource("legless_armor_stand"), "main");
    public static final ArmorModelSet<LeglessArmorStand, LeglessArmorStandModel.ArmorUpper> MODEL_SET_UPPER = ArmorModelSet.of(Changed.modResource("armor_upper_legless_armor_stand"),
            LeglessArmorStandModel::createArmorUpperLayer, LeglessArmorStandModel.ArmorUpper::new);
    public static final ArmorModelSet<LeglessArmorStand, LeglessArmorStandModel.ArmorLower> MODEL_SET_LOWER = ArmorModelSet.of(Changed.modResource("armor_lower_legless_armor_stand"),
            LeglessArmorStandModel::createArmorLowerLayer, LeglessArmorStandModel.ArmorLower::new);
    private final ModelPart Baseplate;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;

    public LeglessArmorStandModel(ModelPart root) {
        this.Baseplate = root.getChild("Baseplate");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Abdomen = root.getChild("Abdomen");
        this.LeftArm = root.getChild("LeftArm");
        this.RightArm = root.getChild("RightArm");
    }

    @Override
    public void setupAnim(LeglessArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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

        this.LeftArm.visible = entity.isShowArms();
        this.RightArm.visible = entity.isShowArms();
        this.Baseplate.visible = !entity.isNoBasePlate();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Abdomen.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        Baseplate.render(poseStack, buffer, packedLight, packedOverlay);
    }

    public static class ArmorUpper extends ArmorStandArmorModel<LeglessArmorStand> {
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
        public void renderForSlot(LeglessArmorStand entity, RenderLayerParent<? super LeglessArmorStand, ?> parent, ItemStack stack, EquipmentSlot slot,
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
        public void setupAnim(LeglessArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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

    public static class ArmorLower extends ArmorStandArmorModel<LeglessArmorStand> {
        private final ModelPart Abdomen;
        private final ModelPart LowerAbdomen;
        private final ModelPart Tail;
        private final ModelPart TailPrimary;

        public ArmorLower(ModelPart root, ArmorModel layer) {
            this.Abdomen = root.getChild("Abdomen");
            this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
            this.Tail = LowerAbdomen.getChild("Tail");

            this.TailPrimary = Tail.getChild("TailPrimary");
        }

        @Override
        public void renderForSlot(LeglessArmorStand entity, RenderLayerParent<? super LeglessArmorStand, ?> parent, ItemStack stack, EquipmentSlot slot,
                                  PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            switch (slot) {
                case LEGS, FEET -> Abdomen.render(poseStack, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
            }
        }

        @Override
        public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
            TailPrimary.visible = true;
            if (armorSlot == EquipmentSlot.LEGS) {
                TailPrimary.visible = false;
            }
        }

        @Override
        public void setupAnim(LeglessArmorStand entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        }
    }

    public static LayerDefinition createStandLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(28, 33).addBox(-1.0F, 0.0F, -1.35F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 38).addBox(-3.0F, 2.5F, -0.85F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -1.65F, -0.8727F, -0.1745F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(12, 27).addBox(-1.0F, -0.25F, -0.4F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 38).addBox(-3.0F, 2.75F, 0.1F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.5F, -0.95F, 1.0913F, -0.028F, -0.0334F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(28, 23).addBox(-1.0F, -0.25F, -1.5F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.5F, 1.1F, 1.2787F, -0.7203F, 0.1958F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(36, 17).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 7.25F, -0.5F, 0.0F, 0.0F, -1.309F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create().texOffs(12, 41).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.25F, 0.0F, 0.0F, 0.0F, -1.0908F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create().texOffs(12, 41).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 4.75F, 0.0F, 0.0F, 0.0F, -0.8727F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create().texOffs(0, 41).addBox(-1.0F, -0.4F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.2F, 0.0F, 0.3054F, 0.0F, -0.8727F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 17).addBox(-6.0F, 0.0F, -1.5F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(36, 26).addBox(-3.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(36, 17).addBox(1.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-4.0F, 10.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, -2.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(20, 23).addBox(0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.5F, -2.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -1.5F, -2.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(36, 35).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, -2.0F));

        PartDefinition Baseplate = partdefinition.addOrReplaceChild("Baseplate", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(8, 27).addBox(-0.5F, -15.5F, -2.5F, 1.0F, 15.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createArmorUpperLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, -3.5F, -2.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation).mirror(false), PartPose.offset(5.0F, -1.5F, -2.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(-5.0F, -1.5F, -2.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.dualDeformation), PartPose.offset(0.0F, -3.5F, -2.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static LayerDefinition createArmorLowerLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(40, 7).addBox(-4.0F, 1.0F, -2.35F, 8.0F, 5.0F, 4.0F, layer.deformation), PartPose.offsetAndRotation(0.0F, 8.0F, -1.65F, -0.8727F, -0.1745F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(0, 8).addBox(-4.75F, 0.75F, -1.9F, 9.0F, 7.0F, 5.0F, layer.deformation), PartPose.offsetAndRotation(0.0F, 6.5F, -0.95F, 1.0913F, -0.028F, -0.0334F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, 0.75F, -2.5F, 8.0F, 6.0F, 4.0F, layer.dualDeformation.extend(0.65F)), PartPose.offsetAndRotation(0.0F, 8.5F, 1.1F, 1.2787F, -0.7203F, 0.1958F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(40, 21).addBox(-3.5F, 0.75F, -2.0F, 7.0F, 5.0F, 4.0F, layer.altDeformation.extend(0.26F)), PartPose.offsetAndRotation(0.0F, 7.25F, -0.5F, 0.0F, 0.0F, -1.309F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 5.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 6.25F, 0.0F, 0.0F, 0.0F, -1.0908F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create().texOffs(44, 0).addBox(-2.0F, 1.0F, -1.5F, 4.0F, 3.0F, 3.0F, layer.altDeformation.extend(0.15F)), PartPose.offsetAndRotation(0.0F, 4.75F, 0.0F, 0.0F, 0.0F, -0.8727F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create().texOffs(24, 25).addBox(-1.5F, 0.6F, -1.5F, 3.0F, 4.0F, 3.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.0F, 4.2F, 0.0F, 0.3054F, 0.0F, -0.8727F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
