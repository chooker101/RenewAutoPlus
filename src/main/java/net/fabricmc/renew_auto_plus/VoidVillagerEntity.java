package net.fabricmc.renew_auto_plus;

import java.util.UUID;
import java.util.Vector;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;

public class VoidVillagerEntity extends PassiveEntity implements VillagerDataContainer {
    private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VoidVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    private EaterFightManager fightManager;
    private State currentState = State.NORMAL;
    private int stateTimer = 0;

    @Nullable
    private UUID targetUuid;

    public VoidVillagerEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0).add(EntityAttributes.GENERIC_ATTACK_SPEED, 8.0).add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }
    
    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        return entityData;
    }

    @SuppressWarnings("resource")
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(this.isBaby()) {
            return super.interactMob(player, hand);
        }
        if (itemStack.isOf(Items.BUCKET)) {
            //player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0f, 1.0f);
            ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.getDefaultStack());
            player.setStackInHand(hand, itemStack2);
            return ActionResult.success(this.getWorld().isClient);
        }
        else if(itemStack.isEmpty()){
            ItemStack book = Items.WRITTEN_BOOK.getDefaultStack();
            NbtCompound nbt = new NbtCompound();
            nbt.putString(WrittenBookItem.TITLE_KEY, "You're a Nerd");
            nbt.putString(WrittenBookItem.AUTHOR_KEY, "Void Villager");
            NbtList nbtList = new NbtList();
            nbtList.add(0, NbtString.of("Ligma"));
            nbt.put(WrittenBookItem.PAGES_KEY, nbtList);
            book.setNbt(nbt);
            player.setStackInHand(hand, book);
            return ActionResult.success(this.getWorld().isClient);
        }
        else if(itemStack.isOf(Items.WRITTEN_BOOK)) {
            //WrittenBookItem book = (WrittenBookItem)itemStack.getItem();
            NbtCompound nbt = itemStack.getNbt();
            if(!WrittenBookItem.isValid(nbt)) {
                return super.interactMob(player, hand);
            }
            String title = nbt.getString(WrittenBookItem.TITLE_KEY);
            if(title.equals("You're a Nerd")) {
                if(!this.getWorld().isClient) {
                    ServerWorld world = (ServerWorld)this.getWorld();
                    TagKey<Structure> tag = TagKey.of(RegistryKeys.STRUCTURE, new Identifier("renew_auto_plus", "void_temple"));
                    BlockPos nearestTemple = world.locateStructure(tag, this.getBlockPos(), 50, false);
                    //nearestTemple = world.getStructureAccessor().getStructureContaining(this.getBlockPos(), tag).getPos().;
                    fightManager = new EaterFightManager(world, nearestTemple, this.getBlockPos(), EaterFightManager.FightDifficulty.BEGINNER);
                    fightManager.addPlayer(player);
                    if(!fightManager.isVillagerPosValid()) {
                        return super.interactMob(player, hand);
                    }
                    BlockPos spawnLocation = fightManager.getPlayerSpawnLocation();
                    if(world.getBlockState(spawnLocation).isAir()) {
                        player.teleport(((double)spawnLocation.getX()) + 0.5, spawnLocation.getY(), ((double)spawnLocation.getZ()) + 0.5);
                    }
                    else {
                        return super.interactMob(player, hand);
                    }
                    spawnLocation = fightManager.getVillagerSpawnLocation();
                    if(world.getBlockState(spawnLocation).isAir()) {
                        this.teleport(((double)spawnLocation.getX()) + 0.5, spawnLocation.getY(), ((double)spawnLocation.getZ()) + 0.5);
                    }
                    else {
                        return super.interactMob(player, hand);
                    }
                    currentState = State.BATTLE;
                    stateTimer = 40;
                }
                return ActionResult.success(this.getWorld().isClient);
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        if(stateTimer > 0) {
            stateTimer--;
        }
        else {
            if(currentState == State.BATTLE) {
                currentState = State.NORMAL;
                BlockPos spawnLocation = fightManager.getVillagerLobbySpawnLocation();
                if(this.getWorld().getBlockState(spawnLocation).isAir()) {
                    this.teleport(((double)spawnLocation.getX()) + 0.5, spawnLocation.getY(), ((double)spawnLocation.getZ()) + 0.5);
                }
                spawnLocation = fightManager.getPlayerLobbySpawnLocation();
                if(this.getWorld().getBlockState(spawnLocation).isAir()) {
                    Vector<PlayerEntity> players = fightManager.getPlayerList();
                    for (PlayerEntity player : players) {
                        player.teleport(((double)spawnLocation.getX()) + 0.5, spawnLocation.getY(), ((double)spawnLocation.getZ()) + 0.5);
                    }
                }
            }
            else if(currentState == State.CHARGING) {
                currentState = State.BATTLE;
                //Move fight start code here
            }
        }
        super.tick();
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity entity) {
        return null;
    }

    @Override
    public VillagerData getVillagerData() {
        return this.dataTracker.get(VILLAGER_DATA);
    }

    @Override
    public void setVillagerData(VillagerData villagerData) {
        this.dataTracker.set(VILLAGER_DATA, villagerData);
    }

    public static enum State {
        NORMAL,
        CHARGING,
        BATTLE
    }
}
