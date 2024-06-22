package sheridan.gcaa.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.packets.c2s.GunFirePacket;
import sheridan.gcaa.network.packets.c2s.SyncPlayerStatusPacket;
import sheridan.gcaa.network.packets.s2c.BroadcastPlayerStatusPacket;

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
    }

    private static <T> void registerPacket(Class<T> clazz, IPacket<T> message) {
        simpleChannel.registerMessage(tempId++, clazz, message::encode, message::decode, message::handle);
    }
}