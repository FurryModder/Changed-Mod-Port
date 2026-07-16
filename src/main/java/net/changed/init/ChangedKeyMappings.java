package net.changed.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.changed.Changed;
import net.changed.ability.GrabEntityAbility;
import net.changed.network.ExtraJumpKeybind;
import net.changed.network.VariantAbilityActivate;
import net.changed.process.ProcessTransfur;
import net.changed.tutorial.ChangedTutorial;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ChangedKeyMappings {
    public static final KeyMapping SELECT_ABILITY = new KeyMapping("key.changed.variant_ability",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R, "key.categories.ui") {
        @Override
        public void setDown(boolean newState) {
            super.setDown(newState);
            if (!newState) return;

            LocalPlayer local = Minecraft.getInstance().player;
            GrabEntityAbility.getGrabberSafe(local).ifPresent(entity -> {
                if (entity.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                        .map(ability -> ability.grabbedHasControl).orElse(false))
                    Changed.PACKET_HANDLER.sendToServer(VariantAbilityActivate.openRadial(local));
            });

            ProcessTransfur.ifPlayerTransfurred(local, variant -> {
                if (variant.isTemporaryFromSuit())
                    return;

                Changed.PACKET_HANDLER.sendToServer(VariantAbilityActivate.openRadial(local));
                ChangedTutorial.triggerOnOpenRadial();
            });
        }
    };
    public static final KeyMapping USE_ABILITY = new KeyMapping("key.changed.use_ability",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z, "key.categories.movement") {
        @Override
        public void setDown(boolean newState) {
            super.setDown(newState);
            LocalPlayer local = Minecraft.getInstance().player;
            ProcessTransfur.ifPlayerTransfurred(local, variant -> {
                assert local != null;
                if (variant.isTemporaryFromSuit())
                    return;

                // KeyStateTracker will check if the state has changed
                if (variant.abilityKey.queueKeyState(newState)) {
                    ChangedTutorial.triggerOnUseAbility(variant.getSelectedAbility());
                    Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(local, newState, variant.selectedAbility));
                }
            });
        }
    };

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(SELECT_ABILITY);
        event.register(USE_ABILITY);
    }

    @EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            LocalPlayer local = Minecraft.getInstance().player;
            Options options = Minecraft.getInstance().options;
            if (local == null)
                return;

            if (Minecraft.getInstance().screen == null) {
                if (event.getKey() == options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                    if (!local.onGround())
                        ProcessTransfur.ifPlayerTransfurred(local, variant -> {
                            if (!variant.getParent().canDoubleJump())
                                return;
                            if (variant.getJumpCharges() > 0) {
                                variant.decJumpCharges();
                                local.jumpFromGround();
                                Changed.PACKET_HANDLER.sendToServer(new ExtraJumpKeybind());
                            }
                        });
                }
            }
        }
    }
}
