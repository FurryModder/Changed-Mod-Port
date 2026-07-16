package net.changed.util;

import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.random.WeightedRandomList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StreamUtil {
    public static <T> Stream<T> shuffledStream(List<T> list, RandomSource random) {
        if (list.isEmpty()) {
            return Stream.empty();
        } else if (list.size() == 1) {
            return Stream.of(list.get(0));
        } else if (list.size() == 2) {
            final var first = Util.getRandom(list, random);

            return Stream.of(first, list.get((list.indexOf(first) + 1) % 2));
        } else {
            final var remaining = new ArrayList<>(list);

            return Stream.iterate(Util.getRandom(remaining, random),
                    prev -> !remaining.isEmpty(),
                    prev -> {
                        remaining.remove(prev);
                        if (remaining.isEmpty())
                            return null;
                        if (remaining.size() == 1)
                            return remaining.get(0);

                        return Util.getRandom(remaining, random);
                    });
        }
    }

    public static <T extends WeightedEntry> Stream<T> weightedShuffledStream(List<T> list, RandomSource random) {
        return weightedShuffledStream(list, random, WeightedRandom.getTotalWeight(list));
    }

    public static <T extends WeightedEntry> Stream<T> weightedShuffledStream(WeightedRandomList<T> list, RandomSource random) {
        return weightedShuffledStream(list.unwrap(), random, WeightedRandom.getTotalWeight(list.unwrap()));
    }

    public static <T extends WeightedEntry> Stream<T> weightedShuffledStream(List<T> list, RandomSource random, int totalWeight) {
        if (list.isEmpty() || totalWeight == 0) {
            return Stream.empty();
        } else if (list.size() == 1) {
            return Stream.of(list.get(0));
        } else if (list.size() == 2) {
            final var first = WeightedRandom.getWeightedItem(list, random.nextInt(totalWeight)).orElseThrow();

            return Stream.of(first, list.get((list.indexOf(first) + 1) % 2));
        } else {
            final var remaining = new ArrayList<>(list);

            return Stream.iterate(WeightedRandom.getWeightedItem(list, random.nextInt(totalWeight)).orElseThrow(),
                    prev -> !remaining.isEmpty(),
                    prev -> {
                        remaining.remove(prev);
                        if (remaining.isEmpty())
                            return null;
                        if (remaining.size() == 1)
                            return remaining.get(0);

                        final int remainingWeight = WeightedRandom.getTotalWeight(remaining);

                        return WeightedRandom.getWeightedItem(remaining, random.nextInt(remainingWeight)).orElseThrow();
                    });
        }
    }
}
