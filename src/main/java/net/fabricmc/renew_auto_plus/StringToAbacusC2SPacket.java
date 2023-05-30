package net.fabricmc.renew_auto_plus;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;

public class StringToAbacusC2SPacket
implements Packet<ServerPlayPacketListener> {
    public static final byte COMPANY_NAME_TYPE = 0;
    public static final byte OWNER_NAME_TYPE = 0;
    private final BlockPos pos;
    private final String string;
    private final byte packetDestination;
    private final byte packetType;

    public StringToAbacusC2SPacket(BlockPos pos, String string, byte packetDestination, byte packetType) {
        this.pos = pos;
        this.string = string;
        this.packetDestination = packetDestination;
        this.packetType = packetType;
    }

    public StringToAbacusC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.string = buf.readString();
        this.packetDestination = buf.readByte();
        this.packetType = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeString(this.string);
        buf.writeByte(this.packetDestination);
        buf.writeByte(this.packetType);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)serverPlayPacketListener;
        if(handler == null) {
            return;
        }
        StringToAbacusPlayChannelHandler.onStringToBlockEntity(handler.player, handler, this);
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public String getString() {
        return this.string;
    }

    public byte getDestination() {
        return this.packetDestination;
    }

    public byte getType() {
        return this.packetType;
    }
}
