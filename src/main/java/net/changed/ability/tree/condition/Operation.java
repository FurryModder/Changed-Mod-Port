package net.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;

public enum Operation implements StringRepresentable, BiPredicate<Integer, Integer> {
    GREATER_THAN("greater_than", (left, right) -> left > right),
    GREATER_THAN_EQUAL_TO("greater_than_equal_to", (left, right) -> left >= right),
    LESS_THAN("less_than", (left, right) -> left < right),
    LESS_THAN_EQUAL_TO("less_than_equal_to", (left, right) -> left <= right),
    EQUAL_TO("equal_to", Objects::equals),
    NOT_EQUAL_TO("not_equal_to", (left, right) -> !Objects.equals(left, right));

    public static Codec<Operation> CODEC = Codec.STRING.comapFlatMap(Operation::fromSerial, Operation::getSerializedName);
    
    public final String serialName;
    public final BiPredicate<Integer, Integer> predicate;

    Operation(String serialName, BiPredicate<Integer, Integer> predicate) {
        this.serialName = serialName;
        this.predicate = predicate;
    }

    @Override
    public String getSerializedName() {
        return serialName;
    }

    public static DataResult<Operation> fromSerial(String name) {
        return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> name + " is not a valid Operation"));
    }

    @Override
    public boolean test(Integer left, Integer right) {
        return predicate.test(left, right);
    }
}
