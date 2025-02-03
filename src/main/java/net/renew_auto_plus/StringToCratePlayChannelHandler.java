package net.renew_auto_plus;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class StringToCratePlayChannelHandler implements PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        StringToCrateC2SPacket packet = new StringToCrateC2SPacket(buf);
        try{
            NetworkThreadUtils.forceMainThread(packet, handler, player.getServerWorld());
        }
        catch(Exception e) {
            return;
        }
    }

    public static void onStringToCrate(ServerPlayerEntity player, ServerPlayNetworkHandler handler, StringToCrateC2SPacket packet) {
        if(player.getWorld() != null) {
            CrateBlockEntity crateBlockEntity = null;
            BlockEntity blockEntity = player.getWorld().getBlockEntity(packet.getBlockPos());
            if (blockEntity instanceof CrateBlockEntity) {
                crateBlockEntity = (CrateBlockEntity)blockEntity;
            }
            if(crateBlockEntity == null) {
                return;
            }
            crateBlockEntity.companyName = packet.getString();
            crateBlockEntity.tryToAttachToAbacus();
        }
    }
}
