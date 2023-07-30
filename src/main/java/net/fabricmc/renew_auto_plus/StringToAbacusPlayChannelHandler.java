package net.fabricmc.renew_auto_plus;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class StringToAbacusPlayChannelHandler implements PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        StringToAbacusC2SPacket packet = new StringToAbacusC2SPacket(buf);
        try{
            NetworkThreadUtils.forceMainThread(packet, handler, player.getWorld());
        }
        catch(Exception e) {
            return;
        }
    }

    public static void onStringToAbacus(ServerPlayerEntity player, ServerPlayNetworkHandler handler, StringToAbacusC2SPacket packet) {
        if(player.world != null) {
            AbacusBlockEntity abacusBlockEntity = null;
            BlockEntity blockEntity = player.world.getBlockEntity(packet.getBlockPos());
            if (blockEntity instanceof AbacusBlockEntity) {
                abacusBlockEntity = (AbacusBlockEntity)blockEntity;
            }
            if(abacusBlockEntity == null) {
                return;
            }
            if(packet.getType() == StringToAbacusC2SPacket.COMPANY_NAME_TYPE) {
                abacusBlockEntity.updateCompanyName(packet.getString());
            }
            else if(packet.getType() == StringToAbacusC2SPacket.ADD_OWNER_NAME_TYPE) {
                abacusBlockEntity.removeOwner(packet.getString());
                abacusBlockEntity.tryAddOwner(packet.getString());
            }
            else if(packet.getType() == StringToAbacusC2SPacket.REMOVE_OWNER_NAME_TYPE) {
                abacusBlockEntity.removeOwner(packet.getString());
            }
        }
    }
}
