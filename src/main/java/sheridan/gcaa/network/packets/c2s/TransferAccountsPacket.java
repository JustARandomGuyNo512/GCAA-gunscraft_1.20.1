package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateTransferBalancePacket;

import java.util.UUID;
import java.util.function.Supplier;

public class TransferAccountsPacket implements IPacket<TransferAccountsPacket> {
    public UUID currentPlayerId;
    public long transferMoney;

    public TransferAccountsPacket(UUID currentPlayerId, long transferMoney) {
        this.currentPlayerId = currentPlayerId;
        this.transferMoney = transferMoney;
    }

    public TransferAccountsPacket() {
    }

    @Override
    public void encode(TransferAccountsPacket message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.currentPlayerId);
        buffer.writeLong(message.transferMoney);
    }

    @Override
    public TransferAccountsPacket decode(FriendlyByteBuf buffer) {
        return new TransferAccountsPacket(buffer.readUUID(), buffer.readLong());
    }

    @Override
    public void handle(TransferAccountsPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null && player.getServer() != null) {
                long balance = PlayerStatusProvider.getStatus(player).getBalance();
                if (balance >= message.transferMoney) {
                    // 转账给的玩家
                    ServerPlayer transferTo = player.getServer().getPlayerList().getPlayer(message.currentPlayerId);
                    if (transferTo != null) {
                        // 被转账的玩家加钱
                        PlayerStatusProvider.getStatus(transferTo).serverSetBalance(PlayerStatusProvider.getStatus(transferTo).getBalance() + message.transferMoney);
                        // 转账的玩家扣钱
                        PlayerStatusProvider.getStatus(player).serverSetBalance(PlayerStatusProvider.getStatus(player).getBalance() - message.transferMoney);
                        PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player),
                                new UpdateTransferBalancePacket(PlayerStatusProvider.getStatus(player).getBalance()));
                        PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> transferTo),
                                new UpdateTransferBalancePacket(PlayerStatusProvider.getStatus(transferTo).getBalance()));
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
