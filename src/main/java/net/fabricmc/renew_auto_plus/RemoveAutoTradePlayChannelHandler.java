package net.fabricmc.renew_auto_plus;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RemoveAutoTradePlayChannelHandler implements PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        RemoveAutoTradeC2SPacket packet = new RemoveAutoTradeC2SPacket(buf);
        try{
            NetworkThreadUtils.forceMainThread(packet, handler, player.getServerWorld());
        }
        catch(Exception e) {
            return;
        }
    }

    public static void onRemoveAutoTrade(ServerPlayerEntity player, ServerPlayNetworkHandler handler, RemoveAutoTradeC2SPacket packet) {
        if(player.getWorld() != null) {
            AbacusBlockEntity abacusBlockEntity = null;
            BlockEntity blockEntity = player.getWorld().getBlockEntity(packet.getBlockPos());
            if (blockEntity instanceof AbacusBlockEntity) {
                abacusBlockEntity = (AbacusBlockEntity)blockEntity;
            }
            if(abacusBlockEntity == null) {
                return;
            }
            abacusBlockEntity.removeAutoTrade(packet.getTradeIndex());
        }
    }
}
