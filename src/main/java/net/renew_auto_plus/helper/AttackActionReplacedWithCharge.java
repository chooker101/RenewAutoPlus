package net.renew_auto_plus.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface AttackActionReplacedWithCharge extends AttackActionReplaced{
    public void stopAttackServer(World world, PlayerEntity user, Hand hand);

    public void stopAttackClient(World world, PlayerEntity user, Hand hand);

    public float getAttackCooldownPercentage();
}
