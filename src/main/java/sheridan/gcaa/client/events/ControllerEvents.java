package sheridan.gcaa.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.screens.GunDebugAdjustScreen;
import sheridan.gcaa.items.guns.IGun;


@Mod.EventBusSubscriber(Dist.CLIENT)
public class ControllerEvents {

    @SubscribeEvent
    public static void mouseEvent(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().isWindowActive() && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().screen == null) {
            Player player = Minecraft.getInstance().player;
            if (player != null && !player.isSpectator() && !player.isSwimming()) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof IGun) {
                    if (event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT) {
                        if (event.getAction() == 1) {
                            Clients.mainHandStatus.buttonDown.set(true);
                        } else if (event.getAction() == 0) {
                            Clients.mainHandStatus.buttonDown.set(false);
                        }
                        event.setCanceled(true);
                    } else if (event.getButton() == InputConstants.MOUSE_BUTTON_LEFT) {
                        Clients.mainHandStatus.ads = event.getAction() == 1;
                        event.setCanceled(true);
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void onButtonPress(InputEvent.Key event) {
        if(Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            ItemStack stackMain = player.getMainHandItem();
            if (KeyBinds.OPEN_DEBUG_SCREEN.isDown() && event.getAction() == 1) {
                if (stackMain.getItem() instanceof IGun) {
                    Minecraft.getInstance().setScreen(new GunDebugAdjustScreen());
                }
            }
        }
    }
}