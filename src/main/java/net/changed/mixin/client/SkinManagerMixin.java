package net.changed.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.changed.client.SkinManagerExtender;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Executor;

@Mixin(SkinManager.class)
public abstract class SkinManagerMixin implements SkinManagerExtender {
    @Unique
    private Path changed$skinsDirectory;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void captureSkinsDirectory(TextureManager textureManager, Path root, MinecraftSessionService sessionService, Executor executor, CallbackInfo callback) {
        this.changed$skinsDirectory = root;
    }

    public Optional<NativeImage> getSkinImage(ResourceLocation name) {
        if (!name.getNamespace().equals("minecraft"))
            return Optional.empty();
        if (!name.getPath().startsWith("skins/"))
            return Optional.empty();
        if (this.changed$skinsDirectory == null)
            return Optional.empty();
        String hashString = name.getPath().substring(6);
        Path imageFile = this.changed$skinsDirectory.resolve(hashString.length() > 2 ? hashString.substring(0, 2) : "xx").resolve(hashString);

        InputStream content = null;
        NativeImage image = null;
        try {
            content = Files.newInputStream(imageFile);
            image = NativeImage.read(content);
        } catch (Exception ignored) {}
        finally {
            try {
                if (content != null)
                    content.close();
            } catch (Exception ignored) {}
        }

        return Optional.ofNullable(image);
    }
}
