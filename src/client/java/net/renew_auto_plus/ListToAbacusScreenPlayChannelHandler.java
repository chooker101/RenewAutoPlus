package net.renew_auto_plus;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ListToAbacusScreenPlayChannelHandler implements PlayChannelHandler {
    public static final byte STALL_TRADE_LIST_TYPE = 0;
    public static final byte OWNER_NAME_LIST_TYPE = 1;
    public static final byte ATTACHED_CRATES_LIST_TYPE = 2;
    public static final byte AUTO_TRADE_LIST_TYPE = 3;

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(!(client.currentScreen instanceof AbacusScreen)) {
            return;
        }
        AbacusScreenHandler screenHandler = ((AbacusScreen)client.currentScreen).getScreenHandler();

        if(!buf.readBoolean()) return;

        byte listType = buf.readByte();

        if(listType == STALL_TRADE_LIST_TYPE) {
            client.execute(() -> {
                screenHandler.updateStallTradeList(StallTradeList.fromPacket(buf));
            });
        }
        else if(listType == OWNER_NAME_LIST_TYPE) {
            client.execute(() -> {
                int listSize = buf.readByte() & 0xFF;
                DefaultedList<String> ownerNames = DefaultedList.ofSize(listSize);
                for(int i = 0; i < listSize; i++) {
                    ownerNames.add(i, buf.readString());
                }
                screenHandler.updateOwnerNameList(ownerNames);
            });
        }
        else if(listType == ATTACHED_CRATES_LIST_TYPE) {
            client.execute(() -> {
                int listSize = buf.readByte() & 0xFF;
                DefaultedList<BlockPos> attachedCrates = DefaultedList.ofSize(listSize);
                for(int i = 0; i < listSize; i++) {
                    attachedCrates.add(i, buf.readBlockPos());
                }
                screenHandler.updateAttachedCrateList(attachedCrates);
            });
        }
        else if(listType == AUTO_TRADE_LIST_TYPE) {
            client.execute(() -> {
                screenHandler.updateAutoTradeList(StallTradeList.fromPacket(buf));
            });
        }
    }
}
