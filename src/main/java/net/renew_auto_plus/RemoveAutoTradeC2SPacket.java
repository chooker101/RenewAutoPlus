package net.renew_auto_plus;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

public class RemoveAutoTradeC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final BlockPos pos;
    private final int tradeIndex;

    public RemoveAutoTradeC2SPacket(BlockPos pos, int tradeIndex) {
        this.pos = pos;
        this.tradeIndex = tradeIndex;
    }

    public RemoveAutoTradeC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.tradeIndex = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(tradeIndex);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)serverPlayPacketListener;
        if(handler == null) {
            return;
        }
        RemoveAutoTradePlayChannelHandler.onRemoveAutoTrade(handler.player, handler, this);
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public int getTradeIndex() {
        return this.tradeIndex;
    }
}
