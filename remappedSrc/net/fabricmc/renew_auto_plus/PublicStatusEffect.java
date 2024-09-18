package net.fabricmc.renew_auto_plus;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class PublicStatusEffect extends StatusEffect {
    public PublicStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
    }
}
