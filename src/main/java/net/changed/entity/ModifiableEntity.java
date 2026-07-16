package net.changed.entity;

import java.util.Map;

public interface ModifiableEntity {
    Map<String, ModificationVector> getModificationVectors();
}
