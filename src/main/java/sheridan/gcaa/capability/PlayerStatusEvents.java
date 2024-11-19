package sheridan.gcaa.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.SyncPlayerStatusPacket;
import sheridan.gcaa.network.packets.s2c.BroadcastPlayerStatusPacket;
import sheridan.gcaa.network.packets.s2c.UpdateVendingMachineScreenPacket;

@Mod.EventBusSubscriber(modid = GCAA.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
                                        cap.getBalance(),
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

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) ->
                PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> player),
                new BroadcastPlayerStatusPacket(
                        player.getId(),
                        cap.getLastShoot(),
                        cap.getLastChamberAction(),
                        cap.getLocalTimeOffset(),
                        cap.getLatency(),
                        cap.getBalance(),
                        cap.isReloading()
                )));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // 检查玩家是否死亡导致的克隆
        if (event.isWasDeath()) {
            // 获取旧的玩家实例和新的玩家实例
            Player original = event.getOriginal();
            Player newPlayer = event.getEntity();

            PlayerStatus oldStatus = PlayerStatusProvider.getStatus(original);
            PlayerStatus newStatus = PlayerStatusProvider.getStatus(newPlayer);
            System.out.println(oldStatus.getBalance() + " old");
            System.out.println(newStatus.getBalance() + " new");

            // 检查和传递 Capability 数据
            original.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent(oldData ->
                    {
                        System.out.println("Old data: " + oldData.getBalance());
                        newPlayer.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent(newData -> {
                            // 将旧数据复制到新实例中
                            System.out.println("copying old data to new player");
                            newData.copyFrom(oldData);
                            System.out.println(newData.getBalance());
                        });
                    }
            );
            System.out.println(PlayerStatusProvider.getStatus(newPlayer).getBalance() + "<=");
        }
    }


}
