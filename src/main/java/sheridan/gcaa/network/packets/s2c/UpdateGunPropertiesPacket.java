package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.data.gun.GunPropertiesHandler;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class UpdateGunPropertiesPacket implements IPacket<UpdateGunPropertiesPacket> {
    public byte[] data = null;
    public String strData = null;

    public UpdateGunPropertiesPacket() {}

    public UpdateGunPropertiesPacket(byte[] data, String strData) {
        this.data = data;
        this.strData = strData;
    }
    @Override
    public void encode(UpdateGunPropertiesPacket message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.data != null);
        if (message.data != null) {
            buffer.writeInt(message.data.length);
            buffer.writeBytes(message.data);
        } else {
            buffer.writeUtf(message.strData);
        }
    }

    @Override
    public UpdateGunPropertiesPacket decode(FriendlyByteBuf buffer) {
        UpdateGunPropertiesPacket updateGunPropertiesPacket = new UpdateGunPropertiesPacket();
        boolean useBytes = buffer.readBoolean();
        if (useBytes) {
            int len = buffer.readInt();
            updateGunPropertiesPacket.data = new byte[len];
            buffer.readBytes(updateGunPropertiesPacket.data);
        } else {
            updateGunPropertiesPacket.strData = buffer.readUtf();
        }
        return updateGunPropertiesPacket;
    }

    @Override
    public void handle(UpdateGunPropertiesPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        GunPropertiesHandler.syncFromServer(message.data, message.strData);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
