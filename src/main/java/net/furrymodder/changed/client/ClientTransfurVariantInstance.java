package net.furrymodder.changed.client;

import net.furrymodder.changed.entity.ChangedEntity;
import net.furrymodder.changed.entity.variant.TransfurVariant;
import net.furrymodder.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTransfurVariantInstance<T extends ChangedEntity> extends TransfurVariantInstance<T> {
    private final AbstractClientPlayer host;

    public ClientTransfurVariantInstance(TransfurVariant<T> parent, AbstractClientPlayer host) {
        super(parent, host);
        this.host = host;
    }
}
