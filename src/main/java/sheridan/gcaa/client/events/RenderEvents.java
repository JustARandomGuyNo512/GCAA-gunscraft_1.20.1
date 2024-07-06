package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.client.render.gui.crosshair.CrossHairRenderer;
import sheridan.gcaa.items.guns.IGun;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onRenderHandFP(RenderHandEvent event) {
        ClientWeaponStatus status = Clients.mainHandStatus;
        status.equipProgress = event.getEquipProgress();
        if (Clients.mainHandStatus.holdingGun.get()) {
            if (Minecraft.getInstance().options.bobView().get()) {
                GlobalWeaponBobbing.INSTANCE.update(event.getPartialTick(), status.equipProgress);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderCrossHair(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            if (Clients.holdingGun()) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player != null) {
                    ItemStack stack = player.getMainHandItem();
                    if (stack.getItem() instanceof IGun gun && !gun.isSniper()) {
                        CrossHairRenderer.INSTANCE.render(gun, event.getGuiGraphics(), player, stack);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
