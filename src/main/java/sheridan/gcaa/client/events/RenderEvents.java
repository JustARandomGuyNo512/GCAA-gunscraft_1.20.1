package sheridan.gcaa.client.events;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.client.render.gui.crosshair.CrossHairRenderer;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.items.guns.IGunFireMode;

import java.awt.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onRenderHandFP(RenderHandEvent event) {
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            ClientWeaponStatus status = Clients.mainHandStatus;
            status.equipProgress = event.getEquipProgress();
            if (Clients.mainHandStatus.holdingGun.get()) {
                if (Minecraft.getInstance().options.bobView().get()) {
                    GlobalWeaponBobbing.INSTANCE.update(event.getPartialTick(), status.equipProgress);
                }
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
                        CrossHairRenderer.INSTANCE.render(0, 16, gun, event.getGuiGraphics(), player, stack, event.getWindow(), event.getPartialTick());
                        event.setCanceled(true);
                    }
                    if (Clients.mainHandStatus.ads.get()) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleCameraAnimation(ViewportEvent.ComputeCameraAngles event) {
        CameraAnimationHandler.INSTANCE.apply(event);
        CameraAnimationHandler.INSTANCE.clear();
    }

    @SubscribeEvent
    public static void renderGunInfo(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                Font font = minecraft.font;
                GuiGraphics guiGraphics = event.getGuiGraphics();
                Window window = Minecraft.getInstance().getWindow();
                int width = window.getGuiScaledWidth();
                int height = window.getGuiScaledHeight();
                renderAmmoCounter(stack, guiGraphics, gun, font, width, height);
                IGunFireMode gunFireMode = gun.getFireMode(stack);
                if (gunFireMode != null) {
                    Component tooltipName = gunFireMode.getTooltipName();
                    if (tooltipName != null) {
                        guiGraphics.drawString(font, tooltipName.getString(), 0.8f * width, 0.85f * height, -1,  true);
                    }
                }
            }
        }
    }

    private static void renderAmmoCounter(ItemStack stack, GuiGraphics guiGraphics, IGun gun, Font font, int width, int height)  {
        int magSize = gun.getMagSize(stack);
        int ammoLeft = gun.getAmmoLeft(stack);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(0, 1, 0, 1);
        float leftRate = ammoLeft / (float) magSize;
        if (leftRate < 0.5f) {
            guiGraphics.setColor(1, 1, 0, 1);
            if (leftRate < 0.21f) {
                guiGraphics.setColor(1, 0, 0, 1);
            }
        }
        guiGraphics.drawString(font, ammoLeft + "", 0.8f * width, 0.8f * height, -1,  true);
        guiGraphics.setColor(1, 1, 1, 1);
        guiGraphics.drawString(font, " / " + magSize, 0.8f * width + font.width(ammoLeft + ""), 0.8f * height, -1,  true);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
//        if (Clients.debugKeyDown) {
//            if (event.getItemStack().getItem() instanceof IGun) {
                //System.out.println(event.getToolTip().toString());
                //System.out.println("===================================");
           //}
        //}
    }
}
