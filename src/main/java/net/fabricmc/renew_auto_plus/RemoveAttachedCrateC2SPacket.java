package net.fabricmc.renew_auto_plus;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

public class RemoveAttachedCrateC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final BlockPos pos;
    private final BlockPos cratePos;

    public RemoveAttachedCrateC2SPacket(BlockPos pos, BlockPos cratePos) {
        this.pos = pos;
        this.cratePos = cratePos;
    }

    public RemoveAttachedCrateC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.cratePos = buf.readBlockPos();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBlockPos(this.cratePos);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)serverPlayPacketListener;
        if(handler == null) {
            return;
        }
        RemoveAttachedCratePlayChannelHandler.onRemoveAttachedCrate(handler.player, handler, this);
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public BlockPos getCratePos() {
        return this.cratePos;
    }
}
