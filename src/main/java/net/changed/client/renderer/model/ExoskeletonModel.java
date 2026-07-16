package net.changed.client.renderer.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.animations.Limb;
import net.changed.entity.robot.Exoskeleton;
import net.changed.init.ChangedTags;
import net.changed.util.EntityUtil;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExoskeletonModel extends EntityModel<Exoskeleton> implements HeadedModel {
    public static final ModelLayerLocation LAYER_LOCATION_SUIT = new ModelLayerLocation(Changed.modResource("exoskeleton"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_VISOR = new ModelLayerLocation(Changed.modResource("exoskeleton"), "visor");
    public static final ModelLayerLocation LAYER_LOCATION_HUMAN = new ModelLayerLocation(Changed.modResource("exoskeleton"), "human");
    private final ModelPart Root;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Back;
    private final ModelPart BackBraceLeft;
    private final ModelPart BackBraceRight;
    private final ModelPart HeadSupport;
    private final ModelPart RightArm;
    private final ModelPart RightArmBraceLeft;
    private final ModelPart RightArmBraceRight;
    private final ModelPart LeftArm;
    private final ModelPart LeftArmBraceLeft;
    private final ModelPart LeftArmBraceRight;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;

    public ExoskeletonModel(ModelPart root) {
        this.Root = root;
        this.Head = Root.getChild("Head");
        this.Torso = Root.getChild("Torso");
        this.Back = Torso.getChild("Back");
        this.BackBraceLeft = Back.getChild("Left");
        this.BackBraceRight = Back.getChild("Right");
        this.HeadSupport = Torso.getChild("HeadSupport");
        this.RightArm = Root.getChild("RightArm");
        this.RightArmBraceLeft = RightArm.getChild("RightBraceLeft");
        this.RightArmBraceRight = RightArm.getChild("RightBraceRight");
        this.LeftArm = Root.getChild("LeftArm");
        this.LeftArmBraceLeft = LeftArm.getChild("LeftBraceLeft");
        this.LeftArmBraceRight = LeftArm.getChild("LeftBraceRight");
        this.RightLeg = Root.getChild("RightLeg");
        this.LeftLeg = Root.getChild("LeftLeg");
    }

    public static LayerDefinition createSuitLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(18, 57).addBox(-2.5F, 1.5F, -0.5F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 9.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(16, 29).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(0, 30).addBox(-2.99F, -0.125F, -2.9F, 4.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(46, 19).mirror().addBox(-7.0F, -6.725F, 0.8F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.13F)).mirror(false), PartPose.offsetAndRotation(5.0F, 7.075F, -4.975F, -0.7418F, 0.0F, 0.0F));

        PartDefinition RightArch_r2 = RightFoot.addOrReplaceChild("RightArch_r2", CubeListBuilder.create().texOffs(23, 1).addBox(-3.0F, -7.45F, -0.725F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.13F)), PartPose.offsetAndRotation(1.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, 1.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(6, 56).addBox(1.5F, 1.5F, -0.5F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 9.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(32, 29).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(46, 19).addBox(-2.0F, -6.725F, 0.8F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.13F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.7418F, 0.0F, 0.0F));

        PartDefinition LeftArch_r2 = LeftFoot.addOrReplaceChild("LeftArch_r2", CubeListBuilder.create().texOffs(12, 34).addBox(-2.0F, -7.45F, -0.725F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.13F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(0, 18).addBox(-2.0F, 1.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(46, 12).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Visor = Head.addOrReplaceChild("Visor", CubeListBuilder.create().texOffs(0, 7).addBox(-5.0F, -6.0F, -4.0F, 10.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 27).addBox(4.0F, -6.0F, -3.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(28, 54).addBox(-5.0F, -6.0F, -3.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 38).addBox(-5.0F, -6.5F, -1.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(56, 0).addBox(-5.0F, -8.5F, 0.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(56, 19).addBox(4.0F, -8.5F, 0.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 34).addBox(-4.0F, -8.5F, 0.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(54, 32).addBox(2.0F, -8.5F, 0.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(46, 42).addBox(-5.5F, -6.0F, -0.5F, 1.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(48, 0).addBox(4.5F, -6.0F, -0.5F, 1.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(10, 42).addBox(4.0F, -6.5F, -1.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(18, 11).addBox(-5.0F, -5.0F, 5.0F, 10.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(22, 61).addBox(-5.0F, -5.0F, 3.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(28, 61).addBox(4.0F, -5.0F, 3.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.75F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(50, 38).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(54, 42).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(32, 9).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(12, 30).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.75F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(50, 53).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 56).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(44, 35).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(12, 32).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Back = Torso.addOrReplaceChild("Back", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -22.0F, 2.0F, 10.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(22, 9).addBox(-4.0F, -17.5F, 3.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 54).addBox(-4.0F, -19.5F, 3.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(34, 27).addBox(-4.0F, -21.5F, 3.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(30, 44).addBox(-3.0F, -25.0F, 4.0F, 2.0F, 9.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(46, 16).addBox(-1.0F, -25.0F, 4.0F, 5.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(46, 16).addBox(-8.0F, -25.0F, 4.0F, 5.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(34, 61).addBox(-7.0F, -15.0F, 1.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(36, 7).addBox(-7.0F, -16.0F, 2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(22, 57).addBox(2.0F, -15.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(10, 58).addBox(2.0F, -15.0F, 1.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(20, 42).addBox(2.0F, -16.0F, 2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 23).addBox(-7.0F, -15.5F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 26.0F, 0.0F));

        PartDefinition Left = Back.addOrReplaceChild("Left", CubeListBuilder.create().texOffs(34, 20).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(10, 50).addBox(-4.5F, -2.5F, -5.5F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 37).addBox(-0.5F, 1.5F, -5.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(36, 50).addBox(-4.5F, 1.5F, -5.5F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.5F, -19.5F, 2.5F, 0.0F, -0.3491F, 0.0F));

        PartDefinition Right = Back.addOrReplaceChild("Right", CubeListBuilder.create().texOffs(34, 13).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(36, 0).addBox(-0.5F, 1.5F, -5.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(50, 35).addBox(0.5F, 1.5F, -5.5F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(46, 24).addBox(0.5F, -2.5F, -5.5F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-6.5F, -19.5F, 2.5F, 0.0F, 0.3491F, 0.0F));

        PartDefinition HeadSupport = Torso.addOrReplaceChild("HeadSupport", CubeListBuilder.create().texOffs(46, 48).addBox(0.0F, -6.25F, 0.0F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-0.5F, 1.0F, 4.25F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(18, 21).addBox(-3.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.30F))
                .texOffs(40, 7).addBox(-4.0F, 2.0F, 2.0F, 6.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 6).addBox(-1.0F, -1.0F, 3.0F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.9599F));

        PartDefinition RightBraceLeft = RightArm.addOrReplaceChild("RightBraceLeft", CubeListBuilder.create().texOffs(10, 53).addBox(-2.5F, -2.0F, -5.5F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 27).addBox(-0.5F, -2.0F, -4.5F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, 4.0F, 2.5F, 0.0F, -0.1745F, 0.0F));

        PartDefinition RightBraceRight = RightArm.addOrReplaceChild("RightBraceRight", CubeListBuilder.create().texOffs(36, 53).addBox(-0.5F, -2.0F, -5.5F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 46).addBox(-0.5F, -2.0F, -4.5F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.5F, 4.0F, 2.5F, 0.0F, 0.1745F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(18, 13).addBox(-1.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.30F))
                .texOffs(36, 37).addBox(-2.0F, 2.0F, 2.0F, 6.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 6).addBox(-1.0F, -1.0F, 3.0F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.9599F));

        PartDefinition LeftBraceLeft = LeftArm.addOrReplaceChild("LeftBraceLeft", CubeListBuilder.create().texOffs(50, 48).addBox(-2.5F, -2.0F, -5.5F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 42).addBox(-0.5F, -2.0F, -4.5F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, 4.0F, 2.5F, 0.0F, -0.1745F, 0.0F));

        PartDefinition LeftBraceRight = LeftArm.addOrReplaceChild("LeftBraceRight", CubeListBuilder.create().texOffs(20, 52).addBox(-0.5F, -2.0F, -5.5F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 44).addBox(-0.5F, -2.0F, -4.5F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, 4.0F, 2.5F, 0.0F, 0.1745F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createVisorLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Visor = Head.addOrReplaceChild("Visor", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -4.0F, 10.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 4).addBox(-5.0F, -6.0F, -3.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(6, 4).addBox(4.0F, -6.0F, -3.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -1.0F));

        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    public static LayerDefinition createHumanLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLegReplacement = partdefinition.addOrReplaceChild("RightLegReplacement", CubeListBuilder.create(), PartPose.offset(-2.5F, 9.5F, 0.0F));

        PartDefinition RightThigh_r2 = RightLegReplacement.addOrReplaceChild("RightThigh_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightPants = RightLegReplacement.addOrReplaceChild("RightPants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightThighLayer_r1 = RightPants.addOrReplaceChild("RightThighLayer_r1", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLegReplacementLower2 = RightLegReplacement.addOrReplaceChild("RightLegReplacementLower2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightThigh_r3 = RightLegReplacementLower2.addOrReplaceChild("RightThigh_r3", CubeListBuilder.create().texOffs(0, 37).addBox(-2.0F, 0.0F, -1.75F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(0, 21).addBox(-2.0F, 0.0F, -1.75F, 4.0F, 7.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 4.8815F, -1.0822F, 1.2654F, 0.0F, 0.0F));

        PartDefinition LeftLegReplacement = partdefinition.addOrReplaceChild("LeftLegReplacement", CubeListBuilder.create(), PartPose.offset(2.5F, 9.5F, 0.0F));

        PartDefinition LeftThigh_r2 = LeftLegReplacement.addOrReplaceChild("LeftThigh_r2", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftPants = LeftLegReplacement.addOrReplaceChild("LeftPants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftThighLayer_r1 = LeftPants.addOrReplaceChild("LeftThighLayer_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLegReplacementLower = LeftLegReplacement.addOrReplaceChild("LeftLegReplacementLower", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftThigh_r3 = LeftLegReplacementLower.addOrReplaceChild("LeftThigh_r3", CubeListBuilder.create().texOffs(0, 53).addBox(-2.0F, 0.0F, -1.75F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(16, 53).addBox(-2.0F, 0.0F, -1.75F, 4.0F, 7.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 4.8815F, -1.0822F, 1.2654F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Exoskeleton entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // TODO default animation

        if (entity.isCharging()) {
            Head.xRot = Mth.DEG_TO_RAD * 42.5F;
            Head.yRot = 0F;
            Head.zRot = 0F;

            Torso.xRot = 0F;
            Torso.yRot = 0F;
            Torso.zRot = 0F;

            RightArm.xRot = Mth.DEG_TO_RAD * 15F;
            RightArm.yRot = 0F;
            RightArm.zRot = Mth.DEG_TO_RAD * 55F;
            LeftArm.xRot = Mth.DEG_TO_RAD * 15F;
            LeftArm.yRot = 0F;
            LeftArm.zRot = Mth.DEG_TO_RAD * -55F;

            RightLeg.xRot = Mth.DEG_TO_RAD * 17.5F;
            RightLeg.yRot = 0F;
            RightLeg.zRot = 0F;
            LeftLeg.xRot = Mth.DEG_TO_RAD * 17.5F;
            LeftLeg.yRot = 0F;
            LeftLeg.zRot = 0F;
        }

        else {
            Head.xRot = 0F;
            Head.yRot = netHeadYaw * ((float)Math.PI / 180F);
            Head.zRot = 0F;

            Torso.xRot = 0F;
            Torso.yRot = 0F;
            Torso.zRot = 0F;

            RightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.125F;
            RightArm.yRot = 0F;
            RightArm.zRot = Mth.DEG_TO_RAD * 35f;
            LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.125F;
            LeftArm.yRot = 0F;
            LeftArm.zRot = Mth.DEG_TO_RAD * -35f;

            RightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            RightLeg.yRot = 0F;
            RightLeg.zRot = Mth.DEG_TO_RAD * 2.5f;
            LeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
            LeftLeg.yRot = 0F;
            LeftLeg.zRot = Mth.DEG_TO_RAD * -2.5f;
        }
    }

    @Override
    public void prepareMobModel(Exoskeleton entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);

        BackBraceLeft.yRot = Mth.DEG_TO_RAD * -10.0F;
        BackBraceRight.yRot = Mth.DEG_TO_RAD * 10.0F;
        RightArmBraceLeft.yRot = 0.0F;
        RightArmBraceRight.yRot = Mth.DEG_TO_RAD * 30.0F;
        LeftArmBraceLeft.yRot = Mth.DEG_TO_RAD * -30.0F;
        LeftArmBraceRight.yRot = 0.0F;

        if (entity.isCharging()) {
            HeadSupport.y = 7.0F;
            Head.z = 4.0F;

            RightArm.x = -5.0F;
            LeftArm.x = 5.0F;
        }

        else {
            HeadSupport.y = 1.0F;
            Head.z = 0.0F;

            RightArm.x = -6.0F;
            LeftArm.x = 6.0F;
        }
    }

    public void prepareMobModel(ItemStack itemStack, float limbSwing, float limbSwingAmount, float partialTick) {
        HeadSupport.y = 7.0F;
        BackBraceLeft.yRot = 0.0F;
        BackBraceRight.yRot = 0.0F;
        RightArmBraceLeft.yRot = 0.0F;
        RightArmBraceRight.yRot = 0.0F;
        LeftArmBraceLeft.yRot = 0.0F;
        LeftArmBraceRight.yRot = 0.0F;
    }

    public <T extends LivingEntity, M extends EntityModel<T>> void animateWearerPose(T wearer, M wearerModel, PoseStack poseStack, ItemStack stack) {
        poseStack.translate(0.0, 0.5 / 16.0, 0.0);

        if (wearerModel instanceof AdvancedHumanoidModel<?>) return;

        poseStack.translate(0.0, 1.5 / 16.0, 0.0);
    }

    public <T extends LivingEntity, M extends EntityModel<T>> void animateWearerLimbs(M wearerModel, ItemStack stack) {
        if (wearerModel instanceof AdvancedHumanoidModel<?>) return;

        Limb.RIGHT_ARM.getModelPartSafe(wearerModel).ifPresent(part -> part.zRot += Mth.DEG_TO_RAD * 12f);
        Limb.LEFT_ARM.getModelPartSafe(wearerModel).ifPresent(part -> part.zRot += Mth.DEG_TO_RAD * -12f);

        Limb.RIGHT_LEG.getModelPartSafe(wearerModel).ifPresent(part -> {
            part.xRot += -0.2182F;
            part.zRot += Mth.DEG_TO_RAD * 2.5f;
        });
        Limb.LEFT_LEG.getModelPartSafe(wearerModel).ifPresent(part -> {
            part.xRot += -0.2182F;
            part.zRot += Mth.DEG_TO_RAD * -2.5f;
        });
    }

    public <T extends LivingEntity, M extends EntityModel<T>> void matchWearersAnim(M wearerModel, ItemStack stack) {
        Limb.HEAD.getModelPartSafe(wearerModel).ifPresent(this.Head::copyFrom);
        Limb.TORSO.getModelPartSafe(wearerModel).ifPresent(this.Torso::copyFrom);
        Limb.RIGHT_ARM.getModelPartSafe(wearerModel).ifPresent(this.RightArm::copyFrom);
        Limb.LEFT_ARM.getModelPartSafe(wearerModel).ifPresent(this.LeftArm::copyFrom);
        Limb.RIGHT_LEG.getModelPartSafe(wearerModel).ifPresent(this.RightLeg::copyFrom);
        Limb.LEFT_LEG.getModelPartSafe(wearerModel).ifPresent(this.LeftLeg::copyFrom);

        if (wearerModel instanceof AdvancedHumanoidModel<?>) return;

        this.RightLeg.xRot -= -0.2182F;
        this.LeftLeg.xRot -= -0.2182F;
    }

    @Override
    public ModelPart getHead() {
        return Head;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Head.render(poseStack, buffer, packedLight, packedOverlay, color);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, color);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    private final ResourceLocation TEXTURE = Changed.modResource("textures/exoskeleton.png");
    private final ResourceLocation TEXTURE_CHARGING = Changed.modResource("textures/exoskeleton_charging.png");

    public ResourceLocation getTexture(Exoskeleton entity) {
        return entity.isCharging() ? TEXTURE_CHARGING : TEXTURE;
    }

    public ResourceLocation getTexture(ItemStack stack) {
        return TEXTURE;
    }

    public static class ReplacementLimbs extends EntityModel<Exoskeleton> {
        private final ModelPart Root;
        private final ModelPart LeftLeg;
        private final ModelPart RightLeg;

        public ReplacementLimbs(ModelPart root) {
            this.Root = root;
            this.RightLeg = Root.getChild("RightLegReplacement");
            this.LeftLeg = Root.getChild("LeftLegReplacement");
        }

        @Override
        public void setupAnim(Exoskeleton entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            // TODO default animation
        }

        public <T extends LivingEntity, M extends EntityModel<T>> void matchWearersAnim(M wearerModel, ItemStack stack) {
            Limb.RIGHT_LEG.getModelPartSafe(wearerModel).ifPresent(this.RightLeg::copyFrom);
            Limb.LEFT_LEG.getModelPartSafe(wearerModel).ifPresent(this.LeftLeg::copyFrom);

            this.RightLeg.xRot -= -0.2182F;
            this.LeftLeg.xRot -= -0.2182F;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
            RightLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
            LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        }

        public ResourceLocation getTexture(LivingEntity entity) {
            if (entity instanceof AbstractClientPlayer clientPlayer)
                return clientPlayer.getSkin().texture();
            return DefaultPlayerSkin.get(entity.getUUID()).texture();
        }
    }

    public static class VisorModel extends EntityModel<Exoskeleton> {
        public static final ResourceLocation VISOR_BLANK = Changed.modResource("textures/models/exoskeleton_visor/blank.png");
        public static final ResourceLocation VISOR_FIRE = Changed.modResource("textures/models/exoskeleton_visor/fire.png");
        public static final ResourceLocation VISOR_ACTIVE = Changed.modResource("textures/models/exoskeleton_visor/active.png");
        public static final int VISOR_CHARGE_STATES = 17;
        public static final List<ResourceLocation> VISOR_CHARGE = Util.make(() -> {
            final var builder = ImmutableList.<ResourceLocation>builder();
            for (int i = 0; i < VISOR_CHARGE_STATES; ++i) {
                builder.add(Changed.modResource(String.format("textures/models/exoskeleton_visor/charge%s.png", i)));
            }
            return builder.build();
        });
        public static final int VISOR_HYPNO_STATES = 2;
        public static final List<ResourceLocation> VISOR_HYPNO = Util.make(() -> {
            final var builder = ImmutableList.<ResourceLocation>builder();
            for (int i = 0; i < VISOR_HYPNO_STATES; ++i) {
                builder.add(Changed.modResource(String.format("textures/models/exoskeleton_visor/hypno%s.png", i)));
            }
            return builder.build();
        });

        private final ModelPart Head;

        public VisorModel(ModelPart root) {
            super(RenderType::eyes);
            this.Head = root.getChild("Head");
        }

        @Override
        public void setupAnim(Exoskeleton entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            // TODO default animation
        }

        public <T extends LivingEntity, M extends EntityModel<T>> void matchParentAnim(ExoskeletonModel parent) {
            this.Head.copyFrom(parent.Head);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
            Head.render(poseStack, buffer, packedLight, packedOverlay, color);
        }

        public ResourceLocation getTexture(LivingEntity wearer, ItemStack itemStack) {
            if (EntityUtil.maybeGetOverlaying(wearer).getType().is(ChangedTags.EntityTypes.BENIGN_LATEXES)) {
                return wearer.tickCount % 12 < 6 ? VISOR_HYPNO.get(0) : VISOR_HYPNO.get(1);
            }

            return VISOR_ACTIVE;
        }

        public ResourceLocation getTexture(Exoskeleton exoskeleton) {
            if (exoskeleton.isCharging()) {
                float scaledCharge = exoskeleton.getCharge() * (VISOR_CHARGE_STATES - 1);
                int chargeLow = Mth.floor(scaledCharge);
                int chargeHigh = Mth.ceil(scaledCharge);

                if (exoskeleton.tickCount % 20 < 10)
                    return VISOR_CHARGE.get(chargeLow);
                else
                    return VISOR_CHARGE.get(chargeHigh);
            }

            if (exoskeleton.hasActiveAttackTarget()) {
                return VISOR_FIRE;
            }

            return VISOR_BLANK;
        }
    }
}
