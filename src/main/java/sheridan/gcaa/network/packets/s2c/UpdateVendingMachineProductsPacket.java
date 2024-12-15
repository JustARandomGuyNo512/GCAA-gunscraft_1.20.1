package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.data.gun.GunPropertiesHandler;
import sheridan.gcaa.data.vendingMachineProducts.VendingMachineProductsHandler;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class UpdateVendingMachineProductsPacket implements IPacket<UpdateVendingMachineProductsPacket>  {
    public String strData;
    public byte[] byteData;

    public UpdateVendingMachineProductsPacket(String strData, byte[] byteData) {
        this.byteData = byteData;
        this.strData = strData;
    }

    public UpdateVendingMachineProductsPacket() {}

    @Override
    public void encode(UpdateVendingMachineProductsPacket message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.byteData != null);
        if (message.byteData != null) {
            buffer.writeInt(message.byteData.length);
            buffer.writeBytes(message.byteData);
        } else {
            buffer.writeUtf(message.strData);
        }
    }

    @Override
    public UpdateVendingMachineProductsPacket decode(FriendlyByteBuf buffer) {
        UpdateVendingMachineProductsPacket updateVendingMachineProductsPacket = new UpdateVendingMachineProductsPacket();
        boolean useBytes = buffer.readBoolean();
        if (useBytes) {
            int len = buffer.readInt();
            updateVendingMachineProductsPacket.byteData = new byte[len];
            buffer.readBytes(updateVendingMachineProductsPacket.byteData);
        } else {
            updateVendingMachineProductsPacket.strData = buffer.readUtf();
        }
        return updateVendingMachineProductsPacket;
    }

    @Override
    public void handle(UpdateVendingMachineProductsPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        VendingMachineProductsHandler.syncFromServer(message.strData, message.byteData);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
