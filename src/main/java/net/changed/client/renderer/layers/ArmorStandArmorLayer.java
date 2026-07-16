package net.changed.client.renderer.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.changed.client.renderer.model.ArmorStandArmorModel;
import net.changed.client.renderer.model.armor.ArmorModelPicker;
import net.changed.entity.decoration.AbstractArmorStand;
import net.changed.extension.ChangedCompatibility;
import net.changed.world.enchantments.FormFittingEnchantment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ArmorStandArmorLayer<T extends AbstractArmorStand, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    final LivingEntityRenderer<T, M> parent;
    public final ArmorModelPicker<T, ArmorStandArmorModel<T>> modelPicker;
    private final TextureAtlas armorTrimAtlas;

    public ArmorStandArmorLayer(LivingEntityRenderer<T, M> parentModel, ArmorModelPicker<T, ArmorStandArmorModel<T>> modelPicker, ModelManager modelManager) {
        super(parentModel);
        this.parent = parentModel;
        this.modelPicker = modelPicker;
        this.armorTrimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    public void render(PoseStack pose, MultiBufferSource buffers, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        this.modelPicker.prepareAndSetupModels(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        boolean firstPerson = ChangedCompatibility.isFirstPersonRendering();

        if (!firstPerson || !entity.isVisuallySwimming()) // Don't render chest-plate if swimming in first person
            this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.CHEST, packedLight, this.getArmorModel(entity, EquipmentSlot.CHEST));
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.LEGS, packedLight, this.getArmorModel(entity, EquipmentSlot.LEGS));
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.FEET, packedLight, this.getArmorModel(entity, EquipmentSlot.FEET));
        if (!firstPerson) // Don't render helmet if first person; only really applies to first person mods
            this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.HEAD, packedLight, this.getArmorModel(entity, EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack pose, MultiBufferSource buffers, T entity, EquipmentSlot slot, int packedLight, ArmorStandArmorModel<T> model) {
        ItemStack itemstack = FormFittingEnchantment.getFormFitted(entity, entity.getItemBySlot(slot), slot);
        if (itemstack.getItem() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem)itemstack.getItem();
            if (armoritem.getEquipmentSlot() == slot) {
                var altModel = model;//net.neoforged.neoforge.client.ForgeHooksClient.getArmorModel(entity, itemstack, slot, model);
                if (altModel != model) {
                    boolean flag = this.usesInnerModel(slot);
                    if (itemstack.is(ItemTags.DYEABLE)) {
                        int i = net.minecraft.world.item.component.DyedItemColor.getOrDefault(itemstack, net.minecraft.world.item.component.DyedItemColor.LEATHER_COLOR);
                        float red = (float)(i >> 16 & 255) / 255.0F;
                        float green = (float)(i >> 8 & 255) / 255.0F;
                        float blue = (float)(i & 255) / 255.0F;
                        this.renderModel(pose, buffers, packedLight,
                                altModel, red, green, blue, this.getArmorResource(entity, itemstack, slot, null));
                        this.renderModel(pose, buffers, packedLight,
                                altModel, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, "overlay"));
                    } else {
                        this.renderModel(pose, buffers, packedLight,
                                altModel, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, null));
                    }

                    ArmorTrim trim = itemstack.get(DataComponents.TRIM);
                    if (trim != null)
                        this.renderTrim(armoritem.getMaterial(), pose, buffers, packedLight, trim, altModel, flag);
                    if (itemstack.hasFoil()) {
                        this.renderGlint(pose, buffers, packedLight, altModel);
                    }
                }

                else {
                    boolean flag = this.usesInnerModel(slot);
                    if (itemstack.is(ItemTags.DYEABLE)) {
                        int i = net.minecraft.world.item.component.DyedItemColor.getOrDefault(itemstack, net.minecraft.world.item.component.DyedItemColor.LEATHER_COLOR);
                        float red = (float)(i >> 16 & 255) / 255.0F;
                        float green = (float)(i >> 8 & 255) / 255.0F;
                        float blue = (float)(i & 255) / 255.0F;
                        this.renderModel(entity, itemstack, slot, pose, buffers, packedLight,
                                model, red, green, blue, this.getArmorResource(entity, itemstack, slot, null));
                        this.renderModel(entity, itemstack, slot, pose, buffers, packedLight,
                                model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, "overlay"));
                    } else {
                        this.renderModel(entity, itemstack, slot, pose, buffers, packedLight,
                                model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, null));
                    }

                    ArmorTrim trim = itemstack.get(DataComponents.TRIM);
                    if (trim != null)
                        this.renderTrim(entity, itemstack, slot, armoritem.getMaterial(), pose, buffers, packedLight, trim, model, flag);
                    if (itemstack.hasFoil()) {
                        this.renderGlint(entity, itemstack, slot, pose, buffers, packedLight, model);
                    }
                }
            }
        }
    }

    private void renderModel(T entity, ItemStack stack, EquipmentSlot slot,
                             PoseStack pose, MultiBufferSource buffers, int packedLight, ArmorStandArmorModel<T> model,
                             float red, float green, float blue, ResourceLocation armorResource) {
        model.prepareVisibility(slot, stack);
        model.renderForSlot(entity, (RenderLayerParent) this.parent, stack, slot, pose,
                buffers.getBuffer(RenderType.armorCutoutNoCull(armorResource)),
                packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

    private void renderModel(PoseStack pose, MultiBufferSource buffers, int packedLight, net.minecraft.client.model.Model model, float red, float green, float blue, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = buffers.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        model.renderToBuffer(pose, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(1.0F, red, green, blue));
    }

    private void renderTrim(T entity, ItemStack stack, EquipmentSlot slot,
                            Holder<ArmorMaterial> material, PoseStack pose, MultiBufferSource buffers, int packedLight, ArmorTrim trim, ArmorStandArmorModel<T> model, boolean inner) {
        model.prepareVisibility(slot, stack);

        ResourceLocation trimTexture = switch (slot) {
            case HEAD -> entity.getEntityShape().headShape.getTrimTexture(trim, material);
            case CHEST -> entity.getEntityShape().torsoShape.getTrimTexture(trim, material);
            case LEGS -> entity.getEntityShape().legsShape.getTrimTexture(trim, material);
            case FEET -> entity.getEntityShape().feetShape.getTrimTexture(trim, material);
            default -> null; // Failsafe below
        };

        if (trimTexture == null)
            trimTexture = inner ? trim.innerTexture(material) : trim.outerTexture(material);

        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(trimTexture);
        VertexConsumer vertexconsumer = textureatlassprite.wrap(buffers.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
        model.renderForSlot(entity, (RenderLayerParent) this.parent, stack, slot, pose,
                vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderTrim(Holder<ArmorMaterial> material, PoseStack pose, MultiBufferSource buffers, int packedLight, ArmorTrim trim, net.minecraft.client.model.Model model, boolean inner) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(inner ? trim.innerTexture(material) : trim.outerTexture(material));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(buffers.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
        model.renderToBuffer(pose, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    private void renderGlint(T entity, ItemStack stack, EquipmentSlot slot,
                             PoseStack pose, MultiBufferSource buffers, int packedLight, ArmorStandArmorModel<T> model) {
        model.prepareVisibility(slot, stack);

        model.renderForSlot(entity, (RenderLayerParent) this.parent, stack, slot, pose,
                buffers.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderGlint(PoseStack pose, MultiBufferSource buffers, int packedLight, net.minecraft.client.model.Model model) {
        model.renderToBuffer(pose, buffers.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY);
    }

    public ArmorStandArmorModel<T> getArmorModel(T entity, EquipmentSlot slot) {
        return modelPicker.getModelForSlot(entity, slot);
    }

    private boolean usesInnerModel(EquipmentSlot p_117129_) {
        return p_117129_ == EquipmentSlot.LEGS;
    }

    /*=================================== FORGE START =========================================*/

    /**
     * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
     *
     * @param entity Entity wearing the armor
     * @param stack ItemStack for the armor
     * @param slot Slot ID that the item is in
     * @param type Subtype, can be null or "overlay"
     * @return ResourceLocation pointing at the armor's texture
     */
    public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        var layers = item.getMaterial().value().layers();
        int layerIndex = "overlay".equals(type) && layers.size() > 1 ? 1 : 0;
        return net.neoforged.neoforge.client.ClientHooks.getArmorTexture(entity, stack, layers.get(layerIndex), usesInnerModel(slot), slot);
    }
    /*=================================== FORGE END ===========================================*/
}
