package net.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.client.renderer.animate.AnimatorPresets;
import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.entity.beast.WhiteWolfMale;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

import java.util.List;

public class WhiteWolfMaleModel extends AdvancedHumanoidModel<WhiteWolfMale> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final HumanoidAnimator<WhiteWolfMale, WhiteWolfMaleModel> animator;

    public WhiteWolfMaleModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.wolfLike(
                        Head, Head.getChild("LeftEar"), Head.getChild("RightEar"),
                        Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
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
    public HumanoidAnimator<WhiteWolfMale, WhiteWolfMaleModel> getAnimator(WhiteWolfMale entity) {
        return animator;
    }
}
