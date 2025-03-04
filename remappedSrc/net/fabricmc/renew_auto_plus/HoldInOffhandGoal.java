package net.renew_auto_plus;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class HoldInOffhandGoal<T extends MobEntity>
extends Goal {
    private final T actor;
    private final ItemStack item;
    private final Predicate<? super T> condition;
    @Nullable
    private final SoundEvent sound;

    public HoldInOffhandGoal(T actor, ItemStack item, @Nullable SoundEvent sound, Predicate<? super T> condition) {
        this.actor = actor;
        this.item = item;
        this.sound = sound;
        this.condition = condition;
    }

    @Override
    public boolean canStart() {
        return this.condition.test(this.actor);
    }

    @Override
    public boolean shouldContinue() {
        return ((LivingEntity)this.actor).isUsingItem();
    }

    @Override
    public void start() {
        ((MobEntity)this.actor).equipStack(EquipmentSlot.OFFHAND, this.item.copy());
        ((LivingEntity)this.actor).setCurrentHand(Hand.OFF_HAND);
    }

    @Override
    public void stop() {
        ((MobEntity)this.actor).equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        if (this.sound != null) {
            ((Entity)this.actor).playSound(this.sound, 1.0f, ((LivingEntity)this.actor).getRandom().nextFloat() * 0.2f + 0.9f);
        }
    }
}
