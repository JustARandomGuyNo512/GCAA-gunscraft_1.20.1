package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateVendingMachineScreenPacket;
import sheridan.gcaa.service.ProductTradingHandler;

import java.util.function.Supplier;

public class BuyProductPacket implements IPacket<BuyProductPacket> {
    public ItemStack itemStack;
    public int productId;

    public BuyProductPacket() {}

    public BuyProductPacket(ItemStack itemStack, int productId) {
        this.itemStack = itemStack;
        this.productId = productId;
    }

    @Override
    public void encode(BuyProductPacket message, FriendlyByteBuf buffer) {
        buffer.writeItemStack(message.itemStack, false);
        buffer.writeInt(message.productId);
    }

    @Override
    public BuyProductPacket decode(FriendlyByteBuf buffer) {
        return new BuyProductPacket(buffer.readItem(), buffer.readInt());
    }

    @Override
    public void handle(BuyProductPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                long balance = ProductTradingHandler.buy(player, message.itemStack, message.productId);
                PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateVendingMachineScreenPacket(
                        balance
                ));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
