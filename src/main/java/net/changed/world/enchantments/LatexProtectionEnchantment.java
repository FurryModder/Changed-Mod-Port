package net.changed.world.enchantments;

import net.changed.init.ChangedEnchantments;
import net.minecraft.world.entity.LivingEntity;

public class LatexProtectionEnchantment {
    public static float getLatexProtection(LivingEntity entity, float progression) {
        int protection = 0;
        int tfResistance = ChangedEnchantments.getEntityLevel(entity, ChangedEnchantments.TRANSFUR_RESISTANCE);

        float tfResistanceDiscount = progression * (float)tfResistance * 0.15F;
        float protectionDiscount = progression * (float)protection * 0.075F;

        return progression - Math.max(tfResistanceDiscount, protectionDiscount);
    }
}
