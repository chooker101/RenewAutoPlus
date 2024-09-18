package net.fabricmc.renew_auto_plus;

import net.minecraft.util.math.BlockPos;

public class EaterFightManager {
    private final BlockPos templeLocation;
    private final FightType fightType;

    public EaterFightManager(BlockPos templeLocation, FightType fightType) { 
        this.templeLocation = templeLocation;
        this.fightType = fightType;
    }

    public static enum FightType {
        BEGINNER,
        NORMAL,
        CHALLENGE
    }
}
