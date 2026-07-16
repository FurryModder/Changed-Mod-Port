package net.changed.mixin.compatibility.Sodium;

import com.mojang.blaze3d.platform.GlStateManager;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat3v;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformInt;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.DefaultShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import net.changed.client.ChangedClient;
import net.changed.client.WaveVisionRenderer;
import net.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultShaderInterface.class, remap = false)
@RequiredMods("sodium")
public abstract class DefaultShaderInterfaceMixin {
    @Unique @Nullable private GlUniformFloat changed$waveVision;
    @Unique @Nullable private GlUniformFloat changed$waveEffect;
    @Unique @Nullable private GlUniformInt changed$waveResonanceTexture;
    @Unique @Nullable private GlUniformFloat3v changed$waveResonance;
    @Unique private static final int CHANGED_WAVE_RESONANCE_TEXTURE_UNIT = 2;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void changed$bindWaveVisionUniforms(ShaderBindingContext context, ChunkShaderOptions options, CallbackInfo ci) {
        this.changed$waveVision = context.bindUniformOptional("u_ChangedWaveVision", GlUniformFloat::new);
        this.changed$waveEffect = context.bindUniformOptional("u_ChangedWaveEffect", GlUniformFloat::new);
        this.changed$waveResonanceTexture = context.bindUniformOptional("u_ChangedWaveResonanceTex", GlUniformInt::new);
        this.changed$waveResonance = context.bindUniformOptional("u_ChangedWaveResonance", GlUniformFloat3v::new);
    }

    @Inject(method = "setupState", at = @At("TAIL"))
    private void changed$setupWaveVisionUniforms(CallbackInfo ci) {
        if (this.changed$waveVision != null) {
            this.changed$waveVision.setFloat(ChangedClient.isRenderingWaveVision() ? 1.0f : 0.0f);
        }

        if (this.changed$waveEffect != null) {
            this.changed$waveEffect.setFloat(ChangedClient.getWaveEffect());
        }

        if (this.changed$waveResonanceTexture != null) {
            int textureId = Minecraft.getInstance().getTextureManager()
                    .getTexture(WaveVisionRenderer.WAVE_RESONANCE_BLOCK_MASK_STRICT)
                    .getId();
            GlStateManager._activeTexture(33984 + CHANGED_WAVE_RESONANCE_TEXTURE_UNIT);
            GlStateManager._bindTexture(textureId);
            this.changed$waveResonanceTexture.setInt(CHANGED_WAVE_RESONANCE_TEXTURE_UNIT);
            GlStateManager._activeTexture(33984);
        }

        if (this.changed$waveResonance != null) {
            Vector3f resonance = ChangedClient.getWaveResonance();
            this.changed$waveResonance.set(resonance.x, resonance.y, resonance.z);
        }
    }
}
