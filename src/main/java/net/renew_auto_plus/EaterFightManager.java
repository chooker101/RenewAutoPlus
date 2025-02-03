package net.renew_auto_plus;

import java.util.Vector;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class EaterFightManager {
    private final ServerWorld world;
    private final BlockPos templeLocation;
    private final BlockPos villagerLobbySpawnLocation;
    private final BlockPos playerSpawnLocation;
    private final BlockPos playerLobbySpawnLocation;
    private final BlockPos bossSpawnLocation;
    private final BlockPos villagerSpawnLocation;
    private final BlockPos arenaFrontRight;
    private final BlockPos arenaBackLeft;
    private final BlockPos mobSpawnRight;
    private final BlockPos mobSpawnLeft;
    private final Direction templeDirection;
    private final FightDifficulty fightDifficulty;
    private final Boolean isValid;

    private Vector<PlayerEntity> players;
    private Phase currentPhase;
    private int phaseTimer;

    public EaterFightManager(ServerWorld world, BlockPos templeLocation, BlockPos villagerLobbySpawnLocation, FightDifficulty fightDifficulty) { 
        this.world = world;
        this.templeLocation = templeLocation;
        this.villagerLobbySpawnLocation = villagerLobbySpawnLocation;
        this.fightDifficulty = fightDifficulty;
        this.players = new Vector<PlayerEntity>();
        int x = villagerLobbySpawnLocation.getX() - templeLocation.getX();
        int z = villagerLobbySpawnLocation.getZ() - templeLocation.getZ();
        //Don't know how to get this any other way, Villager Y is a problem, but no way to get hight, checking for air on other side.
        if(Math.abs(z) >= Math.abs(x)) {
            if(x >= 0) {
                playerSpawnLocation = new BlockPos(templeLocation.getX() + 25, villagerLobbySpawnLocation.getY(), templeLocation.getZ() - 20);
                playerLobbySpawnLocation = new BlockPos(villagerLobbySpawnLocation.getX() - 4, villagerLobbySpawnLocation.getY() - 1, villagerLobbySpawnLocation.getZ());
                bossSpawnLocation = new BlockPos(playerSpawnLocation.getX() + 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() - 25);
                villagerSpawnLocation = new BlockPos(playerSpawnLocation.getX() - 4, playerSpawnLocation.getY() + 1, playerSpawnLocation.getZ());
                arenaFrontRight = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                arenaBackLeft = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                mobSpawnRight = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                mobSpawnLeft = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                if(x == 16 && z == -20) {
                    isValid = true;
                }
                else {
                    isValid = false;
                }
                templeDirection = Direction.EAST;
            }
            else {
                playerSpawnLocation = new BlockPos(templeLocation.getX() - 25, villagerLobbySpawnLocation.getY(), templeLocation.getZ() + 20);
                playerLobbySpawnLocation = new BlockPos(villagerLobbySpawnLocation.getX() + 20, villagerLobbySpawnLocation.getY(), villagerLobbySpawnLocation.getZ() - 25);
                bossSpawnLocation = new BlockPos(playerSpawnLocation.getX() + 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() - 25);
                villagerSpawnLocation = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                arenaFrontRight = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                arenaBackLeft = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                mobSpawnRight = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                mobSpawnLeft = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                if(x == -16 && z == 20) {
                    isValid = true;
                }
                else {
                    isValid = false;
                }
                templeDirection = Direction.WEST;
            }
        }
        else {
            if(z >= 0) {
                playerSpawnLocation = new BlockPos(templeLocation.getX() - 20, villagerLobbySpawnLocation.getY(), templeLocation.getZ() + 25);
                playerLobbySpawnLocation = new BlockPos(villagerLobbySpawnLocation.getX(), villagerLobbySpawnLocation.getY(), villagerLobbySpawnLocation.getZ());
                bossSpawnLocation = new BlockPos(playerSpawnLocation.getX(), playerSpawnLocation.getY(), playerSpawnLocation.getZ());
                villagerSpawnLocation = new BlockPos(playerSpawnLocation.getX(), playerSpawnLocation.getY() + 1, playerSpawnLocation.getZ());
                arenaFrontRight = new BlockPos(playerSpawnLocation.getX(), playerSpawnLocation.getY(), playerSpawnLocation.getZ());
                arenaBackLeft = new BlockPos(playerSpawnLocation.getX(), playerSpawnLocation.getY(), playerSpawnLocation.getZ());
                mobSpawnRight = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                mobSpawnLeft = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                if(x == -20 && z == 16) {
                    isValid = true;
                }
                else {
                    isValid = false;
                }
                templeDirection = Direction.SOUTH;
            }
            else {
                playerSpawnLocation = new BlockPos(templeLocation.getX() + 20, villagerLobbySpawnLocation.getY(), templeLocation.getZ() - 25);
                playerLobbySpawnLocation = new BlockPos(villagerLobbySpawnLocation.getX() + 20, villagerLobbySpawnLocation.getY(), villagerLobbySpawnLocation.getZ() - 25);
                bossSpawnLocation = new BlockPos(playerSpawnLocation.getX() + 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() - 25);
                villagerSpawnLocation = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                arenaFrontRight = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                arenaBackLeft = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                mobSpawnRight = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                mobSpawnLeft = new BlockPos(playerSpawnLocation.getX() - 20, playerSpawnLocation.getY(), playerSpawnLocation.getZ() + 25);
                if(x == 20 && z == -16) {
                    isValid = true;
                }
                else {
                    isValid = false;
                }
                templeDirection = Direction.NORTH;
            }
        }
    }

    public void addPlayer(PlayerEntity player) {
        players.add(player);
    }

    public Vector<PlayerEntity> getPlayerList() {
        return players;
    }

    public BlockPos getPlayerSpawnLocation() {
        return playerSpawnLocation;
    }

    public BlockPos getVillagerSpawnLocation() {
        return villagerSpawnLocation;
    }

    public BlockPos getPlayerLobbySpawnLocation() {
        return playerLobbySpawnLocation;
    }

    public BlockPos getVillagerLobbySpawnLocation() {
        return villagerLobbySpawnLocation;
    }

    public Boolean isVillagerPosValid() {
        return isValid;
    }

    public void startFight() {
        //Spawn eater, health based on player list size
        currentPhase = Phase.GATHER;
        switch (fightDifficulty) {
            case BEGINNER:
                phaseTimer = 4800;
                break;
            case NORMAL:
                phaseTimer = 4800;
                break;
            case CHALLENGE:
                phaseTimer = 3600;
                break;
            default:
                break;
        }
    }

    public static enum FightDifficulty {
        BEGINNER,
        NORMAL,
        CHALLENGE
    }

    public static enum Phase {
        GATHER,
        DAMAGE,
        END
    }
}
