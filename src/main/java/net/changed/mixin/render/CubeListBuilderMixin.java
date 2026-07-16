package net.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.changed.client.CubeDefinitionExtender;
import net.changed.client.CubeListBuilderExtender;
import net.changed.client.Triangle;
import net.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(CubeListBuilder.class)
public abstract class CubeListBuilderMixin implements CubeListBuilderExtender {
    @Shadow @Final private List<CubeDefinition> cubes;

    @Unique private List<Triangle.Definition> triangles = null;

    @Unique private boolean lastCubeWasHidden = false;

    @Override
    public CubeListBuilderExtender removeLastFaces(Direction... directions) {
        if (this.lastCubeWasHidden)
            return this;

        CubeDefinitionExtender cube = (CubeDefinitionExtender)(Object)this.cubes.get(this.cubes.size() - 1);
        cube.removeFaces(directions);
        return this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public CubeListBuilderExtender copyLastFaceUVStart(Direction from, Direction to) {
        if (this.lastCubeWasHidden)
            return this;

        CubeDefinitionExtender cube = (CubeDefinitionExtender)(Object)this.cubes.get(this.cubes.size() - 1);
        cube.copyFaceUVStart(from, to);
        return this;
    }

    @Override
    public CubeListBuilderExtender overrideLastFaceTexOffs(Direction face, int xOffset, int yOffset) {
        if (this.lastCubeWasHidden)
            return this;

        CubeDefinitionExtender cube = (CubeDefinitionExtender)(Object)this.cubes.get(this.cubes.size() - 1);
        cube.overrideFaceTexOffs(face, xOffset, yOffset);
        return this;
    }

    @Override
    public CubeListBuilder finish() {
        return (CubeListBuilder)(Object)this;
    }

    @Override
    public void addTriangle(String comment, Vector3f p1, UVPair uv1, Vector3f p2, UVPair uv2, Vector3f p3, UVPair uv3) {
        if (triangles == null)
            triangles = new ArrayList<>();
        triangles.add(new Triangle.Definition(comment, p1, p2, p3, uv1, uv2, uv3));
    }

    @Nullable
    @Override
    public List<Triangle.Definition> getTriangles() {
        return triangles;
    }

    @WrapOperation(method = "addBox*", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public boolean markLastCubeAsPresent(List<CubeDefinition> instance, Object cubeDefinition, Operation<Boolean> original) {
        this.lastCubeWasHidden = false;
        return original.call(instance, cubeDefinition);
    }

    @WrapMethod(method = "addBox(Ljava/lang/String;FFFIIILnet/minecraft/client/model/geom/builders/CubeDeformation;II)Lnet/minecraft/client/model/geom/builders/CubeListBuilder;")
    public CubeListBuilder skipHiddenCube(String comment, float x, float y, float z, int width, int height, int depth, CubeDeformation deformation, int texU, int texV, Operation<CubeListBuilder> original) {
        if (deformation == LatexHumanoidArmorModel.HIDDEN_CUBE) {
            this.texOffs(texU, texV);
            this.lastCubeWasHidden = true;
            return (CubeListBuilder)(Object)this;
        }

        return original.call(comment, x, y, z, width, height, depth, deformation, texU, texV);
    }

    @WrapMethod(method = "addBox(Ljava/lang/String;FFFFFFLnet/minecraft/client/model/geom/builders/CubeDeformation;)Lnet/minecraft/client/model/geom/builders/CubeListBuilder;")
    public CubeListBuilder skipHiddenCube(String comment, float x, float y, float z, float width, float height, float depth, CubeDeformation deformation, Operation<CubeListBuilder> original) {
        if (deformation == LatexHumanoidArmorModel.HIDDEN_CUBE) {
            this.lastCubeWasHidden = true;
            return (CubeListBuilder)(Object)this;
        }

        return original.call(comment, x, y, z, width, height, depth, deformation);
    }

    @WrapMethod(method = "addBox(FFFFFFLnet/minecraft/client/model/geom/builders/CubeDeformation;FF)Lnet/minecraft/client/model/geom/builders/CubeListBuilder;")
    public CubeListBuilder skipHiddenCube(float x, float y, float z, float width, float height, float depth, CubeDeformation deformation, float texScaleU, float texScaleV, Operation<CubeListBuilder> original) {
        if (deformation == LatexHumanoidArmorModel.HIDDEN_CUBE) {
            this.lastCubeWasHidden = true;
            return (CubeListBuilder)(Object)this;
        }

        return original.call(x, y, z, width, height, depth, deformation, texScaleU, texScaleV);
    }

    @WrapMethod(method = "addBox(FFFFFFLnet/minecraft/client/model/geom/builders/CubeDeformation;)Lnet/minecraft/client/model/geom/builders/CubeListBuilder;")
    public CubeListBuilder skipHiddenCube(float x, float y, float z, float width, float height, float depth, CubeDeformation deformation, Operation<CubeListBuilder> original) {
        if (deformation == LatexHumanoidArmorModel.HIDDEN_CUBE) {
            this.lastCubeWasHidden = true;
            return (CubeListBuilder)(Object)this;
        }

        return original.call(x, y, z, width, height, depth, deformation);
    }

    @Override
    public CubeListBuilderExtender texOffs(int xTexOffs, int yTexOffs) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).texOffs(xTexOffs, yTexOffs);
    }

    @Override
    public CubeListBuilderExtender mirror() {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).mirror();
    }

    @Override
    public CubeListBuilderExtender mirror(boolean mirror) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).mirror(mirror);
    }

    @Override
    public CubeListBuilderExtender addBox(String comment, float x, float y, float z, int width, int height, int depth, CubeDeformation deformation, int xTexOffs, int yTexOffs) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(comment, x, y, z, width, height, depth, deformation, xTexOffs, yTexOffs);
    }

    @Override
    public CubeListBuilderExtender addBox(String comment, float x, float y, float z, int width, int height, int depth, int xTexOffs, int yTexOffs) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(comment, x, y, z, width, height, depth, xTexOffs, yTexOffs);
    }

    @Override
    public CubeListBuilderExtender addBox(float x, float y, float z, float width, float height, float depth) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(x, y, z, width, height, depth);
    }

    @Override
    public CubeListBuilderExtender addBox(float x, float y, float z, float width, float height, float depth, Set<Direction> visibleFaces) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(x, y, z, width, height, depth, visibleFaces);
    }

    @Override
    public CubeListBuilderExtender addBox(String comment, float x, float y, float z, float width, float height, float depth) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(comment, x, y, z, width, height, depth);
    }

    @Override
    public CubeListBuilderExtender addBox(String comment, float x, float y, float z, float width, float height, float depth, CubeDeformation deformation) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(comment, x, y, z, width, height, depth, deformation);
    }

    @Override
    public CubeListBuilderExtender addBox(float x, float y, float z, float width, float height, float depth, boolean mirror) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(x, y, z, width, height, depth, mirror);
    }

    @Override
    public CubeListBuilderExtender addBox(float x, float y, float z, float width, float height, float depth, CubeDeformation deformation, float texScaleX, float texScaleY) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(x, y, z, width, height, depth, deformation, texScaleX, texScaleY);
    }

    @Override
    public CubeListBuilderExtender addBox(float x, float y, float z, float width, float height, float depth, CubeDeformation deformation) {
        return (CubeListBuilderExtender)((CubeListBuilder)(Object)this).addBox(x, y, z, width, height, depth, deformation);
    }
}
