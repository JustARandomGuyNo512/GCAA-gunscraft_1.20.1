package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class AmmunitionManagePacket implements IPacket<AmmunitionManagePacket> {
    @Override
    public void encode(AmmunitionManagePacket message, FriendlyByteBuf buffer) {}

    @Override
    public AmmunitionManagePacket decode(FriendlyByteBuf buffer) {
        return new AmmunitionManagePacket();
    }

    @Override
    public void handle(AmmunitionManagePacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                AmmunitionHandler.manageAmmunition(player, player.getMainHandItem());
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
