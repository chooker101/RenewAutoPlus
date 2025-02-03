package net.renew_auto_plus;

import java.util.Vector;

import net.minecraft.network.PacketByteBuf;

public class StallTradeList extends Vector<StallTrade> {
    public void toPacket(PacketByteBuf buf) {
        buf.writeByte((byte)(this.size() & 0xFF));
        for (int i = 0; i < this.size(); ++i) {
            StallTrade stallTrade = (StallTrade)this.get(i);
            stallTrade.toPacket(buf);
        }
    }

    public static StallTradeList fromPacket(PacketByteBuf buf) {
        StallTradeList stallTradeList = new StallTradeList();
        int size = buf.readByte() & 0xFF;
        for (int i = 0; i < size; ++i) {
            StallTrade stallTrade = StallTrade.fromPacket(buf);
            stallTradeList.add(stallTrade);
        }
        return stallTradeList;
    }
}
