package sheridan.gcaa.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    public static void playerServerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (!event.player.level().isClientSide()) {//send broadcast
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    if (cap.dataChanged) {
                        PacketHandler.simpleChannel.send(PacketDistributor.TRACKING_ENTITY.with(() -> event.player),
                                new BroadcastPlayerStatusPacket(
                                        player.getId(),
                                        cap.getLastShootLeft(),
                                        cap.getLastShootRight(),
                                        cap.getLastChamberAction(),
                                        cap.isReloading()
                                ));
                        cap.dataChanged = false;
                    }
                });
            } else {// send to server
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    if (cap.dataChanged) {
                        PacketHandler.simpleChannel.send(PacketDistributor.TRACKING_ENTITY.with(() -> event.player),
                                new SyncPlayerStatusPacket(
                                        cap.getLastShootLeft(),
                                        cap.getLastShootRight(),
                                        cap.getLastChamberAction(),
                                        cap.isReloading()
                                ));
                        cap.dataChanged = false;
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
//        PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getOriginal()), new AfterPlayerRebornPacket());
        if (event.getOriginal().level().isClientSide() && event.isWasDeath()) {
            System.out.println("Player is reborn client side");
        }
    }

}
