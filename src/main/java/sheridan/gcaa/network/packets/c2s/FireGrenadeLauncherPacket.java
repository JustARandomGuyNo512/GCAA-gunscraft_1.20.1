package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class FireGrenadeLauncherPacket implements IPacket<FireGrenadeLauncherPacket> {
    public long lastFire;
    public int itemId;

    public FireGrenadeLauncherPacket() {}

    public FireGrenadeLauncherPacket(long lastFire, int itemId)  {
        this.lastFire = lastFire;
        this.itemId = itemId;
    }

    @Override
    public void encode(FireGrenadeLauncherPacket message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.lastFire);
        buffer.writeInt(message.itemId);
    }

    @Override
    public FireGrenadeLauncherPacket decode(FriendlyByteBuf buffer) {
        return new FireGrenadeLauncherPacket(buffer.readLong(), buffer.readInt());
    }

    @Override
    public void handle(FireGrenadeLauncherPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof IGun gun) {
                    Item item = Item.byId(message.itemId);
                    if (item instanceof GrenadeLauncher launcher) {
                        GrenadeLauncher.shoot(heldItem, gun, player, message.lastFire, launcher);
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
