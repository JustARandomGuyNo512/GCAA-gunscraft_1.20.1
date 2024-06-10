package sheridan.gcaa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.model.guns.G19Model;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.items.ModItems;

public class Clients {

    public static void onSetUp(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        GunModelRegistry.register(ModItems.G19.get(), new G19Model());
    }



    public static void updateClientPlayerStatus(int id, long lastShootRight, long lastShootLeft, long lastChamber, boolean reloading) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            Entity entity = clientLevel.getEntity(id);
            if (entity instanceof Player player) {
                player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
                    cap.setLastShootRight(lastShootRight);
                    cap.setLastShootLeft(lastShootLeft);
                    cap.setReloading(reloading);
                    cap.setLastChamberAction(lastChamber);
                    cap.dataChanged = false;
                });
            }
        }
    }
}
