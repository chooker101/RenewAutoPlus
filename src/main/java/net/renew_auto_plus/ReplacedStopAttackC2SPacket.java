package net.renew_auto_plus;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Hand;

public class ReplacedStopAttackC2SPacket implements Packet<ServerPlayPacketListener> {
    int hand; //Maybe delete as always main currntly

    public ReplacedStopAttackC2SPacket(Hand hand) {
        if(hand == Hand.MAIN_HAND){
            this.hand = 0;
        }
        else {
            this.hand = 1;
        }
    }

    public ReplacedStopAttackC2SPacket(PacketByteBuf buf) {
        this.hand = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.hand);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)serverPlayPacketListener;
        if(handler == null) {
            return;
        }
        ReplacedStopAttackPlayChannelHandler.onReplacedStopAttack(handler.player, handler, this);
    }

    public Hand getHand() {
        if(hand == 0) {
            return Hand.MAIN_HAND;
        }
        return Hand.OFF_HAND;
    }
}
