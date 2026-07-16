package net.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

public class LightExposureCondition extends AbstractCondition {
    public final int threshold;
    public final float blockScale;
    public final float sunScale;
    public final float moonScale;
    public final Operation operation;

    public static final Codec<LightExposureCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("threshold").forGetter(condition -> condition.threshold),
            Codec.FLOAT.fieldOf("blockScale").orElse(1f).forGetter(condition -> condition.blockScale),
            Codec.FLOAT.fieldOf("sunScale").orElse(1f).forGetter(condition -> condition.sunScale),
            Codec.FLOAT.fieldOf("moonScale").orElse(1f).forGetter(condition -> condition.moonScale),
            Operation.CODEC.fieldOf("operation").orElse(Operation.GREATER_THAN).forGetter(condition -> condition.operation)
    ).apply(instance, LightExposureCondition::new));
    
    public LightExposureCondition(int threshold, float blockScale, float sunScale, float moonScale, Operation operation) {
        this.threshold = threshold;
        this.blockScale = blockScale;
        this.sunScale = sunScale;
        this.moonScale = moonScale;
        this.operation = operation;
    }

    @Override
    public boolean test(IAbstractChangedEntity entity) {
        var level = entity.getLevel();
        BlockPos checkPos = EntityUtil.getEyeBlock(entity.getEntity());

        int skyBrightness = level.getBrightness(LightLayer.SKY, checkPos) - level.getSkyDarken();
        float sunAngle = level.getSunAngle(1.0F);
        if (skyBrightness > 0) {
            float f1 = sunAngle < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            sunAngle += (f1 - sunAngle) * 0.2F;
            skyBrightness = Math.max(Math.round((float)skyBrightness * Mth.clamp(Mth.cos(sunAngle) * 1.7f, 0f, 1f) * sunScale), 0);
        }

        int blockBrightness = Math.round((float)level.getBrightness(LightLayer.BLOCK, checkPos) * blockScale);

        float moonBrightness = level.getMoonBrightness() * 4f * (level.getBrightness(LightLayer.SKY, checkPos) / 15f);
        float moonAngle = 1.0f - level.getSunAngle(1.0F);
        if (moonBrightness > 0) {
            float f1 = moonAngle < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            moonAngle += (f1 - moonAngle) * 0.2F;
            moonBrightness = Math.max(Math.round((float)moonBrightness * Mth.clamp(Mth.cos(moonAngle) * 1.7f, 0f, 1f) * moonScale), 0);
        }

        return operation.test((int)(skyBrightness + blockBrightness + moonBrightness), this.threshold);
    }

    @Override
    public Codec<? extends AbstractCondition> getCodec() {
        return CODEC;
    }
}
