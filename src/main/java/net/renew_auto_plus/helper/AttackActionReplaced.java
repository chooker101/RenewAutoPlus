package net.renew_auto_plus.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface AttackActionReplaced {
    public void onAttackServer(World world, PlayerEntity user, Hand hand);

    public void onAttackClient(World world, PlayerEntity user, Hand hand);

    public boolean canAttack(PlayerEntity user);
}
