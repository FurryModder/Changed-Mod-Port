package net.changed.world.features.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.changed.init.ChangedFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.changed.compat.ForgeRegistries;

/**
 * Intended to locally fix MC-102223
 * Placement processors may opt in to this fix with {@link #INSTANCE HangingBlockFixerProcessor.INSTANCE}.
 * Implementation in {@link net.changed.mixin.StructureTemplateMixin}
 */
public class HangingBlockFixerProcessor extends StructureProcessor {
    public static final MapCodec<HangingBlockFixerProcessor> CODEC = MapCodec.unit(() -> {
        return HangingBlockFixerProcessor.INSTANCE;
    });
    public static final HangingBlockFixerProcessor INSTANCE = new HangingBlockFixerProcessor();

    private HangingBlockFixerProcessor() {
    }

    protected StructureProcessorType<?> getType() {
        return ChangedFeatures.HANGING_BLOCK_FIXER_PROCESSOR.get();
    }
}
