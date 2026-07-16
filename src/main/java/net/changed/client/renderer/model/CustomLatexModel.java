package net.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.Changed;
import net.changed.client.renderer.animate.AnimatorPresets;
import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.animate.tail.*;
import net.changed.entity.beast.CustomLatexEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CustomLatexModel extends AdvancedHumanoidModel<CustomLatexEntity> implements LowerTorsoedModel, LeglessModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("custom_latex"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart TorsoShort;
    private final ModelPart Tail;

    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart LeglessTail;

    private final ModelPart LowerTorso;
    private final ModelPart Saddle;
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackRightLeg;
    private final ModelPart BackLeftLeg;
    private final ModelPart TaurTail;

    private final ModelPart ShortHair;
    private final ModelPart LongHair;

    private final ModelPart WolfEars;
    private final ModelPart CatEars;
    private final ModelPart DragonEars;
    private final ModelPart SharkEars;

    private final ModelPart GenericTorso;
    private final ModelPart ChiseledTorso;
    private final ModelPart FemaleTorso;
    private final ModelPart HeavyTorso;

    private final ModelPart GenericTorsoShort;
    private final ModelPart ChiseledTorsoShort;
    private final ModelPart FemaleTorsoShort;
    private final ModelPart HeavyTorsoShort;

    private final ModelPart WolfTail;
    private final ModelPart CatTail;
    private final ModelPart SharkTail;
    private final ModelPart DragonTail;
    private final ModelPart WolfTailTaur;
    private final ModelPart CatTailTaur;
    private final ModelPart SharkTailTaur;
    private final ModelPart DragonTailTaur;

    private final ModelPart SharkRightArm;
    private final ModelPart SharkLeftArm;
    private final ModelPart WyvernRightArm;
    private final ModelPart WyvernLeftArm;

    private final Map<CustomLatexEntity.LegType, Map<CustomLatexEntity.TailType, HumanoidAnimator<CustomLatexEntity, CustomLatexModel>>> animators;

    private double yOffset = 0.0;
    private float bodyScale = 1.0F;
    private float headScale = 1.0F;

    public CustomLatexModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.TorsoShort = root.getChild("TorsoShort");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.LeglessTail = LowerAbdomen.getChild("Tail2");

        this.LowerTorso = root.getChild("LowerTorso");
        this.Saddle = LowerTorso.getChild("Saddle");
        this.FrontRightLeg = LowerTorso.getChild("RightLeg3");
        this.FrontLeftLeg = LowerTorso.getChild("LeftLeg3");
        this.BackRightLeg = LowerTorso.getChild("RightLeg2");
        this.BackLeftLeg = LowerTorso.getChild("LeftLeg2");
        this.TaurTail = LowerTorso.getChild("Tail3");

        this.GenericTorso = Torso.getChild("Generic");
        this.ChiseledTorso = Torso.getChild("Chiseled");
        this.FemaleTorso = Torso.getChild("Female");
        this.HeavyTorso = Torso.getChild("Heavy");

        this.GenericTorsoShort = TorsoShort.getChild("Generic2");
        this.ChiseledTorsoShort = TorsoShort.getChild("Chiseled2");
        this.FemaleTorsoShort = TorsoShort.getChild("Female2");
        this.HeavyTorsoShort = TorsoShort.getChild("Heavy2");

        final var ears = Head.getChild("Ears");
        this.WolfEars = ears.getChild("WolfEars");
        this.CatEars = ears.getChild("CatEars");
        this.DragonEars = ears.getChild("DragonEars");
        this.SharkEars = ears.getChild("SharkEars");

        final var hair = Head.getChild("Hair");
        this.ShortHair = hair.getChild("Short");
        this.LongHair = hair.getChild("Long");

        this.WolfTail = Tail.getChild("WolfTail");
        this.CatTail = Tail.getChild("CatTail");
        this.SharkTail = Tail.getChild("SharkTail");
        this.DragonTail = Tail.getChild("DragonTail");
        this.WolfTailTaur = TaurTail.getChild("WolfTail2");
        this.CatTailTaur = TaurTail.getChild("CatTail2");
        this.SharkTailTaur = TaurTail.getChild("SharkTail2");
        this.DragonTailTaur = TaurTail.getChild("DragonTail2");

        this.SharkRightArm = RightArm.getChild("RightArmShark");
        this.SharkLeftArm = LeftArm.getChild("LeftArmShark");
        this.WyvernRightArm = RightArm.getChild("RightArmWyvern");
        this.WyvernLeftArm = LeftArm.getChild("LeftArmWyvern");

        animators = new EnumMap<>(CustomLatexEntity.LegType.class);
        for (var legType : CustomLatexEntity.LegType.values())
            animators.put(legType, new EnumMap<>(CustomLatexEntity.TailType.class));

        final var bipedalAnimators = animators.get(CustomLatexEntity.LegType.BIPEDAL);
        for (var tailType : CustomLatexEntity.TailType.values())
            bipedalAnimators.put(tailType, HumanoidAnimator.of(this).hipOffset(-1.5f));

        final var taurAnimators = animators.get(CustomLatexEntity.LegType.CENTAUR);
        for (var tailType : CustomLatexEntity.TailType.values())
            taurAnimators.put(tailType, HumanoidAnimator.of(this).forwardOffset(-7.0f).hipOffset(-1.5f).legLength(13.5f).torsoLength(11.05f));

        final var merAnimators = animators.get(CustomLatexEntity.LegType.MERMAID);
        for (var tailType : CustomLatexEntity.TailType.values())
            merAnimators.put(tailType, HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f));

        { // Bipedal
            var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
            var leftFoot = leftLowerLeg.getChild("LeftFoot");
            var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
            var rightFoot = rightLowerLeg.getChild("RightFoot");

            {
                var tailPrimary = WolfTail.getChild("TailPrimary");
                var tailSecondary = tailPrimary.getChild("TailSecondary");
                var tailTertiary = tailSecondary.getChild("TailTertiary");

                bipedalAnimators.get(CustomLatexEntity.TailType.WOLF).addPreset(AnimatorPresets.wolfLike(
                        Head, WolfEars.getChild("LeftEar"), WolfEars.getChild("RightEar"),
                        Torso, LeftArm, RightArm,
                        WolfTail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
            }

            {
                var tailPrimary = CatTail.getChild("TailPrimary2");
                var tailSecondary = tailPrimary.getChild("TailSecondary2");
                var tailTertiary = tailSecondary.getChild("TailTertiary2");
                var tailQuaternary = tailTertiary.getChild("TailQuaternary");

                bipedalAnimators.get(CustomLatexEntity.TailType.CAT).addPreset(AnimatorPresets.catLike(
                        Head, CatEars.getChild("LeftEar2"), CatEars.getChild("RightEar2"),
                        Torso, LeftArm, RightArm,
                        CatTail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
            }

            {
                var tailPrimary = SharkTail.getChild("TailPrimary3");
                var tailSecondary = tailPrimary.getChild("TailSecondary3");
                var tailTertiary = tailSecondary.getChild("TailTertiary3");

                bipedalAnimators.get(CustomLatexEntity.TailType.SHARK).addPreset(AnimatorPresets.sharkLike(
                        Head,
                        Torso, LeftArm, RightArm,
                        SharkTail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
            }

            {
                var tailPrimary = DragonTail.getChild("TailPrimary4");
                var tailSecondary = tailPrimary.getChild("TailSecondary4");
                var tailTertiary = tailSecondary.getChild("TailTertiary4");
                var tailQuaternary = tailTertiary.getChild("TailQuaternary2");

                bipedalAnimators.get(CustomLatexEntity.TailType.DRAGON).addPreset(AnimatorPresets.dragonLike(
                        Head,
                        Torso, LeftArm, RightArm,
                        DragonTail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
            }
        }

        { // Mer
            var tailPrimary = LeglessTail.getChild("TailPrimary5");
            var tailSecondary = tailPrimary.getChild("TailSecondary5");
            var tailTertiary = tailSecondary.getChild("TailTertiary5");
            var tailQuaternary = tailTertiary.getChild("TailQuaternary3");
            var tailQuintary = tailQuaternary.getChild("TailQuintary");

            final var singleton = merAnimators.get(CustomLatexEntity.TailType.SHARK);

            singleton.addPreset(AnimatorPresets.leglessShark(Head, TorsoShort, LeftArm, RightArm, Abdomen, LowerAbdomen, LeglessTail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary, tailQuintary)));

            for (var tailType : CustomLatexEntity.TailType.values())
                merAnimators.put(tailType, singleton);
        }

        { // Taur
            var leftLowerLeg = FrontLeftLeg.getChild("LeftLowerLeg3");
            var rightLowerLeg = FrontRightLeg.getChild("RightLowerLeg3");

            var leftLowerLeg2 = BackLeftLeg.getChild("LeftLowerLeg2");
            var leftFoot2 = leftLowerLeg2.getChild("LeftFoot2");
            var rightLowerLeg2 = BackRightLeg.getChild("RightLowerLeg2");
            var rightFoot2 = rightLowerLeg2.getChild("RightFoot2");

            {
                var tailPrimary = WolfTailTaur.getChild("TailPrimary6");
                var tailSecondary = tailPrimary.getChild("TailSecondary6");
                var tailTertiary = tailSecondary.getChild("TailTertiary6");

                taurAnimators.get(CustomLatexEntity.TailType.WOLF).addPreset(AnimatorPresets.taurLike(
                                Head, WolfEars.getChild("LeftEar"), WolfEars.getChild("RightEar"),
                                Torso, LeftArm, RightArm,
                                LowerTorso, FrontLeftLeg, leftLowerLeg, leftLowerLeg.getChild("LeftFoot3"), FrontRightLeg, rightLowerLeg, rightLowerLeg.getChild("RightFoot3"),
                                BackLeftLeg, leftLowerLeg2, leftFoot2, leftFoot2.getChild("LeftPad2"), BackRightLeg, rightLowerLeg2, rightFoot2, rightFoot2.getChild("RightPad2")))
                        .addAnimator(new WolfTailInitAnimator<>(WolfTailTaur, List.of(tailPrimary, tailSecondary, tailTertiary)));
            }

            {
                var tailPrimary = CatTailTaur.getChild("TailPrimary7");
                var tailSecondary = tailPrimary.getChild("TailSecondary7");
                var tailTertiary = tailSecondary.getChild("TailTertiary7");
                var tailQuaternary = tailTertiary.getChild("TailQuaternary4");

                taurAnimators.get(CustomLatexEntity.TailType.CAT).addPreset(AnimatorPresets.taurLike(
                                Head, CatEars.getChild("LeftEar2"), CatEars.getChild("RightEar2"),
                                Torso, LeftArm, RightArm,
                                LowerTorso, FrontLeftLeg, leftLowerLeg, leftLowerLeg.getChild("LeftFoot3"), FrontRightLeg, rightLowerLeg, rightLowerLeg.getChild("RightFoot3"),
                                BackLeftLeg, leftLowerLeg2, leftFoot2, leftFoot2.getChild("LeftPad2"), BackRightLeg, rightLowerLeg2, rightFoot2, rightFoot2.getChild("RightPad2")))
                        .addAnimator(new CatTailInitAnimator<>(CatTailTaur, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary)));
            }

            {
                var tailPrimary = SharkTailTaur.getChild("TailPrimary8");
                var tailSecondary = tailPrimary.getChild("TailSecondary8");
                var tailTertiary = tailSecondary.getChild("TailTertiary8");

                taurAnimators.get(CustomLatexEntity.TailType.SHARK).addPreset(AnimatorPresets.taurLike(
                                Head, WolfEars.getChild("LeftEar"), WolfEars.getChild("RightEar"),
                                Torso, LeftArm, RightArm,
                                LowerTorso, FrontLeftLeg, leftLowerLeg, leftLowerLeg.getChild("LeftFoot3"), FrontRightLeg, rightLowerLeg, rightLowerLeg.getChild("RightFoot3"),
                                BackLeftLeg, leftLowerLeg2, leftFoot2, leftFoot2.getChild("LeftPad2"), BackRightLeg, rightLowerLeg2, rightFoot2, rightFoot2.getChild("RightPad2")))
                        .addAnimator(new SharkTailInitAnimator<>(SharkTailTaur, List.of(tailPrimary, tailSecondary, tailTertiary)));
            }

            {
                var tailPrimary = DragonTailTaur.getChild("TailPrimary9");
                var tailSecondary = tailPrimary.getChild("TailSecondary9");
                var tailTertiary = tailSecondary.getChild("TailTertiary9");
                var tailQuaternary = tailTertiary.getChild("TailQuaternary5");

                taurAnimators.get(CustomLatexEntity.TailType.DRAGON).addPreset(AnimatorPresets.taurLike(
                                Head, WolfEars.getChild("LeftEar"), WolfEars.getChild("RightEar"),
                                Torso, LeftArm, RightArm,
                                LowerTorso, FrontLeftLeg, leftLowerLeg, leftLowerLeg.getChild("LeftFoot3"), FrontRightLeg, rightLowerLeg, rightLowerLeg.getChild("RightFoot3"),
                                BackLeftLeg, leftLowerLeg2, leftFoot2, leftFoot2.getChild("LeftPad2"), BackRightLeg, rightLowerLeg2, rightFoot2, rightFoot2.getChild("RightPad2")))
                        .addAnimator(new DragonTailInitAnimator<>(DragonTailTaur, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary)));
            }
        }
    }

    public static LayerDefinition createBodyLayer() {

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(92, 37).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(108, 25).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(54, 16).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(106, 108).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(40, 92).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(108, 35).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(112, 14).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(66, 109).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(86, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(64, 44).addBox(-4.5F, -1.25F, -2.5F, 9.0F, 7.0F, 5.0F, new CubeDeformation(0.06F)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition Tail2 = LowerAbdomen.addOrReplaceChild("Tail2", CubeListBuilder.create().texOffs(68, 26).addBox(-4.0F, -0.75F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 5.5F, 0.0F));

        PartDefinition TailPrimary5 = Tail2.addOrReplaceChild("TailPrimary5", CubeListBuilder.create().texOffs(0, 87).addBox(-3.5F, -0.25F, -2.0F, 7.0F, 5.0F, 4.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition TailSecondary5 = TailPrimary5.addOrReplaceChild("TailSecondary5", CubeListBuilder.create().texOffs(92, 16).addBox(-3.0F, -0.25F, -2.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition TailTertiary5 = TailSecondary5.addOrReplaceChild("TailTertiary5", CubeListBuilder.create().texOffs(62, 116).addBox(-2.0F, -0.25F, -1.5F, 4.0F, 3.0F, 3.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition TailQuaternary3 = TailTertiary5.addOrReplaceChild("TailQuaternary3", CubeListBuilder.create().texOffs(16, 118).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.0F, 2.55F, 0.0F));

        PartDefinition TailQuintary = TailQuaternary3.addOrReplaceChild("TailQuintary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, -0.5F));

        PartDefinition Base_r1 = TailQuintary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(124, 32).addBox(-0.5F, 9.4311F, 0.1673F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(76, 116).addBox(-0.5F, 1.5311F, 0.1673F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Base_r2 = TailQuintary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(96, 118).addBox(-0.5F, -10.3457F, 2.9744F, 1.0F, 7.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(46, 115).addBox(-0.5F, -12.3457F, -0.0256F, 1.0F, 10.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, -2.3562F, 0.0F, 0.0F));

        PartDefinition Base_r3 = TailQuintary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(122, 100).addBox(-1.0F, 0.1701F, -0.3405F, 2.0F, 5.0F, 2.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, 0.0F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(32, 44).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,  CubeDeformation.NONE)
                .texOffs(108, 70).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F,  CubeDeformation.NONE)
                .texOffs(40, 90).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Ears = Head.addOrReplaceChild("Ears", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition WolfEars = Ears.addOrReplaceChild("WolfEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar = WolfEars.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(122, 122).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(34, 99).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(18, 96).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(100, 113).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = WolfEars.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(66, 104).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(100, 109).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(112, 23).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(124, 34).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition CatEars = Ears.addOrReplaceChild("CatEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar2 = CatEars.addOrReplaceChild("RightEar2", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, 0.0F));

        PartDefinition rightear_r1 = RightEar2.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(120, 76).addBox(4.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(2.5F, 30.0F, 0.0F, -0.5236F, -0.1745F, -0.2618F));

        PartDefinition LeftEar2 = CatEars.addOrReplaceChild("LeftEar2", CubeListBuilder.create(), PartPose.offset(2.5F, -5.0F, 0.0F));

        PartDefinition leftear_r1 = LeftEar2.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(0, 122).addBox(-6.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(-2.5F, 30.0F, 0.0F, -0.5236F, 0.1745F, 0.2618F));

        PartDefinition DragonEars = Ears.addOrReplaceChild("DragonEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar3 = DragonEars.addOrReplaceChild("RightEar3", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.3F));

        PartDefinition rightear_r2 = RightEar3.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(122, 115).addBox(4.0F, -24.8F, -22.0F, 2.0F, 6.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(100, 115).addBox(4.0F, -26.0F, -25.0F, 2.0F, 8.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, -0.1309F, -0.2618F));

        PartDefinition LeftEar3 = DragonEars.addOrReplaceChild("LeftEar3", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.3F));

        PartDefinition leftear_r2 = LeftEar3.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(10, 122).addBox(-6.0F, -24.8F, -22.0F, 2.0F, 6.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(100, 115).addBox(-6.0F, -26.0F, -25.0F, 2.0F, 8.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, 0.1309F, 0.2618F));

        PartDefinition SharkEars = Ears.addOrReplaceChild("SharkEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition HeadFin_r1 = SharkEars.addOrReplaceChild("HeadFin_r1", CubeListBuilder.create().texOffs(88, 74).addBox(-0.25F, -1.0F, 0.0F, 6.0F, 3.0F, 2.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -6.5F, -1.0F, 1.0263F, -0.733F, -0.9599F));

        PartDefinition HeadFin_r2 = SharkEars.addOrReplaceChild("HeadFin_r2", CubeListBuilder.create().texOffs(48, 74).addBox(-0.25F, -2.0F, 0.0F, 6.0F, 3.0F, 2.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -6.5F, -1.0F, -1.0263F, -0.733F, -2.1817F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Short = Hair.addOrReplaceChild("Short", CubeListBuilder.create().texOffs(32, 60).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F))
                .texOffs(54, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Long = Hair.addOrReplaceChild("Long", CubeListBuilder.create().texOffs(36, 25).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(0, 44).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Generic = Torso.addOrReplaceChild("Generic", CubeListBuilder.create().texOffs(0, 62).addBox(-4.0F, -26.0F, -2.0F, 8.0F, 12.0F, 4.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 26.0F, 0.0F));

        PartDefinition Chiseled = Torso.addOrReplaceChild("Chiseled", CubeListBuilder.create().texOffs(68, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F,  CubeDeformation.NONE)
                .texOffs(68, 36).addBox(-4.0F, 5.5F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F))
                .texOffs(88, 56).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Muscles = Chiseled.addOrReplaceChild("Muscles", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Muscles.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(62, 122).mirror().addBox(0.3F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, -0.0087F, 0.0004F));

        PartDefinition cube_r2 = Muscles.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(62, 122).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, 0.0087F, -0.0004F));

        PartDefinition Abs = Muscles.addOrReplaceChild("Abs", CubeListBuilder.create(), PartPose.offset(0.0F, 4.3F, 0.2F));

        PartDefinition cube_r3 = Abs.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(36, 123).addBox(-2.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.225F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r4 = Abs.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(74, 95).addBox(0.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(0.05F, 2.45F, -2.225F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r5 = Abs.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(56, 92).addBox(-3.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.2F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r6 = Abs.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(56, 92).mirror().addBox(0.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.45F, -2.2F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r7 = Abs.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(56, 92).addBox(-3.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)), PartPose.offsetAndRotation(-0.05F, 2.35F, -2.45F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r8 = Abs.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(56, 92).mirror().addBox(0.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.35F, -2.45F, 0.0F, -0.0087F, 0.0F));

        PartDefinition Female = Torso.addOrReplaceChild("Female", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition TorsoLower_r1 = Female.addOrReplaceChild("TorsoLower_r1", CubeListBuilder.create().texOffs(64, 69).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(0, 78).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Waist_r1 = Female.addOrReplaceChild("Waist_r1", CubeListBuilder.create().texOffs(84, 91).addBox(-4.0F, -3.4F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 12.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Plantoids = Female.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, -2.0F));

        PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(120, 70).addBox(-4.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                .texOffs(84, 118).addBox(0.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(124, 28).addBox(-0.5F, -1.3F, -0.1F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

        PartDefinition Heavy = Torso.addOrReplaceChild("Heavy", CubeListBuilder.create().texOffs(0, 62).addBox(-4.0F, -26.0F, -2.0F, 8.0F, 12.0F, 4.0F,  CubeDeformation.NONE)
                .texOffs(24, 74).addBox(-4.0F, -20.0F, -2.15F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 26.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition WolfTail = Tail.addOrReplaceChild("WolfTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary = WolfTail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r4 = TailPrimary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(108, 85).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r5 = TailSecondary.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(84, 79).addBox(-2.5F, -0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 2.5F));

        PartDefinition Base_r6 = TailTertiary.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(16, 110).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 1.8326F, 0.0F, 0.0F));

        PartDefinition CatTail = Tail.addOrReplaceChild("CatTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary2 = CatTail.addOrReplaceChild("TailPrimary2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r7 = TailPrimary2.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(84, 109).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

        PartDefinition TailSecondary2 = TailPrimary2.addOrReplaceChild("TailSecondary2", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r8 = TailSecondary2.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(88, 63).addBox(-2.5F, -0.45F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));

        PartDefinition TailTertiary2 = TailSecondary2.addOrReplaceChild("TailTertiary2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));

        PartDefinition Base_r9 = TailTertiary2.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(48, 79).addBox(-2.5F, 4.55F, -3.3F, 5.0F, 8.0F, 5.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5272F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary2.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.5F));

        PartDefinition Base_r10 = TailQuaternary.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(0, 115).addBox(-2.0F, 5.5F, -3.8F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.7017F, 0.0F, 0.0F));

        PartDefinition SharkTail = Tail.addOrReplaceChild("SharkTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary3 = SharkTail.addOrReplaceChild("TailPrimary3", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.75F));

        PartDefinition TailFin_r1 = TailPrimary3.addOrReplaceChild("TailFin_r1", CubeListBuilder.create().texOffs(124, 23).addBox(-4.0F, 4.0F, -0.75F, 1.0F, 4.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(28, 118).addBox(-4.0F, 0.0F, 0.25F, 1.0F, 9.0F, 1.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, 1.75F, 1.0F, 1.789F, 0.0F, 0.0F));

        PartDefinition Base_r11 = TailPrimary3.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(108, 94).addBox(-2.0F, -1.075F, 0.375F, 4.0F, 2.0F, 2.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.875F, 0.85F, 1.9199F, 0.0F, 0.0F));

        PartDefinition Base_r12 = TailPrimary3.addOrReplaceChild("Base_r12", CubeListBuilder.create().texOffs(92, 25).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary3 = TailPrimary3.addOrReplaceChild("TailSecondary3", CubeListBuilder.create(), PartPose.offset(0.0F, 3.25F, 7.25F));

        PartDefinition Base_r13 = TailSecondary3.addOrReplaceChild("Base_r13", CubeListBuilder.create().texOffs(110, 115).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.25F, 1.0F, 1.309F, 0.0F, 0.0F));

        PartDefinition TailTertiary3 = TailSecondary3.addOrReplaceChild("TailTertiary3", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 4.5F));

        PartDefinition Base_r14 = TailTertiary3.addOrReplaceChild("Base_r14", CubeListBuilder.create().texOffs(118, 23).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(24, 62).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5F, 4.25F, 1.1345F, 0.0F, 0.0F));

        PartDefinition Base_r15 = TailTertiary3.addOrReplaceChild("Base_r15", CubeListBuilder.create().texOffs(32, 123).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(54, 115).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 10.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.5F, 4.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r16 = TailTertiary3.addOrReplaceChild("Base_r16", CubeListBuilder.create().texOffs(40, 83).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.25F, 0.25F, 1.4835F, 0.0F, 0.0F));

        PartDefinition DragonTail = Tail.addOrReplaceChild("DragonTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary4 = DragonTail.addOrReplaceChild("TailPrimary4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition Base_r17 = TailPrimary4.addOrReplaceChild("Base_r17", CubeListBuilder.create().texOffs(110, 45).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition TailSecondary4 = TailPrimary4.addOrReplaceChild("TailSecondary4", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

        PartDefinition Base_r18 = TailSecondary4.addOrReplaceChild("Base_r18", CubeListBuilder.create().texOffs(56, 95).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition TailTertiary4 = TailSecondary4.addOrReplaceChild("TailTertiary4", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

        PartDefinition Base_r19 = TailTertiary4.addOrReplaceChild("Base_r19", CubeListBuilder.create().texOffs(0, 96).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition TailQuaternary2 = TailTertiary4.addOrReplaceChild("TailQuaternary2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

        PartDefinition Base_r20 = TailQuaternary2.addOrReplaceChild("Base_r20", CubeListBuilder.create().texOffs(122, 94).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 83).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,  CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition RightArmShark = RightArm.addOrReplaceChild("RightArmShark", CubeListBuilder.create(), PartPose.offset(-2.0789F, 2.8746F, 1.1151F));

        PartDefinition Spike_r1 = RightArmShark.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(118, 123).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(110, 123).addBox(-0.5F, -2.5F, -2.0F, 1.0F, 5.0F, 1.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6425F, 0.8346F, 3.1091F));

        PartDefinition RightArmWyvern = RightArm.addOrReplaceChild("RightArmWyvern", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Wing_r1 = RightArmWyvern.addOrReplaceChild("Wing_r1", CubeListBuilder.create().texOffs(0, -1).addBox(-6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 1).addBox(-6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 2).addBox(-6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.6545F, -0.2182F, 0.0F));

        PartDefinition Wing_r2 = RightArmWyvern.addOrReplaceChild("Wing_r2", CubeListBuilder.create().texOffs(5, 1).addBox(-6.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.6981F, -0.2182F, 0.0F));

        PartDefinition Wing_r3 = RightArmWyvern.addOrReplaceChild("Wing_r3", CubeListBuilder.create().texOffs(0, -3).addBox(-6.0F, -10.75F, 17.0F, 0.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.9599F, -0.2182F, 0.0F));

        PartDefinition Wing_r4 = RightArmWyvern.addOrReplaceChild("Wing_r4", CubeListBuilder.create().texOffs(0, 11).addBox(-6.5F, -13.89F, 7.68F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.3491F, -0.2182F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(68, 79).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,  CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition LeftArmShark = LeftArm.addOrReplaceChild("LeftArmShark", CubeListBuilder.create(), PartPose.offset(2.6568F, 2.0711F, 1.6568F));

        PartDefinition Spike_r2 = LeftArmShark.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(104, 85).addBox(0.875F, -1.5F, -0.5F, 1.0F, 5.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(114, 123).addBox(-0.125F, -0.5F, -0.5F, 1.0F, 4.0F, 1.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4796F, -0.6979F, 0.7102F));

        PartDefinition LeftArmWyvern = LeftArm.addOrReplaceChild("LeftArmWyvern", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Wing_r5 = LeftArmWyvern.addOrReplaceChild("Wing_r5", CubeListBuilder.create().texOffs(35, -1).addBox(6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(35, 13).addBox(6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(35, 3).addBox(6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.6545F, 0.2182F, 0.0F));

        PartDefinition Wing_r6 = LeftArmWyvern.addOrReplaceChild("Wing_r6", CubeListBuilder.create().texOffs(37, 0).addBox(5.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.6981F, 0.2182F, 0.0F));

        PartDefinition Wing_r7 = LeftArmWyvern.addOrReplaceChild("Wing_r7", CubeListBuilder.create().texOffs(35, -3).addBox(6.0F, -10.75F, 17.0F, 0.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.9599F, 0.2182F, 0.0F));

        PartDefinition Wing_r8 = LeftArmWyvern.addOrReplaceChild("Wing_r8", CubeListBuilder.create().texOffs(36, 11).addBox(5.5F, -13.89F, 7.68F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.3491F, 0.2182F, 0.0F));

        PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 6.0F, 19.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 10.5F, -7.0F));

        PartDefinition Saddle = LowerTorso.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -5.0F, 3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, 15.875F));

        PartDefinition LeftThigh_r2 = LeftLeg2.addOrReplaceChild("LeftThigh_r2", CubeListBuilder.create().texOffs(74, 98).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r2 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r2", CubeListBuilder.create().texOffs(0, 105).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r2 = LeftFoot2.addOrReplaceChild("LeftArch_r2", CubeListBuilder.create().texOffs(112, 53).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(66, 109).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-3.5F, 0.0F, 15.875F));

        PartDefinition RightThigh_r2 = RightLeg2.addOrReplaceChild("RightThigh_r2", CubeListBuilder.create().texOffs(90, 98).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r2 = RightLowerLeg2.addOrReplaceChild("RightCalf_r2", CubeListBuilder.create().texOffs(106, 98).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r2 = RightFoot2.addOrReplaceChild("RightArch_r2", CubeListBuilder.create().texOffs(32, 114).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(106, 108).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg3 = LowerTorso.addOrReplaceChild("LeftLeg3", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, -1.7F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg3.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(34, 103).addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg3 = LeftLeg3.addOrReplaceChild("LeftLowerLeg3", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7848F, 3.7028F));

        PartDefinition LeftLowerLeg_r1 = LeftLowerLeg3.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(104, 74).addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftFoot3 = LeftLowerLeg3.addOrReplaceChild("LeftFoot3", CubeListBuilder.create().texOffs(66, 109).addBox(-1.95F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 5.7152F, -4.3278F));

        PartDefinition RightLeg3 = LowerTorso.addOrReplaceChild("RightLeg3", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -1.7F));

        PartDefinition RightUpperLeg_r1 = RightLeg3.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(50, 104).addBox(-9.5F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(7.75F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg3 = RightLeg3.addOrReplaceChild("RightLowerLeg3", CubeListBuilder.create(), PartPose.offset(0.0F, 5.7848F, 3.7028F));

        PartDefinition RightLowerLeg_r1 = RightLowerLeg3.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(18, 99).addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.25F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightFoot3 = RightLowerLeg3.addOrReplaceChild("RightFoot3", CubeListBuilder.create().texOffs(106, 108).addBox(-2.025F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F,  CubeDeformation.NONE), PartPose.offset(0.25F, 5.7152F, -4.3278F));

        PartDefinition Tail3 = LowerTorso.addOrReplaceChild("Tail3", CubeListBuilder.create(), PartPose.offset(0.0F, -0.25F, 14.0F));

        PartDefinition WolfTail2 = Tail3.addOrReplaceChild("WolfTail2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, -0.1745F, 0.0F, 0.0F));

        PartDefinition TailPrimary6 = WolfTail2.addOrReplaceChild("TailPrimary6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.25F));

        PartDefinition Base_r21 = TailPrimary6.addOrReplaceChild("Base_r21", CubeListBuilder.create().texOffs(108, 85).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.75F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary6 = TailPrimary6.addOrReplaceChild("TailSecondary6", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 3.75F));

        PartDefinition Base_r22 = TailSecondary6.addOrReplaceChild("Base_r22", CubeListBuilder.create().texOffs(84, 79).addBox(-2.5F, -0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary6 = TailSecondary6.addOrReplaceChild("TailTertiary6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 2.5F));

        PartDefinition Base_r23 = TailTertiary6.addOrReplaceChild("Base_r23", CubeListBuilder.create().texOffs(16, 110).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 1.8326F, 0.0F, 0.0F));

        PartDefinition CatTail2 = Tail3.addOrReplaceChild("CatTail2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition TailPrimary7 = CatTail2.addOrReplaceChild("TailPrimary7", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Base_r24 = TailPrimary7.addOrReplaceChild("Base_r24", CubeListBuilder.create().texOffs(84, 109).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

        PartDefinition TailSecondary7 = TailPrimary7.addOrReplaceChild("TailSecondary7", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r25 = TailSecondary7.addOrReplaceChild("Base_r25", CubeListBuilder.create().texOffs(88, 63).addBox(-2.5F, -0.45F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));

        PartDefinition TailTertiary7 = TailSecondary7.addOrReplaceChild("TailTertiary7", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));

        PartDefinition Base_r26 = TailTertiary7.addOrReplaceChild("Base_r26", CubeListBuilder.create().texOffs(48, 79).addBox(-2.5F, 4.55F, -3.3F, 5.0F, 8.0F, 5.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5272F, 0.0F, 0.0F));

        PartDefinition TailQuaternary4 = TailTertiary7.addOrReplaceChild("TailQuaternary4", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.5F));

        PartDefinition Base_r27 = TailQuaternary4.addOrReplaceChild("Base_r27", CubeListBuilder.create().texOffs(0, 115).addBox(-2.0F, 5.5F, -3.8F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.7017F, 0.0F, 0.0F));

        PartDefinition SharkTail2 = Tail3.addOrReplaceChild("SharkTail2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition TailPrimary8 = SharkTail2.addOrReplaceChild("TailPrimary8", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.75F));

        PartDefinition TailFin_r2 = TailPrimary8.addOrReplaceChild("TailFin_r2", CubeListBuilder.create().texOffs(124, 23).addBox(-4.0F, 4.0F, -0.75F, 1.0F, 4.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(28, 118).addBox(-4.0F, 0.0F, 0.25F, 1.0F, 9.0F, 1.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, 1.75F, 1.0F, 1.789F, 0.0F, 0.0F));

        PartDefinition Base_r28 = TailPrimary8.addOrReplaceChild("Base_r28", CubeListBuilder.create().texOffs(108, 94).addBox(-2.0F, -1.075F, 0.375F, 4.0F, 2.0F, 2.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.875F, 0.85F, 1.9199F, 0.0F, 0.0F));

        PartDefinition Base_r29 = TailPrimary8.addOrReplaceChild("Base_r29", CubeListBuilder.create().texOffs(92, 25).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary8 = TailPrimary8.addOrReplaceChild("TailSecondary8", CubeListBuilder.create(), PartPose.offset(0.0F, 3.25F, 7.25F));

        PartDefinition Base_r30 = TailSecondary8.addOrReplaceChild("Base_r30", CubeListBuilder.create().texOffs(110, 115).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.25F, 1.0F, 1.309F, 0.0F, 0.0F));

        PartDefinition TailTertiary8 = TailSecondary8.addOrReplaceChild("TailTertiary8", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 4.5F));

        PartDefinition Base_r31 = TailTertiary8.addOrReplaceChild("Base_r31", CubeListBuilder.create().texOffs(118, 23).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(24, 62).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5F, 4.25F, 1.1345F, 0.0F, 0.0F));

        PartDefinition Base_r32 = TailTertiary8.addOrReplaceChild("Base_r32", CubeListBuilder.create().texOffs(32, 123).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F,  CubeDeformation.NONE)
                .texOffs(54, 115).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 10.0F, 3.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.5F, 4.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r33 = TailTertiary8.addOrReplaceChild("Base_r33", CubeListBuilder.create().texOffs(40, 83).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.25F, 0.25F, 1.4835F, 0.0F, 0.0F));

        PartDefinition DragonTail2 = Tail3.addOrReplaceChild("DragonTail2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition TailPrimary9 = DragonTail2.addOrReplaceChild("TailPrimary9", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition Base_r34 = TailPrimary9.addOrReplaceChild("Base_r34", CubeListBuilder.create().texOffs(110, 45).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition TailSecondary9 = TailPrimary9.addOrReplaceChild("TailSecondary9", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

        PartDefinition Base_r35 = TailSecondary9.addOrReplaceChild("Base_r35", CubeListBuilder.create().texOffs(56, 95).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition TailTertiary9 = TailSecondary9.addOrReplaceChild("TailTertiary9", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

        PartDefinition Base_r36 = TailTertiary9.addOrReplaceChild("Base_r36", CubeListBuilder.create().texOffs(0, 96).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition TailQuaternary5 = TailTertiary9.addOrReplaceChild("TailQuaternary5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

        PartDefinition Base_r37 = TailQuaternary5.addOrReplaceChild("Base_r37", CubeListBuilder.create().texOffs(122, 94).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition TorsoShort = partdefinition.addOrReplaceChild("TorsoShort", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Generic2 = TorsoShort.addOrReplaceChild("Generic2", CubeListBuilder.create().texOffs(64, 56).addBox(-4.0F, -26.0F, -2.0F, 8.0F, 9.0F, 4.0F,  CubeDeformation.NONE), PartPose.offset(0.0F, 26.0F, 0.0F));

        PartDefinition Chiseled2 = TorsoShort.addOrReplaceChild("Chiseled2", CubeListBuilder.create().texOffs(68, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F,  CubeDeformation.NONE)
                .texOffs(68, 36).addBox(-4.0F, 5.5F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Muscles2 = Chiseled2.addOrReplaceChild("Muscles2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r9 = Muscles2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(62, 122).mirror().addBox(0.3F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, -0.0087F, 0.0004F));

        PartDefinition cube_r10 = Muscles2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(62, 122).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, 0.0087F, -0.0004F));

        PartDefinition Abs2 = Muscles2.addOrReplaceChild("Abs2", CubeListBuilder.create(), PartPose.offset(0.0F, 4.3F, 0.2F));

        PartDefinition cube_r11 = Abs2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(36, 123).addBox(-2.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.225F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r12 = Abs2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(74, 95).addBox(0.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(0.05F, 2.45F, -2.225F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r13 = Abs2.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(56, 92).addBox(-3.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.2F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r14 = Abs2.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(56, 92).mirror().addBox(0.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.45F, -2.2F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r15 = Abs2.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(56, 92).addBox(-3.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)), PartPose.offsetAndRotation(-0.05F, 2.35F, -2.45F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r16 = Abs2.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(56, 92).mirror().addBox(0.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.35F, -2.45F, 0.0F, -0.0087F, 0.0F));

        PartDefinition Female2 = TorsoShort.addOrReplaceChild("Female2", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition TorsoLower_r2 = Female2.addOrReplaceChild("TorsoLower_r2", CubeListBuilder.create().texOffs(64, 69).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(0, 78).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F,  CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Plantoids2 = Female2.addOrReplaceChild("Plantoids2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, -2.0F));

        PartDefinition RightPlantoid_r2 = Plantoids2.addOrReplaceChild("RightPlantoid_r2", CubeListBuilder.create().texOffs(120, 70).addBox(-4.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                .texOffs(84, 118).addBox(0.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition Center_r2 = Plantoids2.addOrReplaceChild("Center_r2", CubeListBuilder.create().texOffs(124, 28).addBox(-0.5F, -1.3F, -0.1F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

        PartDefinition Heavy2 = TorsoShort.addOrReplaceChild("Heavy2", CubeListBuilder.create().texOffs(64, 56).addBox(-4.0F, -26.0F, -2.0F, 8.0F, 9.0F, 4.0F,  CubeDeformation.NONE)
                .texOffs(86, 8).addBox(-4.0F, -20.0F, -2.15F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 26.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 160, 160);
    }

    protected void prepareBody(CustomLatexEntity.TorsoType type, CustomLatexEntity.LegType legType) {
        GenericTorso.visible = false;
        ChiseledTorso.visible = false;
        FemaleTorso.visible = false;
        HeavyTorso.visible = false;

        switch (type) {
            case GENERIC -> GenericTorso.visible = true;
            case CHISELED -> ChiseledTorso.visible = true;
            case FEMALE -> FemaleTorso.visible = true;
            case HEAVY -> HeavyTorso.visible = true;
        }

        GenericTorsoShort.visible = GenericTorso.visible;
        ChiseledTorsoShort.visible = ChiseledTorso.visible;
        FemaleTorsoShort.visible = FemaleTorso.visible;
        HeavyTorsoShort.visible = HeavyTorso.visible;

        Torso.visible = false;
        Tail.visible = false;
        TorsoShort.visible = false;
        LowerTorso.visible = false;
        TaurTail.visible = false;

        switch (legType) {
            case BIPEDAL -> {
                Torso.visible = true;
                Tail.visible = true;
            }
            case MERMAID -> {
                TorsoShort.visible = true;
            }
            case CENTAUR -> {
                Torso.visible = true;
                LowerTorso.visible = true;
                TaurTail.visible = true;
            }
        }
    }

    protected void prepareHair(CustomLatexEntity.HairType type) {
        ShortHair.visible = false;
        LongHair.visible = false;

        switch (type) {
            case SHORT -> ShortHair.visible = true;
            case LONG -> LongHair.visible = true;
        }
    }

    protected void prepareEars(CustomLatexEntity.EarType type) {
        WolfEars.visible = false;
        CatEars.visible = false;
        DragonEars.visible = false;
        SharkEars.visible = false;

        switch (type) {
            case WOLF -> WolfEars.visible = true;
            case CAT -> CatEars.visible = true;
            case DRAGON -> DragonEars.visible = true;
            case SHARK -> SharkEars.visible = true;
        }
    }

    protected void prepareTail(CustomLatexEntity.TailType type) {
        WolfTail.visible = false;
        CatTail.visible = false;
        DragonTail.visible = false;
        SharkTail.visible = false;

        switch (type) {
            case WOLF -> WolfTail.visible = true;
            case CAT -> CatTail.visible = true;
            case DRAGON -> DragonTail.visible = true;
            case SHARK -> SharkTail.visible = true;
        }

        WolfTailTaur.visible = WolfTail.visible;
        CatTailTaur.visible = CatTail.visible;
        DragonTailTaur.visible = DragonTail.visible;
        SharkTailTaur.visible = SharkTail.visible;
    }

    protected void prepareLegs(CustomLatexEntity.LegType type) {
        RightLeg.visible = false;
        LeftLeg.visible = false;
        Abdomen.visible = false;
        LowerTorso.visible = false;

        switch (type) {
            case BIPEDAL -> {
                RightLeg.visible = true;
                LeftLeg.visible = true;
                Tail.visible = true;
            }
            case MERMAID -> {
                Abdomen.visible = true;
                Tail.visible = false;
            }
            case CENTAUR -> {
                LowerTorso.visible = true;
            }
        }
    }

    protected void prepareArms(CustomLatexEntity.ArmType type) {
        WyvernRightArm.visible = false;
        WyvernLeftArm.visible = false;
        SharkRightArm.visible = false;
        SharkLeftArm.visible = false;

        switch (type) {
            case WYVERN -> {
                WyvernRightArm.visible = true;
                WyvernLeftArm.visible = true;
            }
            case SHARK -> {
                SharkRightArm.visible = true;
                SharkLeftArm.visible = true;
            }
        }
    }

    public void prepareVisibility(CustomLatexEntity entity) {
        this.prepareBody(entity.getTorsoType(), entity.getLegType());
        this.prepareHair(entity.getHairType());
        this.prepareEars(entity.getEarType());
        this.prepareTail(entity.getTailType());
        this.prepareLegs(entity.getLegType());
        this.prepareArms(entity.getArmType());

        var scaleType = entity.getScaleType();
        this.bodyScale = scaleType.bodyScale;
        this.headScale = scaleType.headScale;
        this.yOffset = Mth.map(this.bodyScale, 1.0F, 1.125F, 0.0F, -2.88F / 16.0F);
    }

    @Override
    public void prepareMobModel(CustomLatexEntity entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.prepareVisibility(entity);
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm leg) {
        return leg == HumanoidArm.LEFT ? (this.LeftLeg.visible ? this.LeftLeg : null) : (this.RightLeg.visible ? this.RightLeg : null);
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Abdomen.visible ? TorsoShort : Torso;
    }

    @Override
    public ModelPart getLowerTorso() {
        return LowerTorso.visible ? LowerTorso : null;
    }

    @Override
    public ModelPart getAbdomen() {
        return Abdomen.visible ? Abdomen : null;
    }

    @Override
    public boolean shouldPartTransfur(ModelPart part) {
        return this.Saddle != part;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        this.scaleForBody(poseStack);
        Abdomen.render(poseStack, buffer, packedLight, packedOverlay, color);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, color);
        TorsoShort.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.swapResetPoseStack(poseStack);
        LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.swapResetPoseStack(poseStack);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.pushPose();
        this.scaleForHead(poseStack);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public HumanoidAnimator<CustomLatexEntity, CustomLatexModel> getAnimator(CustomLatexEntity entity) {
        return animators.get(entity.getLegType()).get(entity.getTailType());
    }

    @Override
    public void scaleForBody(PoseStack poseStack) {
        poseStack.translate(0.0, yOffset, 0.0);
        poseStack.scale(bodyScale, bodyScale, bodyScale);
    }

    @Override
    public void scaleForHead(PoseStack poseStack) {
        poseStack.translate(0.0, yOffset, 0.0);
    }
}