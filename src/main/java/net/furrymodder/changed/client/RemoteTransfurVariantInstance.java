package net.furrymodder.changed.client;

import net.furrymodder.changed.entity.ChangedEntity;
import net.furrymodder.changed.entity.variant.TransfurVariant;
import net.minecraft.client.player.RemotePlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemoteTransfurVariantInstance<T extends ChangedEntity> extends ClientTransfurVariantInstance<T> {
    private final RemotePlayer host;

    public RemoteTransfurVariantInstance(TransfurVariant<T> parent, RemotePlayer host) {
        super(parent, host);
        this.host = host;
    }
}
