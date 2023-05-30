package net.fabricmc.renew_auto_plus;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

public class StringToCrateC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final BlockPos pos;
    private final String string;

    public StringToCrateC2SPacket(BlockPos pos, String string) {
        this.pos = pos;
        this.string = string;
    }

    public StringToCrateC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.string = buf.readString();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeString(this.string);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)serverPlayPacketListener;
        if(handler == null) {
            return;
        }
        StringToCratePlayChannelHandler.onStringToBlockEntity(handler.player, handler, this);
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public String getString() {
        return this.string;
    }
}
