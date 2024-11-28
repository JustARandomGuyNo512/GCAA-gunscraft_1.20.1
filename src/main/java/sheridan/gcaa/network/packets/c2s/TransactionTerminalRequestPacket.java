package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateTransactionDataPacket;

import java.util.ArrayList;
import java.util.function.Supplier;

public class TransactionTerminalRequestPacket implements IPacket<TransactionTerminalRequestPacket> {


    @Override
    public void encode(TransactionTerminalRequestPacket message, FriendlyByteBuf buffer) {

    }

    @Override
    public TransactionTerminalRequestPacket decode(FriendlyByteBuf buffer) {
        return new TransactionTerminalRequestPacket();
    }

    @Override
    public void handle(TransactionTerminalRequestPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null && player.getServer() != null) {
                PlayerList playerList = player.getServer().getPlayerList();
                ArrayList<Integer> playerIds = new ArrayList<>();
                playerList.getPlayers().forEach(p -> {
//                  if (p.getId() != player.getId()) {
                      playerIds.add(p.getId());
//                  }
                });
                PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player),
                        new UpdateTransactionDataPacket(
                                playerIds
                        ));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
