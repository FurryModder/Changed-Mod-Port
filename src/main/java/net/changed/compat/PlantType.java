package net.changed.compat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlantType {
    private static final Map<String, PlantType> TYPES = new ConcurrentHashMap<>();

    public static PlantType get(String name) {
        return TYPES.computeIfAbsent(name, PlantType::new);
    }

    private final String name;

    private PlantType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
