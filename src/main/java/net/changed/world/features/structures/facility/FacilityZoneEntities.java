package net.changed.world.features.structures.facility;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.Changed;
import net.changed.init.ChangedRegistry;
import net.changed.util.ResourceUtil;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.changed.compat.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public class FacilityZoneEntities extends SimplePreparableReloadListener<List<FacilityZoneEntities.ZoneEntitiesDefinition>> {
    public record EntitySpawnDefinition(EntityType<?> entityType, int weight, int minimum, int maximum, int cost) {
        public static final Codec<EntitySpawnDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("type").forGetter(EntitySpawnDefinition::entityType),
                Codec.INT.fieldOf("weight").forGetter(EntitySpawnDefinition::weight),
                Codec.INT.fieldOf("minimum").forGetter(EntitySpawnDefinition::minimum),
                Codec.INT.fieldOf("maximum").forGetter(EntitySpawnDefinition::maximum),
                Codec.INT.fieldOf("cost").forGetter(EntitySpawnDefinition::cost)
        ).apply(instance, EntitySpawnDefinition::new));
    }

    public record ZoneEntitiesDefinition(Zone zone, int available, List<EntitySpawnDefinition> spawns) {
        public static final Codec<ZoneEntitiesDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ChangedRegistry.FACILITY_ZONES.get().getCodec().fieldOf("zone").forGetter(ZoneEntitiesDefinition::zone),
                Codec.INT.fieldOf("available").forGetter(ZoneEntitiesDefinition::available),
                EntitySpawnDefinition.CODEC.listOf().fieldOf("spawns").forGetter(ZoneEntitiesDefinition::spawns)
        ).apply(instance, ZoneEntitiesDefinition::new));
    }

    public static final FacilityZoneEntities INSTANCE = new FacilityZoneEntities();

    private final Map<Zone, List<ZoneEntitiesDefinition>> facilityZoneSpawns = new HashMap<>();

    private ZoneEntitiesDefinition processJSONFile(JsonObject root) {
        return ZoneEntitiesDefinition.CODEC.decode(JsonOps.INSTANCE, root)
                .getOrThrow(RuntimeException::new).getFirst();
    }

    @Override
    @NotNull
    public List<FacilityZoneEntities.ZoneEntitiesDefinition> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        return ResourceUtil.processJSONResources(new ArrayList<>(), resources, "facility_zone_spawns", (list, filename, id, json) -> {
            list.add(processJSONFile(json));
        }, (exception, filename) -> Changed.LOGGER.error("Failed to load facility zone spawn configuration from \"{}\" : {}", filename, exception));
    }

    @Override
    protected void apply(@NotNull List<FacilityZoneEntities.ZoneEntitiesDefinition> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        for (var zone : ChangedRegistry.FACILITY_ZONES.get().getValues()) {
            facilityZoneSpawns.put(zone, output.stream().filter(definition -> definition.zone == zone).toList());
        }
    }

    public final List<ZoneEntitiesDefinition> getSpawns(Zone zone) {
        return facilityZoneSpawns.get(zone);
    }
}
