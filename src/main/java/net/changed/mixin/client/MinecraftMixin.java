package net.changed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.changed.block.AbstractLatexBlock;
import net.changed.block.AlertingPuddle;
import net.changed.block.StasisChamber;
import net.changed.entity.latex.LatexType;
import net.changed.init.ChangedLatexTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public Entity cameraEntity;
    @Shadow @Nullable public Screen screen;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow public abstract void setScreen(@Nullable Screen p_91153_);

    @Inject(method = "tick", at = @At("HEAD"))
    public void closeBedScreenForStasis(CallbackInfo ci) {
        if (this.screen instanceof InBedChatScreen && this.player != null && StasisChamber.isEntityStabilized(this.player))
            this.setScreen(null);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSleeping()Z"))
    public boolean doNotOpenBedScreenForStasis(LocalPlayer player, Operation<Boolean> original) {
        if (StasisChamber.isEntityStabilized(player))
            return false;
        return original.call(player);
    }

    @WrapMethod(method = "shouldEntityAppearGlowing")
    public boolean isEntityMovingOnWhiteLatex(Entity entity, Operation<Boolean> original) {
        if (!(entity instanceof LivingEntity livingEntity))
            return original.call(entity);
        if (this.cameraEntity == null)
            return original.call(entity);
        if (LatexType.getEntityLatexType(this.cameraEntity) != ChangedLatexTypes.WHITE_LATEX.get())
            return original.call(entity);
        if (LatexType.getEntityLatexType(livingEntity) == ChangedLatexTypes.WHITE_LATEX.get())
            return original.call(entity);

        BlockState standing = livingEntity.level().getBlockState(livingEntity.blockPosition().below());
        if (standing == null || standing.isAir())
            return original.call(entity);
        if (AbstractLatexBlock.isSurfaceOfType(entity.level(), livingEntity.blockPosition(), Direction.DOWN, ChangedLatexTypes.WHITE_LATEX.get()))
            return true;
        return original.call(entity);
    }

    @WrapMethod(method = "shouldEntityAppearGlowing")
    public boolean isEntityMovingOnAlertPuddle(Entity entity, Operation<Boolean> original) {
        if (!(entity instanceof LivingEntity livingEntity))
            return original.call(entity);
        if (this.cameraEntity == null)
            return original.call(entity);

        BlockState standing = livingEntity.level().getBlockState(livingEntity.blockPosition());
        if (standing.getBlock() instanceof AlertingPuddle alertingPuddle && alertingPuddle.shouldGlowLocally(livingEntity, this.cameraEntity))
            return true;
        standing = livingEntity.level().getBlockState(livingEntity.blockPosition().below());
        if (standing.getBlock() instanceof AlertingPuddle alertingPuddle && alertingPuddle.shouldGlowLocally(livingEntity, this.cameraEntity))
            return true;
        return original.call(entity);
    }
}
