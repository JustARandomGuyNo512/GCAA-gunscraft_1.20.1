package sheridan.gcaa;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.model.guns.G19Model;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.ModItems;

import static sheridan.gcaa.client.render.DisplayData.DataType.POS;
import static sheridan.gcaa.client.render.DisplayData.DataType.ROT;
import static sheridan.gcaa.client.render.DisplayData.DataType.SCALE;

public class Clients {

    @OnlyIn(Dist.CLIENT)
    public static void onSetUp(final FMLClientSetupEvent event) {
        GunModelRegistry.registerModel(ModItems.G19.get(), new G19Model());
        GunModelRegistry.registerTransform(ModItems.G19.get(), new DisplayData()
                .setFirstPersonMain(0f, 0f, 0, POS).setFirstPersonMain(0.5f, 0.5f, 0.5f, SCALE)
                .setFirstPersonRight(0.5f, 0.5f, 0.5f, POS).setFirstPersonRight(0.5f, 0.5f, 0.5f, ROT).setFirstPersonRight(0.5f, 0.5f, 0.5f, SCALE)
                .setFirstPersonLeft(0.5f, 0.5f, 0.5f, POS).setFirstPersonLeft(0.5f, 0.5f, 0.5f, ROT).setFirstPersonLeft(0.5f, 0.5f, 0.5f, SCALE)
                .setThirdPersonLeft(0f, 0f, 0, POS).setThirdPersonLeft(0.5f, 0.5f, 0.5f, SCALE)
                .setThirdPersonRight(0f, 0f, 0, POS).setThirdPersonRight(0.5f, 0.5f, 0.5f, SCALE)
                .setGround(0f, 0f, 0, POS).setGround(0.5f, 0.5f, 0.5f, SCALE)
                .setFrame(0f, 0f, 0, POS).setFrame(0f, 0f, 0, ROT).setFrame(0.5f, 0.5f, 0.5f, SCALE)
        );

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
