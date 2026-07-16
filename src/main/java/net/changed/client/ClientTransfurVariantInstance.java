package net.changed.client;

import net.changed.entity.ChangedEntity;
import net.changed.entity.variant.TransfurVariant;
import net.changed.entity.variant.TransfurVariantInstance;
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
