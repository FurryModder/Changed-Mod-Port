package net.changed.item;

import net.changed.data.AccessorySlotContext;
import net.changed.init.ChangedSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.changed.compat.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LabCoatItem extends ClothingItem {
    public LabCoatItem() {
        this.registerDefaultState(this.stateDefinition.any().setValue(CLOSED, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> builder, TooltipFlag tooltipFlag) {
        if (Minecraft.getInstance().level != null) {
            this.addInteractInstructions(builder::add);
        }

        super.appendHoverText(stack, context, builder, tooltipFlag);
    }

    @Override
    protected void createClothingStateDefinition(StateDefinition.Builder<ClothingItem, ClothingState> builder) {
        super.createClothingStateDefinition(builder);
        builder.add(CLOSED);
    }

    @Override
    public void accessoryInteract(AccessorySlotContext<?> slotContext) {
        super.accessoryInteract(slotContext);
        this.setClothingState(slotContext.stack(), this.getClothingState(slotContext.stack()).cycle(CLOSED));
        SoundEvent changeSound = this.getEquipSound(slotContext.stack());
        if (changeSound != null)
            slotContext.wearer().playSound(changeSound, 1F, 1F);
    }

    @Nullable
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (this.getClothingState(stack).getValue(CLOSED))
            return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "textures/models/%s_closed.png".formatted(itemId.getPath()));
        else
            return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "textures/models/%s.png".formatted(itemId.getPath()));
    }

    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.COAT_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.COAT_BREAK.get();
    }
}
