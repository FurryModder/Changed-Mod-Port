package net.changed.mixin.compatibility.Sodium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer;
import net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.changed.extension.RequiredMods;
import net.changed.extension.sodium.ModelCuboidExtender;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityRenderer.class, remap = false)
@RequiredMods("sodium")
public abstract class EntityRendererMixin {
    @Shadow @Final private static Vector2f[][] VERTEX_TEXTURES;
    @Shadow private static void buildVertexTexCoord(Vector2f[] uvs, float u1, float v1, float u2, float v2) {}

    @WrapMethod(method = "prepareVertices(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/caffeinemc/mods/sodium/client/render/immediate/model/ModelCuboid;)V")
    private static void applyCubeModifications(PoseStack.Pose matrices, ModelCuboid cuboid, Operation<Void> original) {
        original.call(matrices, cuboid);

        var overrides = ((ModelCuboidExtender)cuboid).getOverrideFaceTexOffs();
        if (overrides != null) {
            for (var entry : overrides.entrySet()) {
                var faceDir = entry.getKey();
                var uvSet = entry.getValue();
                buildVertexTexCoord(VERTEX_TEXTURES[ModelCuboid.getFaceIndex(faceDir)],
                        uvSet.u0(), uvSet.v0(), uvSet.u1(), uvSet.v1());
            }
        }
    }
}
