package net.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.renew_auto_plus.helper.AttackActionReplacedWithCharge;
import net.renew_auto_plus.helper.OverworldInventoryStorage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Pseudo
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityExtension extends LivingEntity implements OverworldInventoryStorage {
    @Shadow
    private final PlayerInventory inventory;
    private final SimpleInventory storedInventory = new SimpleInventory(41); //Need to put in NBT
    private boolean hasStoredInventory = false;

    protected PlayerEntityExtension(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        inventory = new PlayerInventory(null);
    }

    @Shadow
    public abstract float getAttackCooldownProgressPerTick();
    
    @Inject(method = "getAttackCooldownProgress(F)F", at = @At("HEAD"), cancellable = true)
    public void getAttackCooldownProgressEarlyReturn(float baseTime, CallbackInfoReturnable<Float> info) {
        if(this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof AttackActionReplacedWithCharge) {
            float percentage = MathHelper.clamp(((AttackActionReplacedWithCharge)this.getStackInHand(Hand.MAIN_HAND).getItem()).getAttackCooldownPercentage(), 0.0f, 1.0f);
            info.setReturnValue(percentage);
        }
    }

    @Override
    public void storeCurrentInventory() {
        if(this.inventory == null || hasStoredInventory) {
            return;
        }
        for(int i = 0; i < this.inventory.size(); ++i) {
            storedInventory.setStack(i, this.inventory.getStack(i).copy());
            this.inventory.setStack(i, ItemStack.EMPTY);
        }
        hasStoredInventory = true;
    }
    
    @Override
    public Inventory getStoredInventory() {
        if(hasStoredInventory) {
            return storedInventory;
        }
        else {
            return null;
        }
    }
    
    @Override
    public boolean hasStoredInventory() {
        return hasStoredInventory;
    }

    @Override
    public void setHasStoredInventory(boolean hasStored) {
        hasStoredInventory = hasStored;
    }
}
