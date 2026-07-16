package net.changed.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.changed.Changed;
import net.changed.entity.Emote;
import net.changed.process.ProcessEmote;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.Locale;

@EventBusSubscriber
public class CommandEmote {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_EMOTE = SuggestionProviders.register(Changed.modResource("emotes"), (p_121667_, p_121668_) -> {
        var list = List.of(Emote.values());
        return SharedSuggestionProvider.suggest(list.stream().map(emote -> emote.toString().toLowerCase(Locale.ROOT)), p_121668_);
    });

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("emote")
                .then(Commands.argument("emote", StringArgumentType.string()).suggests(SUGGEST_EMOTE)
                        .executes(context -> doEmote(context.getSource(), Emote.valueOf(StringArgumentType.getString(context, "emote").toUpperCase(Locale.ROOT))))
                ));
    }

    private static int doEmote(CommandSourceStack source, Emote emote) throws CommandSyntaxException {
        ProcessEmote.playerEmote(source.getPlayerOrException(), emote);
        return Command.SINGLE_SUCCESS;
    }
}
