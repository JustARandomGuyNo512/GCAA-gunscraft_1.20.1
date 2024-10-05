package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class PlayerPosCacheHandler {
    private static final Map<UUID, PosCache> playerPosCache = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        playerPosCache.put(event.getEntity().getUUID(), new PosCache());
    }

    @SubscribeEvent
    public static void onPlayerLeaveServer(PlayerEvent.PlayerLoggedOutEvent event) {
        playerPosCache.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void updatePlayerPosCache(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            playerPosCache.forEach((playerUUID, posCache) -> {
                Player player = event.getServer().getPlayerList().getPlayer(playerUUID);
                if (player == null) {
                    playerPosCache.remove(playerUUID);
                } else {
                    posCache.update(player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PosCache posCache = playerPosCache.get(event.getEntity().getUUID());
        if (posCache != null) {
            posCache.clear();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PosCache posCache = playerPosCache.get(event.getEntity().getUUID());
        if (posCache != null) {
            posCache.clear();
        }
    }

    public static AABB getPlayerAABB(Player player, int delay, float inflate) {
        if (delay == -1) {
            return player.getBoundingBox().inflate(inflate);
        }
        PosCache posCache = playerPosCache.get(player.getUUID());
        return posCache == null ? null :  posCache.getBoundingBox(delay, player, inflate);
    }
}
