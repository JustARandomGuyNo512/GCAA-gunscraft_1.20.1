package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class FireGrenadeLauncherPacket implements IPacket<FireGrenadeLauncherPacket> {
    public long lastFire;

    public FireGrenadeLauncherPacket() {}

    public FireGrenadeLauncherPacket(long lastFire) {
        this.lastFire = lastFire;
    }

    @Override
    public void encode(FireGrenadeLauncherPacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.lastFire);
    }

    @Override
    public FireGrenadeLauncherPacket decode(FriendlyByteBuf buffer) {
        return new FireGrenadeLauncherPacket(buffer.readLong());
    }

    @Override
    public void handle(FireGrenadeLauncherPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof IGun gun) {
                    GrenadeLauncher.shoot(heldItem, gun, player, message.lastFire);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
