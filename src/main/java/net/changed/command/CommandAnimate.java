package net.changed.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.changed.Changed;
import net.changed.entity.Emote;
import net.changed.entity.animation.AnimationEvent;
import net.changed.init.ChangedAnimationEvents;
import net.changed.init.ChangedRegistry;
import net.changed.process.ProcessEmote;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Locale;

@EventBusSubscriber
public class CommandAnimate {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ANIMATIONS = SuggestionProviders.register(Changed.modResource("animations"), (p_121667_, p_121668_) -> {
        return SharedSuggestionProvider.suggestResource(ChangedRegistry.ANIMATION_EVENTS.get().getKeys().stream(), p_121668_);
    });

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("animate").requires(p -> p.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity())
                        .then(Commands.argument("animate", ResourceLocationArgument.id()).suggests(SUGGEST_ANIMATIONS)
                                .executes(context -> doAnimate(context.getSource(), EntityArgument.getEntity(context, "entity"), ChangedRegistry.ANIMATION_EVENTS.get().getValue(ResourceLocationArgument.getId(context, "animate"))))
                ))
        );
    }

    private static int doAnimate(CommandSourceStack source, Entity entity, AnimationEvent<?> event) throws CommandSyntaxException {
        if (entity instanceof LivingEntity livingEntity)
            ChangedAnimationEvents.broadcastEntityAnimation(livingEntity, event, null);
        return Command.SINGLE_SUCCESS;
    }
}
