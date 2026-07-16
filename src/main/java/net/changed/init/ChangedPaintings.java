package net.changed.init;

import net.changed.Changed;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;

import java.util.List;

public class ChangedPaintings {
    public record Entry(String name, int width, int height) {
        public ResourceKey<PaintingVariant> key() {
            return ResourceKey.create(Registries.PAINTING_VARIANT, Changed.modResource(name));
        }
    }

    public static final List<Entry> ENTRIES = List.of(
            new Entry("a_lazy_fox_on_the_paper", 3, 3),
            new Entry("creation_of_lin", 4, 2),
            new Entry("dark_latex_mask", 1, 1),
            new Entry("dr_k", 2, 2),
            new Entry("earth_and_moon", 3, 2),
            new Entry("kade_tail", 2, 2),
            new Entry("puro_doodle", 2, 2),
            new Entry("puro_place", 2, 2),
            new Entry("puro_point", 3, 2),
            new Entry("puro_portrait", 2, 3),
            new Entry("sharks_gaze", 4, 4),
            new Entry("thunder_science_building", 4, 3)
    );
}
