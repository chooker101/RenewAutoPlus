package net.fabricmc.renew_auto_plus;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.fabricmc.renew_auto_plus.helper.AttackActionReplaced;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class ReplacedOnAttackPlayChannelHandler implements PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ReplacedOnAttackC2SPacket packet = new ReplacedOnAttackC2SPacket(buf);
        try{
            NetworkThreadUtils.forceMainThread(packet, handler, player.getWorld());
        }
        catch(Exception e) {
            return;
        }
    }

    public static void onReplacedOnAttack(ServerPlayerEntity player, ServerPlayNetworkHandler handler, ReplacedOnAttackC2SPacket packet) {
        if(player.world != null) {
            Hand hand = packet.getHand();
            ItemStack handItem = player.getStackInHand(hand);
            if(handItem.getItem() instanceof AttackActionReplaced){
                ((AttackActionReplaced)handItem.getItem()).onAttackServer(player.world, player, hand);
            }
        }
    }
}
