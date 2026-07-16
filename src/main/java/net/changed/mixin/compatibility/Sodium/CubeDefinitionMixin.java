package net.changed.mixin.compatibility.Sodium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.changed.client.CubeDefinitionExtender;
import net.changed.extension.RequiredMods;
import net.changed.extension.sodium.ModelCuboidExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Field;

@Mixin(CubeDefinition.class)
@RequiredMods("sodium")
public abstract class CubeDefinitionMixin implements CubeDefinitionExtender {
    @Shadow @Final private Vector3f dimensions;
    @Shadow @Final private UVPair texScale;

    @Unique
    private static Field changed$sodiumCuboidField;

    @Unique
    private static ModelCuboid changed$getSodiumCuboid(ModelPart.Cube cube) {
        try {
            if (changed$sodiumCuboidField == null) {
                changed$sodiumCuboidField = cube.getClass().getDeclaredField("sodium$cuboid");
                changed$sodiumCuboidField.setAccessible(true);
            }

            return (ModelCuboid)changed$sodiumCuboidField.get(cube);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
            return null;
        }
    }

    @WrapMethod(method = "bake")
    public ModelPart.Cube bakeWithExtraSodium(int texWidth, int texHeight, Operation<ModelPart.Cube> original) {
        var cube = original.call(texWidth, texHeight);

        var overrideFaceTexOffs = this.getOverrideFaceTexOffs();

        ModelCuboid sodiumCuboid = changed$getSodiumCuboid(cube);
        if (overrideFaceTexOffs != null && sodiumCuboid instanceof ModelCuboidExtender cubeExtender) {
            cubeExtender.overrideFaceTexOffs(overrideFaceTexOffs, texWidth * this.texScale.u(), texHeight * this.texScale.v(),
                    this.dimensions.x, this.dimensions.y, this.dimensions.z);
        }

        return cube;
    }
}
