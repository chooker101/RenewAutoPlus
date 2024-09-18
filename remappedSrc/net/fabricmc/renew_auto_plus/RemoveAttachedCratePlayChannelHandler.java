package net.fabricmc.renew_auto_plus;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RemoveAttachedCratePlayChannelHandler implements PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        RemoveAttachedCrateC2SPacket packet = new RemoveAttachedCrateC2SPacket(buf);
        try{
            NetworkThreadUtils.forceMainThread(packet, handler, player.getServerWorld());
        }
        catch(Exception e) {
            return;
        }
    }

    public static void onRemoveAttachedCrate(ServerPlayerEntity player, ServerPlayNetworkHandler handler, RemoveAttachedCrateC2SPacket packet) {
        if(player.method_48926() != null) {
            AbacusBlockEntity abacusBlockEntity = null;
            BlockEntity blockEntity = player.method_48926().getBlockEntity(packet.getBlockPos());
            if (blockEntity instanceof AbacusBlockEntity) {
                abacusBlockEntity = (AbacusBlockEntity)blockEntity;
            }
            if(abacusBlockEntity == null) {
                return;
            }
            abacusBlockEntity.removeCrate(packet.getCratePos());
        }
    }
}
