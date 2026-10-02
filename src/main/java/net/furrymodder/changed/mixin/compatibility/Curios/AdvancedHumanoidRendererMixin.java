package net.furrymodder.changed.mixin.compatibility.Curios;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.furrymodder.changed.client.renderer.AdvancedHumanoidRenderer;
import net.furrymodder.changed.client.renderer.model.AdvancedHumanoidModel;
import net.furrymodder.changed.entity.ChangedEntity;
import net.furrymodder.changed.extension.RequiredMods;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import org.spongepowered.asm.mixin.Mixin;
import top.theillusivec4.curios.client.render.CuriosLayer;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
@RequiredMods("curios")
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends MobRenderer<T, M> {
    public AdvancedHumanoidRendererMixin(EntityRendererProvider.Context context, M main, float shadowSize) {
        super(context, main, shadowSize);
    }

    @WrapMethod(method = "addLayers")
    public void curios$addLayers(EntityRendererProvider.Context context, M main, Operation<Void> original) {
        original.call(context, main);

        this.addLayer(new CuriosLayer<>(this));
    }
}
