package sheridan.gcaa.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.packets.c2s.*;
import sheridan.gcaa.network.packets.s2c.BroadcastPlayerStatusPacket;
import sheridan.gcaa.network.packets.s2c.ClientPlayParticlePacket;
import sheridan.gcaa.network.packets.s2c.UpdateAttachmentScreenGuiContext;

public class PacketHandler
{
    public static final String PROTOCOL_VERSION = GCAA.MODID + "1.0";
    public static SimpleChannel simpleChannel;
    private static int tempId;

    public static void register()
    {
        simpleChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(GCAA.MODID, "common"), ()-> PROTOCOL_VERSION,
                (s) ->true, (s) ->true);

        registerPacket(SyncPlayerStatusPacket.class, new SyncPlayerStatusPacket());
        registerPacket(BroadcastPlayerStatusPacket.class, new BroadcastPlayerStatusPacket());
        registerPacket(GunFirePacket.class, new GunFirePacket());
        registerPacket(SwitchFireModePacket.class, new SwitchFireModePacket());
        registerPacket(GunReloadPacket.class, new GunReloadPacket());
        registerPacket(ClientPlayParticlePacket.class, new ClientPlayParticlePacket());
        registerPacket(OpenAttachmentScreenPacket.class, new OpenAttachmentScreenPacket());
        registerPacket(SetAttachmentsPacket.class, new SetAttachmentsPacket());
        registerPacket(UpdateAttachmentScreenGuiContext.class, new UpdateAttachmentScreenGuiContext());
    }

    private static <T> void registerPacket(Class<T> clazz, IPacket<T> message) {
        simpleChannel.registerMessage(tempId++, clazz, message::encode, message::decode, message::handle);
    }
}