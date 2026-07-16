package net.changed.client;

import net.changed.entity.ChangedEntity;
import net.changed.entity.variant.TransfurVariant;
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
