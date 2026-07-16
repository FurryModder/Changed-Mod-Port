package net.changed.init;

import net.changed.Changed;
import net.changed.world.features.structures.facility.types.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedFacilityPieceTypes {
    public static final DeferredRegister<PieceType<?>> REGISTRY = ChangedRegistry.FACILITY_PIECE_TYPES.createDeferred(Changed.MODID);

    /**
     * The first piece to be placed in facility generation. Will always connect to a "changed:staircase_start"
     */
    public static final DeferredHolder<PieceType<?>, EntranceType> ENTRANCE = REGISTRY.register("entrance", EntranceType::new);
    /**
     * Start of staircase generation. Next piece will always be a "changed:staircase_section"
     */
    public static final DeferredHolder<PieceType<?>, StaircaseStartType> STAIRCASE_START = REGISTRY.register("staircase_start", StaircaseStartType::new);
    /**
     * Section of staircase generation. Next piece will likely be a "changed:staircase_section" or else a "changed:staircase_end"
     */
    public static final DeferredHolder<PieceType<?>, StaircaseSectionType> STAIRCASE_SECTION = REGISTRY.register("staircase_section", StaircaseSectionType::new);
    /**
     * End of staircase generation. Next piece will always be a "changed:split"
     */
    public static final DeferredHolder<PieceType<?>, StaircaseEndType> STAIRCASE_END = REGISTRY.register("staircase_end", StaircaseEndType::new);
    /**
     * Hallway/corridor. Takes you from one place to another, and has one entrance and one exit.
     * Next piece could be a "changed:corridor", "changed:split", "changed:transition", "changed:room"
     */
    public static final DeferredHolder<PieceType<?>, CorridorType> CORRIDOR = REGISTRY.register("corridor", CorridorType::new);
    /**
     * Splits generation into 2 or more paths. Next piece could be a "changed:corridor", "changed:transition", "changed:room"
     */
    public static final DeferredHolder<PieceType<?>, SplitType> SPLIT = REGISTRY.register("split", SplitType::new);
    /**
     * Transitions between zones. Next piece will always be a "changed:corridor"
     */
    public static final DeferredHolder<PieceType<?>, TransitionType> TRANSITION = REGISTRY.register("transition", TransitionType::new);
    /**
     * Emergency piece that is used to seal generation failures. Never generated initially
     */
    public static final DeferredHolder<PieceType<?>, SealType> SEAL = REGISTRY.register("seal", SealType::new);
    /**
     * End of a generation path. Also is used if generation fails, prioritized over a "changed:seal"
     */
    public static final DeferredHolder<PieceType<?>, RoomType> ROOM = REGISTRY.register("room", RoomType::new);
}
