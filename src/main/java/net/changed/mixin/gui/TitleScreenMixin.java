package net.changed.mixin.gui;

import net.changed.client.gui.BpiButton;
import net.changed.client.gui.BasicPlayerInfoScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initChangedBPI(CallbackInfo callback) {
        int buttonX = this.width / 2 + 128;
        int buttonY = this.height / 4 + 138;

        for (var renderable : this.renderables) {
            if (renderable instanceof AbstractWidget widget &&
                    widget.getWidth() == 20 &&
                    widget.getHeight() == 20 &&
                    widget.getX() >= this.width / 2 + 100) {
                buttonX = widget.getX() + 24;
                buttonY = widget.getY();
                break;
            }
        }

        this.addRenderableWidget(new BpiButton(buttonX, buttonY, button -> {
            this.minecraft.setScreen(new BasicPlayerInfoScreen(this));
        }));
    }
}
