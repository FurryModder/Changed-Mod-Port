package net.changed.mixin.compatibility.Sodium;

import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import net.changed.extension.RequiredMods;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShaderLoader.class, remap = false)
@RequiredMods("sodium")
public abstract class ShaderLoaderMixin {
    @Inject(method = "getShaderSource", at = @At("RETURN"), cancellable = true)
    private static void changed$injectWaveVisionShader(ResourceLocation name, CallbackInfoReturnable<String> cir) {
        if (!name.getNamespace().equals("sodium") || !name.getPath().equals("blocks/block_layer_opaque.fsh")) {
            return;
        }

        cir.setReturnValue(changed$patchBlockLayerFragmentShader(cir.getReturnValue()));
    }

    @Unique
    private static String changed$patchBlockLayerFragmentShader(String source) {
        if (source.contains("u_ChangedWaveResonanceTex")) {
            return source;
        }

        source = source.replace(
                "uniform float u_FogEnd; // The ending position of the shader fog\n",
                "uniform float u_FogEnd; // The ending position of the shader fog\n" +
                        "uniform float u_ChangedWaveVision;\n" +
                        "uniform float u_ChangedWaveEffect;\n" +
                        "uniform sampler2D u_ChangedWaveResonanceTex;\n" +
                        "uniform vec3 u_ChangedWaveResonance;\n");

        source = source.replace(
                "out vec4 fragColor; // The output fragment for the color framebuffer\n",
                "out vec4 fragColor; // The output fragment for the color framebuffer\n" +
                        "\n" +
                        "float changed_colorIntensity(vec3 color) {\n" +
                        "    return dot(color.rgb, vec3(0.2989, 0.5870, 0.1140));\n" +
                        "}\n" +
                        "\n" +
                        "vec4 changed_waveColoring(vec4 colorIn) {\n" +
                        "    float intensity = changed_colorIntensity(colorIn.rgb);\n" +
                        "    return mix(vec4(vec3(intensity), colorIn.a), colorIn, 0.2);\n" +
                        "}\n" +
                        "\n" +
                        "vec4 changed_resonanceColoring(vec4 colorIn) {\n" +
                        "    float intensity = changed_colorIntensity(colorIn.rgb);\n" +
                        "    return mix(vec4(vec3(intensity), colorIn.a), colorIn, 1.2);\n" +
                        "}\n" +
                        "\n" +
                        "float changed_waveStrength(float effectValue, float depth, float decay, float interval, float speed) {\n" +
                        "    float time = (depth / interval) - (effectValue / speed);\n" +
                        "    float waved = (sin(mod(time, 3.141592 * 0.5)) * decay) - decay + 1.0;\n" +
                        "    if (waved < 0.9995) waved *= 0.6;\n" +
                        "    if (time > 0.0) waved = 0.0;\n" +
                        "    return clamp(waved, 0.0, 1.0);\n" +
                        "}\n" +
                        "\n" +
                        "vec4 changed_waveVision(vec4 colorIn, float effectValue, float depth, vec3 resonanceColor) {\n" +
                        "    float stableEffectValue = max(effectValue, 16.0);\n" +
                        "    vec4 waveColor = changed_waveColoring(colorIn);\n" +
                        "    vec4 resoColor = changed_resonanceColoring(colorIn);\n" +
                        "    float baseVisibility = 0.08 * (1.0 - smoothstep(48.0, 96.0, depth));\n" +
                        "    float waveFast = max(changed_waveStrength(stableEffectValue, depth, 0.995, 60.0, 20.0), baseVisibility);\n" +
                        "    float waveRecv = changed_waveStrength(stableEffectValue, depth, 0.7, 60.0, 20.0) * changed_colorIntensity(resonanceColor) * 2.0;\n" +
                        "    return vec4(mix(waveFast * waveColor.rgb, mix(resoColor.rgb, resonanceColor, 0.35), waveRecv), 1.0);\n" +
                        "}\n" +
                        "\n" +
                        "vec4 changed_waveVision(vec4 colorIn, float effectValue, float depth) {\n" +
                        "    return changed_waveVision(colorIn, effectValue, depth, vec3(0.0));\n" +
                        "}\n");

        return source.replace(
                "    fragColor = _linearFog(diffuseColor, v_FragDistance, u_FogColor, u_FogStart, u_FogEnd);\n",
                "    if (u_ChangedWaveVision > 0.5) {\n" +
                        "#ifdef USE_FOG\n" +
                        "        float changedDepth = max(v_FragDistance, 0.0);\n" +
                        "#else\n" +
                        "        float changedDepth = 0.0;\n" +
                        "#endif\n" +
                        "        vec3 changedResonanceMask = texture(u_ChangedWaveResonanceTex, v_TexCoord).rgb;\n" +
                        "        diffuseColor = changed_waveVision(diffuseColor, u_ChangedWaveEffect, changedDepth, u_ChangedWaveResonance * changedResonanceMask);\n" +
                        "    }\n" +
                        "\n" +
                        "    fragColor = _linearFog(diffuseColor, v_FragDistance, u_FogColor, u_FogStart, u_FogEnd);\n");
    }
}
