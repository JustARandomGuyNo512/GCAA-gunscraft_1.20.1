package sheridan.gcaa.network.packets.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class UpdateAmmunitionModifyScreenPacket implements IPacket<UpdateAmmunitionModifyScreenPacket> {
    public String modsUUID;
    public int maxModCapability;
    public CompoundTag modsTag;
    public long balance;

    public UpdateAmmunitionModifyScreenPacket() {}

    public UpdateAmmunitionModifyScreenPacket(String modsUUID, int maxModCapability, CompoundTag modsTag, long balance) {
        this.modsUUID = modsUUID;
        this.maxModCapability = maxModCapability;
        this.modsTag = modsTag;
        this.balance = balance;
    }

    @Override
    public void encode(UpdateAmmunitionModifyScreenPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.modsUUID);
        buffer.writeInt(message.maxModCapability);
        buffer.writeNbt(message.modsTag);
        buffer.writeLong(message.balance);
    }

    @Override
    public UpdateAmmunitionModifyScreenPacket decode(FriendlyByteBuf buffer) {
        return new UpdateAmmunitionModifyScreenPacket(
                buffer.readUtf(),
                buffer.readInt(),
                buffer.readNbt(),
                buffer.readLong()
        );
    }

    @Override
    public void handle(UpdateAmmunitionModifyScreenPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.updateAmmunitionModifyScreen(message.modsUUID, message.maxModCapability, message.modsTag, message.balance);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
