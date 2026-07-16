package net.changed.client.renderer.model.armor;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ArmorModelSet<T extends LivingEntity, M extends EntityModel<? super T>> {
    private final Map<ArmorModel, ModelLayerLocation> modelDefinitions = new HashMap<>();
    private final Function<ArmorModel, LayerDefinition> modelCreator;
    private final BiFunction<ModelPart, ArmorModel, M> modelWrapper;

    private ArmorModelSet(Map<ArmorModel, ModelLayerLocation> copiedDefinitions, BiFunction<ModelPart, ArmorModel, M> modelWrapper) {
        modelDefinitions.putAll(copiedDefinitions);
        this.modelCreator = (armorModel -> null);
        this.modelWrapper = modelWrapper;
    }

    public ArmorModelSet(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, BiFunction<ModelPart, ArmorModel, M> modelWrapper) {
        Arrays.stream(ArmorModel.values()).forEach(model -> {
            modelDefinitions.put(model, new ModelLayerLocation(rootId, model.identifier));
        });
        this.modelCreator = modelCreator;
        this.modelWrapper = modelWrapper;
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> ofUnspecified(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator) {
        return new ArmorModelSet<>(rootId, modelCreator, ((rootPart, armorModel) -> null)) {
            @Override
            public M createModel(EntityModelSet bakery, ArmorModel model) {
                throw new IllegalStateException("Attempted to create armor model wrapper for unspecified class");
            }
        };
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> of(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, BiFunction<ModelPart, ArmorModel, M> modelWrapper) {
        return new ArmorModelSet<>(rootId, modelCreator, modelWrapper);
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> castOf(ArmorModelSet<?, ?> other, BiFunction<ModelPart, ArmorModel, M> modelWrapper) {
        return new ArmorModelSet<>(other.modelDefinitions, modelWrapper) {
            @Override
            public void registerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {}
        };
    }

    public void registerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        modelDefinitions.forEach((model, id) -> consumer.accept(id, () -> modelCreator.apply(model)));
    }

    public M createModel(EntityModelSet bakery, ArmorModel model) {
        return modelWrapper.apply(bakery.bakeLayer(modelDefinitions.get(model)), model);
    }

    public Map<ArmorModel, M> createModels(EntityModelSet bakery) {
        return modelDefinitions.keySet().stream().collect(Collectors.toUnmodifiableMap(Function.identity(), armorModel -> this.createModel(bakery, armorModel)));
    }

    public ModelLayerLocation getModelName(ArmorModel model) {
        return modelDefinitions.get(model);
    }
}
