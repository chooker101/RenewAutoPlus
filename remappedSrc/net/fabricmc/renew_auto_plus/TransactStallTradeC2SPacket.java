package net.fabricmc.renew_auto_plus;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

public class TransactStallTradeC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final BlockPos pos;
    private final boolean isPurchase;
    private final StallTrade stallTrade;
    private final int tradeAmount;

    public TransactStallTradeC2SPacket(BlockPos pos, Boolean isPurchase, StallTrade stallTrade, int tradeAmount) {
        this.pos = pos;
        this.isPurchase = isPurchase;
        this.stallTrade = stallTrade;
        this.tradeAmount = tradeAmount;
    }

    public TransactStallTradeC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.isPurchase = buf.readBoolean();
        this.stallTrade = StallTrade.fromPacket(buf);
        this.tradeAmount = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(isPurchase);
        this.stallTrade.toPacket(buf);
        buf.writeInt(tradeAmount);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)serverPlayPacketListener;
        if(handler == null) {
            return;
        }
        TransactStallTradePlayChannelHandler.onTransactStallTrade(handler.player, handler, this);
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public Boolean isPurchase() {
        return this.isPurchase;
    }

    public StallTrade getStallTrade() {
        return this.stallTrade;
    }

    public int getTradeAmount() {
        return this.tradeAmount;
    }
}
