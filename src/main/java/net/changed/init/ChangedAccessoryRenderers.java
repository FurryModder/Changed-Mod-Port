package net.changed.init;

import net.changed.client.renderer.accessory.*;
import net.changed.client.renderer.layers.AccessoryLayer;
import net.changed.client.renderer.model.ExoskeletonModel;
import net.changed.client.renderer.model.armor.ArmorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Set;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedAccessoryRenderers {
    @SubscribeEvent
    public static void registerAccessoryRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var modelSet = Minecraft.getInstance().getEntityModels();

            AccessoryLayer.registerRenderer(ChangedItems.BENIGN_SHORTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.PINK_SHORTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.BLACK_PANTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.NAVY_PANTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.SPORTS_BRA.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.BLACK_TSHIRT.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.WHITE_TSHIRT.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.TSC_VEST.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.LAB_COAT.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_OUTER, Set.of(
                    new SimpleClothingRenderer.ModelComponent(ArmorModel.CLOTHING_OUTER, EquipmentSlot.CHEST),
                    new SimpleClothingRenderer.ModelComponent(ArmorModel.CLOTHING_MIDDLE, EquipmentSlot.LEGS)
            )));
            AccessoryLayer.registerRenderer(ChangedItems.WETSUIT.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, Set.of(
                    new SimpleClothingRenderer.ModelComponent(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST),
                    new SimpleClothingRenderer.ModelComponent(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS)
            )));
            AccessoryLayer.registerRenderer(ChangedItems.NITRILE_GLOVES.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_OUTER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.ORANGE_NECK_TIE.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.RED_NECK_TIE.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.BLUE_NECK_TIE.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.DOG_COLLAR.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.FACE_MASK.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.HEAD));

            AccessoryLayer.registerRenderer(ChangedItems.EXOSKELETON.get(), () ->
                    new WornExoskeletonRenderer(modelSet, ExoskeletonModel.LAYER_LOCATION_SUIT, ExoskeletonModel.LAYER_LOCATION_VISOR, ExoskeletonModel.LAYER_LOCATION_HUMAN));

            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.BENIGN_SHORTS.get(), ChangedItems.PINK_SHORTS.get(), ChangedItems.SPORTS_BRA.get(),
                            ChangedItems.BLACK_PANTS.get(), ChangedItems.NAVY_PANTS.get()),
                    AccessoryLayer.RenderOrder.BELOW, ChangedItems.BLACK_TSHIRT.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.WHITE_TSHIRT.get(), ChangedItems.WETSUIT.get()),
                    AccessoryLayer.RenderOrder.SAME, ChangedItems.BLACK_TSHIRT.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.ORANGE_NECK_TIE.get(), ChangedItems.RED_NECK_TIE.get(), ChangedItems.BLUE_NECK_TIE.get(), ChangedItems.DOG_COLLAR.get(),
                            ChangedItems.FACE_MASK.get(), ChangedItems.TSC_VEST.get()),
                    AccessoryLayer.RenderOrder.ABOVE, ChangedItems.BLACK_TSHIRT.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.LAB_COAT.get()),
                    AccessoryLayer.RenderOrder.ABOVE, ChangedItems.ORANGE_NECK_TIE.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.NITRILE_GLOVES.get()),
                    AccessoryLayer.RenderOrder.ABOVE, ChangedItems.LAB_COAT.get());
        });
    }
}
