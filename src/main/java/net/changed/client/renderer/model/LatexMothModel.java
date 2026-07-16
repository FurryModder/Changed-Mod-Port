package net.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.renderer.animate.AnimatorPresets;
import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.entity.beast.LatexMoth;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexMothModel extends AdvancedHumanoidModel<LatexMoth> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_moth"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final HumanoidAnimator<LatexMoth, LatexMothModel> animator;

    public LatexMothModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.Tail = Torso.getChild("Tail");
        this.LeftWing = Torso.getChild("LeftWing");
        this.RightWing = Torso.getChild("RightWing");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.mothLike(
                        Head, Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary), LeftWing, RightWing,
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(16, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(52, 16).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(56, 26).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(56, 9).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(48, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(32, 54).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(16, 57).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(48, 57).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(25, 16).addBox(-2.0F, -3.0F, -5.5F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(47, 16).addBox(-1.5F, -1.0F, -4.75F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition RightAntenna = Head.addOrReplaceChild("RightAntenna", CubeListBuilder.create().texOffs(56, 35).addBox(-3.23F, -1.9F, 0.06F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(64, 48).addBox(-2.23F, -0.9F, 0.06F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.52F, -9.0F, -1.76F, 0.3534F, 0.5672F, 1.309F));

        PartDefinition LeftAntenna = Head.addOrReplaceChild("LeftAntenna", CubeListBuilder.create().texOffs(64, 46).addBox(-2.77F, -1.9F, 0.06F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(64, 50).addBox(-1.77F, -0.9F, 0.06F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.52F, -9.0F, -1.76F, 0.3534F, -0.5672F, -1.309F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(48, 38).addBox(-4.0F, 0.1F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.45F))
                .texOffs(32, 29).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offset(-0.5F, 2.5F, 0.8F));

        PartDefinition RightWingPivot = RightWing.addOrReplaceChild("RightWingPivot", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4F, 0.0F, 0.0F, 0.1309F, 0.2182F, -0.0436F));

        PartDefinition WingBase_r1 = RightWingPivot.addOrReplaceChild("WingBase_r1", CubeListBuilder.create().texOffs(48, 63).addBox(-10.75F, -20.75F, 9.0F, 5.0F, 9.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(56, 37).addBox(-9.75F, -11.75F, 9.0F, 4.0F, 1.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(64, 52).addBox(-8.75F, -21.75F, 8.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 23.5F, -1.0F, 0.2182F, -0.2182F, 0.2618F));

        PartDefinition RightWing2 = Torso.addOrReplaceChild("RightWing2", CubeListBuilder.create(), PartPose.offset(-0.5F, 4.5F, 1.0F));

        PartDefinition RightWingPivot2 = RightWing2.addOrReplaceChild("RightWingPivot2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition WingBase_r2 = RightWingPivot2.addOrReplaceChild("WingBase_r2", CubeListBuilder.create().texOffs(58, 63).addBox(-10.75F, -20.75F, 9.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(64, 54).addBox(-8.75F, -21.75F, 8.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 23.5F, -1.0F, 0.2182F, -0.2182F, 0.2618F));

        PartDefinition WingBase_r3 = RightWingPivot2.addOrReplaceChild("WingBase_r3", CubeListBuilder.create().texOffs(64, 15).addBox(-9.75F, -11.75F, 9.0F, 4.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.1F, 21.625F, -1.425F, 0.2182F, -0.2182F, 0.2618F));

        PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offset(0.9F, 2.5F, 0.8F));

        PartDefinition LeftWingPivot = LeftWing.addOrReplaceChild("LeftWingPivot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, -0.2182F, 0.0436F));

        PartDefinition WingBase_r4 = LeftWingPivot.addOrReplaceChild("WingBase_r4", CubeListBuilder.create().texOffs(40, 64).addBox(5.75F, -21.75F, 8.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 62).addBox(5.75F, -20.75F, 9.0F, 5.0F, 9.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(56, 15).addBox(5.75F, -11.75F, 9.0F, 4.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 23.5F, -1.0F, 0.2182F, 0.2182F, -0.2618F));

        PartDefinition LeftWing2 = Torso.addOrReplaceChild("LeftWing2", CubeListBuilder.create(), PartPose.offset(0.5F, 4.5F, 1.0F));

        PartDefinition LeftWingPivot2 = LeftWing2.addOrReplaceChild("LeftWingPivot2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition WingBase_r5 = LeftWingPivot2.addOrReplaceChild("WingBase_r5", CubeListBuilder.create().texOffs(64, 56).addBox(5.75F, -21.75F, 8.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(30, 64).addBox(5.75F, -20.75F, 9.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 23.5F, -1.0F, 0.2182F, 0.2182F, -0.2618F));

        PartDefinition WingBase_r6 = LeftWingPivot2.addOrReplaceChild("WingBase_r6", CubeListBuilder.create().texOffs(64, 37).addBox(5.75F, -11.75F, 9.0F, 4.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.1F, 21.625F, -1.425F, 0.2182F, 0.2182F, -0.2618F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.4F, -0.4F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.4F, 0.3F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 0).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.4F, -0.3F, 1.0908F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.9F, 3.2F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(32, 16).addBox(-2.5F, -0.45F, -2.0F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.65F, 0.8F, 1.3963F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 38).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        Head.render(poseStack, buffer, packedLight, packedOverlay, color);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, color);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    @Override
    public HumanoidAnimator<LatexMoth, LatexMothModel> getAnimator(LatexMoth entity) {
        return animator;
    }
}