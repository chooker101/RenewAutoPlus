package net.fabricmc.renew_auto_plus.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.renew_auto_plus.RenewAutoPlusInitialize;
import net.fabricmc.renew_auto_plus.ReplacedOnAttackC2SPacket;
import net.fabricmc.renew_auto_plus.ReplacedStopAttackC2SPacket;
import net.fabricmc.renew_auto_plus.helper.AttackActionReplaced;
import net.fabricmc.renew_auto_plus.helper.AttackActionReplacedWithCharge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.thread.ReentrantThreadExecutor;

@Pseudo
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientExtension extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler {
    private Boolean wasAttackPressedLastFrame = false;

    @Shadow
    protected int attackCooldown;

    @Shadow
    public ClientPlayerEntity player;

    @Shadow
    public ClientWorld world;

    @Shadow
    public GameOptions options;

    public MinecraftClientExtension(String string) {
        super(string);
    }

    @Shadow
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Shadow
    private void doAttack() {}

    @Inject(method = "handleBlockBreaking(Z)V", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreakingEarlyReturn(boolean bl, CallbackInfo info) {
        ItemStack mainHandItem = this.player.getStackInHand(Hand.MAIN_HAND);
        if(mainHandItem.getItem() instanceof AttackActionReplaced) {
            info.cancel();
            return;
        }
    }

    @Inject(method = "doAttack()V", at = @At("HEAD"), cancellable = true)
    private void doAttackEarlyReturn(CallbackInfo info) {
        if (this.attackCooldown == 10000) {
            info.cancel();
            return;
        }
        ItemStack mainHandItem = this.player.getStackInHand(Hand.MAIN_HAND);
        if(mainHandItem.getItem() instanceof AttackActionReplaced){
            if(!((AttackActionReplaced)mainHandItem.getItem()).canAttack(player)) {
                info.cancel();
                return;
            }
            PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
            ReplacedOnAttackC2SPacket packet = new ReplacedOnAttackC2SPacket(Hand.MAIN_HAND);
            packet.write(byteBuf);
            this.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.REPLACED_ON_ATTACK_PACKET_ID, byteBuf));
            ((AttackActionReplaced)mainHandItem.getItem()).onAttackClient(this.world, this.player, Hand.MAIN_HAND);
            info.cancel();
            return;
        }
    }

    @Inject(method = "handleInputEvents()V", at = @At("RETURN"), cancellable = true)
    private void handleInputEventsExtension(CallbackInfo info) {
        ItemStack mainHandItem = this.player.getStackInHand(Hand.MAIN_HAND);
        if(mainHandItem.getItem() instanceof AttackActionReplacedWithCharge){
            if (this.options.keyAttack.isPressed()) {
                wasAttackPressedLastFrame = true;
                this.doAttack();
            }
            else if (wasAttackPressedLastFrame && ((AttackActionReplacedWithCharge)mainHandItem.getItem()).canAttack(player)) {
                wasAttackPressedLastFrame = false;
                PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
                ReplacedStopAttackC2SPacket packet = new ReplacedStopAttackC2SPacket(Hand.MAIN_HAND);
                packet.write(byteBuf);
                this.getNetworkHandler().sendPacket(ClientPlayNetworking.createC2SPacket(RenewAutoPlusInitialize.REPLACED_STOP_ATTACK_PACKET_ID, byteBuf));
                ((AttackActionReplacedWithCharge)mainHandItem.getItem()).stopAttackClient(this.world, this.player, Hand.MAIN_HAND);
            }
        }
    }
}
