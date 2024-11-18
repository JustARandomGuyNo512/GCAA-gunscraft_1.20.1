package sheridan.gcaa.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.SyncPlayerStatusPacket;
import sheridan.gcaa.network.packets.s2c.BroadcastPlayerStatusPacket;

@Mod.EventBusSubscriber
public class PlayerStatusEvents {

    @SubscribeEvent
    public static void registerCapabilityEvent(RegisterCapabilitiesEvent event) {
        event.register(PlayerStatusProvider.class);
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (!event.player.level().isClientSide()) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    if (cap.dataChanged) {
                        PacketHandler.simpleChannel.send(PacketDistributor.TRACKING_ENTITY.with(() -> event.player),
                                new BroadcastPlayerStatusPacket(
                                        player.getId(),
                                        cap.getLastShoot(),
                                        cap.getLastChamberAction(),
                                        cap.getLocalTimeOffset(),
                                        cap.getLatency(),
                                        cap.isReloading()
                                ));
                        cap.dataChanged = false;
                    }
                });
            } else {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    if (cap.dataChanged) {
                        PacketHandler.simpleChannel.sendToServer(
                                new SyncPlayerStatusPacket(
                                        cap.getLastShoot(),
                                        cap.getLastChamberAction(),
                                        cap.getLocalTimeOffset(),
                                        cap.isReloading()
                                ));
                        cap.dataChanged = false;
                    }
                });
            }
        }
    }

}
