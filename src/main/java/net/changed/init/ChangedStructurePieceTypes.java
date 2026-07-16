package net.changed.init;

import net.changed.Changed;
import net.changed.world.features.structures.SurfaceNBTPiece;
import net.changed.world.features.structures.facility.FacilityKeystone;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Locale;

public class ChangedStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> REGISTRY = DeferredRegister.create(Registries.STRUCTURE_PIECE, Changed.MODID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> NBT = setTemplatePieceId(SurfaceNBTPiece::new, "nbt");
    public static final DeferredHolder<StructurePieceType, StructurePieceType> FACILITY_SINGLE = setTemplatePieceId(FacilitySinglePiece.StructureInstance::new, "facility_single");
    public static final DeferredHolder<StructurePieceType, StructurePieceType> FACILITY_KEYSTONE = setTemplatePieceId(FacilityKeystone::new, "facility_keystone");

    private static DeferredHolder<StructurePieceType, StructurePieceType> setFullContextPieceId(StructurePieceType type, String name) {
        return REGISTRY.register(name.toLowerCase(Locale.ROOT), () -> type);
    }

    private static DeferredHolder<StructurePieceType, StructurePieceType> setPieceId(StructurePieceType.ContextlessType piece, String name) {
        return setFullContextPieceId(piece, name);
    }

    private static DeferredHolder<StructurePieceType, StructurePieceType> setTemplatePieceId(StructurePieceType.StructureTemplateType piece, String name) {
        return setFullContextPieceId(piece, name);
    }
}
