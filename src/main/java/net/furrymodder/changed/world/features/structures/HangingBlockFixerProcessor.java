package net.furrymodder.changed.world.features.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.furrymodder.changed.init.ChangedFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.furrymodder.changed.compat.ForgeRegistries;

/**
 * Intended to locally fix MC-102223
 * Placement processors may opt in to this fix with {@link #INSTANCE HangingBlockFixerProcessor.INSTANCE}.
 * Implementation in {@link net.furrymodder.changed.mixin.StructureTemplateMixin}
 */
public class HangingBlockFixerProcessor extends StructureProcessor {
    public static final MapCodec<HangingBlockFixerProcessor> CODEC = MapCodec.unit(() -> {
        return HangingBlockFixerProcessor.INSTANCE;
    });
    public static final HangingBlockFixerProcessor INSTANCE = new HangingBlockFixerProcessor();

    private HangingBlockFixerProcessor() {
    }

    @Override
    public StructureTemplate.StructureEntityInfo processEntity(LevelReader level, BlockPos seedPos,
                                                               StructureTemplate.StructureEntityInfo rawEntityInfo,
                                                               StructureTemplate.StructureEntityInfo entityInfo,
                                                               StructurePlaceSettings placementSettings,
                                                               StructureTemplate template) {
        if (!entityInfo.nbt.contains("TileX", Tag.TAG_ANY_NUMERIC) ||
                !entityInfo.nbt.contains("TileY", Tag.TAG_ANY_NUMERIC) ||
                !entityInfo.nbt.contains("TileZ", Tag.TAG_ANY_NUMERIC)) {
            return entityInfo;
        }

        CompoundTag fixedNbt = entityInfo.nbt.copy();
        fixedNbt.putInt("TileX", entityInfo.blockPos.getX());
        fixedNbt.putInt("TileY", entityInfo.blockPos.getY());
        fixedNbt.putInt("TileZ", entityInfo.blockPos.getZ());
        return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, fixedNbt);
    }

    protected StructureProcessorType<?> getType() {
        return ChangedFeatures.HANGING_BLOCK_FIXER_PROCESSOR.get();
    }
}
