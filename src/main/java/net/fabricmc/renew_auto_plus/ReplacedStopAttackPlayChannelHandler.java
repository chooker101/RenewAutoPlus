package net.fabricmc.renew_auto_plus;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.fabricmc.renew_auto_plus.helper.AttackActionReplacedWithCharge;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class ReplacedStopAttackPlayChannelHandler implements PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ReplacedStopAttackC2SPacket packet = new ReplacedStopAttackC2SPacket(buf);
        try{
            NetworkThreadUtils.forceMainThread(packet, handler, player.getWorld());
        }
        catch(Exception e) {
            return;
        }
    }

    public static void onReplacedStopAttack(ServerPlayerEntity player, ServerPlayNetworkHandler handler, ReplacedStopAttackC2SPacket packet) {
        if(player.world != null) {
            Hand hand = packet.getHand();
            ItemStack handItem = player.getStackInHand(hand);
            if(handItem.getItem() instanceof AttackActionReplacedWithCharge){
                ((AttackActionReplacedWithCharge)handItem.getItem()).stopAttackServer(player.world, player, hand);
            }
        }
    }
}
