package net.changed.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.entity.latex.LatexType;
import net.changed.entity.variant.TransfurVariant;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.init.ChangedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TransfurPredicate {
    public static final Codec<TransfurPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("type").forGetter(TransfurPredicate::typeId),
            ResourceLocation.CODEC.listOf().optionalFieldOf("forms").forGetter(TransfurPredicate::formIds),
            Codec.BOOL.optionalFieldOf("flying", false).forGetter(TransfurPredicate::isFlying),
            Codec.BOOL.optionalFieldOf("swimming", false).forGetter(TransfurPredicate::isSwimming),
            Codec.BOOL.optionalFieldOf("legless", false).forGetter(TransfurPredicate::isLegless)
    ).apply(instance, TransfurPredicate::fromCodec));

    public static final TransfurPredicate ANY = new TransfurPredicate();
    @Nullable
    private final Set<TransfurVariant<?>> forms;
    @Nullable
    private final LatexType type;
    private final boolean flying;
    private final boolean swimming;
    private final boolean legless;

    public TransfurPredicate() {
        this.forms = null;
        this.type = null;
        this.flying = false;
        this.swimming = false;
        this.legless = false;
    }

    public TransfurPredicate(LatexType type) {
        this.forms = null;
        this.type = type;
        this.flying = false;
        this.swimming = false;
        this.legless = false;
    }

    public TransfurPredicate(Set<TransfurVariant<?>> forms) {
        this.forms = forms;
        this.type = null;
        this.flying = false;
        this.swimming = false;
        this.legless = false;
    }

    public TransfurPredicate(boolean flying, boolean swimming, boolean legless) {
        this.forms = null;
        this.type = null;
        this.flying = flying;
        this.swimming = swimming;
        this.legless = legless;
    }

    private static TransfurPredicate fromCodec(Optional<ResourceLocation> typeId, Optional<List<ResourceLocation>> formIds,
                                               boolean flying, boolean swimming, boolean legless) {
        if (typeId.isPresent())
            return new TransfurPredicate(ChangedRegistry.LATEX_TYPE.getValue(typeId.get()));
        if (formIds.isPresent()) {
            ImmutableSet.Builder<TransfurVariant<?>> builder = ImmutableSet.builder();
            for (var formId : formIds.get()) {
                if (!ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(formId))
                    throw new JsonSyntaxException("Unknown form id '" + formId + "'");
                builder.add(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(formId));
            }

            var forms = builder.build();
            if (!forms.isEmpty())
                return new TransfurPredicate(forms);
        }
        return (flying || swimming || legless) ? new TransfurPredicate(flying, swimming, legless) : ANY;
    }

    private Optional<ResourceLocation> typeId() {
        return type == null ? Optional.empty() : Optional.of(ResourceLocation.parse(type.toString()));
    }

    private Optional<List<ResourceLocation>> formIds() {
        if (forms == null)
            return Optional.empty();
        return Optional.of(forms.stream().map(TransfurVariant::getFormId).toList());
    }

    private boolean isFlying() {
        return flying;
    }

    private boolean isSwimming() {
        return swimming;
    }

    private boolean isLegless() {
        return legless;
    }

    public boolean matches(TransfurVariantInstance<?> form) {
        if (this == ANY)
            return true;
        if (forms != null)
            for (TransfurVariant<?> setForm : forms)
                if (setForm.getFormId() == form.getFormId())
                    return true;
        if (type != null)
            return form.getLatexType() == type;
        if (form.getParent().canGlide && flying)
            return true;
        if (form.getParent().getBreatheMode().canBreatheWater() && swimming)
            return true;
        if (form.getEntityShape().isLegless() && legless)
            return true;
        return false;
    }

    public static TransfurPredicate fromJson(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "form");
            if (jsonObject.has("type")) {
                final LatexType type = ChangedRegistry.LATEX_TYPE.getValue(ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "type")));
                return new TransfurPredicate(type);
            }
            if (jsonObject.has("forms")) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "forms");
                if (jsonArray != null) {
                    ImmutableSet.Builder<TransfurVariant<?>> builder = ImmutableSet.builder();
                    for (var element : jsonArray) {
                        ResourceLocation resourcelocation = ResourceLocation.parse(GsonHelper.convertToString(element, "form"));
                        if (!ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(resourcelocation))
                            throw new JsonSyntaxException("Unknown form id '" + resourcelocation + "'");
                        builder.add(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(resourcelocation));
                    }

                    Set<TransfurVariant<?>> set = builder.build();
                    if (!set.isEmpty())
                        return new TransfurPredicate(set);
                }
            }
            boolean flying = false, swimming = false, legless = false;
            if (jsonObject.has("flying"))
                flying = GsonHelper.getAsBoolean(jsonObject, "flying");
            if (jsonObject.has("swimming"))
                swimming = GsonHelper.getAsBoolean(jsonObject, "swimming");
            if (jsonObject.has("legless"))
                legless = GsonHelper.getAsBoolean(jsonObject, "legless");
            return (flying || swimming || legless) ? new TransfurPredicate(flying, swimming, legless) : ANY;
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY)
            return JsonNull.INSTANCE;
        else {
            JsonObject jsonObject = new JsonObject();
            if (this.forms != null) {
                JsonArray jsonArray = new JsonArray();

                for (var form : this.forms) {
                    jsonArray.add(form.getFormId().toString());
                }

                jsonObject.add("forms", jsonArray);
            }

            if (this.type != null) {
                jsonObject.addProperty("type", this.type.toString());
            }

            jsonObject.addProperty("flying", this.flying);
            jsonObject.addProperty("swimming", this.swimming);
            jsonObject.addProperty("legless", this.legless);
            return jsonObject;
        }
    }
}
