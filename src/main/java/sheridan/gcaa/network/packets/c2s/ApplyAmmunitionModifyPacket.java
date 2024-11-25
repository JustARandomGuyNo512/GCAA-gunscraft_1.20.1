package sheridan.gcaa.network.packets.c2s;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.screens.containers.AmmunitionModifyMenu;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateAmmunitionModifyScreenPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ApplyAmmunitionModifyPacket implements IPacket<ApplyAmmunitionModifyPacket> {
    public List<String> mods;
    public long price;

    public ApplyAmmunitionModifyPacket() {}

    public ApplyAmmunitionModifyPacket(List<IAmmunitionMod> mods) {
        this.mods = new ArrayList<>();
        for (IAmmunitionMod mod : mods) {
            this.mods.add(mod.getId().toString());
            price += mod.getPrice();
        }
    }

    @Override
    public void encode(ApplyAmmunitionModifyPacket message, FriendlyByteBuf buffer) {
        int num = message.mods.size();
        buffer.writeInt(num);
        for (String mod : message.mods) {
            buffer.writeUtf(mod);
        }
        buffer.writeLong(message.price);
    }

    @Override
    public ApplyAmmunitionModifyPacket decode(FriendlyByteBuf buffer) {
        ApplyAmmunitionModifyPacket packet = new ApplyAmmunitionModifyPacket();
        int size = buffer.readInt();
        packet.mods = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            packet.mods.add(buffer.readUtf());
        }
        packet.price = buffer.readLong();
        return packet;
    }

    @Override
    public void handle(ApplyAmmunitionModifyPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null && message.mods.size() > 0) {
                long balance = PlayerStatusProvider.getStatus(player).getBalance();
                if (player.containerMenu instanceof AmmunitionModifyMenu menu) {
                    ItemStack itemStack = menu.ammo.getItem(0);
                    if (itemStack.getItem() instanceof IAmmunition ammunition) {
                        int ammoLeft = ammunition.getAmmoLeft(itemStack);
                        if (ammoLeft > 0) {
                            message.price *= (double) ammoLeft / ammunition.get().getMaxDamage(itemStack);
                        }
                        if (message.price <= balance) {
                            ammunition.addModsById(message.mods, itemStack);
                            PlayerStatusProvider.getStatus(player).serverSetBalance(balance - message.price);
                        }
                        String modsUUID = ammunition.getModsUUID(itemStack);
                        int capacity = ammunition.getMaxModCapacity();
                        CompoundTag tag = ammunition.getModsTag(itemStack);
                        PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), new UpdateAmmunitionModifyScreenPacket(
                                modsUUID,
                                capacity,
                                tag,
                                PlayerStatusProvider.getStatus(player).getBalance()
                        ));
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
