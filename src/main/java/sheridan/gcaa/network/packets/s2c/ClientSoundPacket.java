package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class ClientSoundPacket implements IPacket<ClientSoundPacket> {
    public float originalVol;
    public float volModify;
    public float pitch;
    public float x;
    public float y;
    public float z;
    public String soundName;

    public ClientSoundPacket() {}

    public ClientSoundPacket(float originalVol, float volModify, float pitch, float x, float y, float z, String soundName) {
        this.originalVol = originalVol;
        this.volModify = volModify;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.soundName = soundName;
    }

    public ClientSoundPacket(float originalVol, float volModify, float pitch, Vec3 pos, String soundName) {
        this.originalVol = originalVol;
        this.volModify = volModify;
        this.pitch = pitch;
        this.x = (float) pos.x;
        this.y = (float) pos.y;
        this.z = (float) pos.z;
        this.soundName = soundName;
    }

    @Override
    public void encode(ClientSoundPacket message, FriendlyByteBuf buffer) {
        buffer.writeFloat(message.originalVol);
        buffer.writeFloat(message.volModify);
        buffer.writeFloat(message.pitch);
        buffer.writeFloat(message.x);
        buffer.writeFloat(message.y);
        buffer.writeFloat(message.z);
        buffer.writeUtf(message.soundName);
    }

    @Override
    public ClientSoundPacket decode(FriendlyByteBuf buffer) {
        return new ClientSoundPacket(buffer.readFloat(),buffer.readFloat(),buffer.readFloat(),
                buffer.readFloat(),buffer.readFloat(),buffer.readFloat(), buffer.readUtf());
    }

    @Override
    public void handle(ClientSoundPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    {
                        Clients.handleClientSound(message.originalVol, message.volModify, message.pitch,
                                message.x, message.y, message.z, message.soundName);
                    }
            );
        });
        supplier.get().setPacketHandled(true);
    }
}
