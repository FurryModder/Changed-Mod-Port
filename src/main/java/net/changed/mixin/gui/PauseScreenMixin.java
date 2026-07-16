package net.changed.mixin.gui;

import net.changed.client.gui.BpiButton;
import net.changed.client.gui.BasicPlayerInfoScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "createPauseMenu", at = @At("RETURN"))
    public void addBPIButton(CallbackInfo callback) {
        int buttonX = this.width / 2 - 126;
        int buttonY = this.height / 4 + 80;
        String optionsText = Component.translatable("menu.options").getString();

        for (var renderable : this.renderables) {
            if (renderable instanceof Button button && button.getMessage().getString().equals(optionsText)) {
                buttonX = button.getX() - 24;
                buttonY = button.getY();
                break;
            }
        }

        this.addRenderableWidget(new BpiButton(buttonX, buttonY, button -> {
            this.minecraft.setScreen(new BasicPlayerInfoScreen(this, this.minecraft.player));
        }));
    }
}
