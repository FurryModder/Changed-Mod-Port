package net.changed.process;

import net.changed.Changed;
import net.changed.entity.Emote;
import net.changed.init.ChangedParticles;
import net.changed.network.packet.EmotePacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.changed.network.PacketDistributor;

public class ProcessEmote {
    private static void rawEmote(LivingEntity entity, Emote emote) {
        entity.level().addParticle(ChangedParticles.emote(entity, emote), entity.getX(), entity.getY() + entity.getDimensions(entity.getPose()).height() + 0.65, entity.getZ(),
                0, 0, 0);
    }

    public static void playerEmote(Player player, Emote emote) {
        if (!player.level().isClientSide) {
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new EmotePacket(player.getId(), emote));
        } else {
            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                ProcessEmote.rawEmote(variant.getChangedEntity(), emote);
            }, () -> {
                ProcessEmote.rawEmote(player, emote);
            });
        }
    }
}
