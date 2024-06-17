package sheridan.gcaa.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
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
                IGun[] guns = Clients.getGuns(player);
                IGun gunMain = guns[0];
                IGun gunOff = guns[1];
                if (gunMain != null || gunOff != null) {
                    if (event.getButton() == 0) {//left click
                        if (event.getAction() == InputConstants.PRESS) {
                            System.out.println("left down");
                        } else if (event.getAction() == InputConstants.RELEASE) {
                            System.out.println("left up");
                        }
                    } else if (event.getButton() == 1) {//right click
                        if (event.getAction() == InputConstants.PRESS) {
                            System.out.println("right down");
                        } else if (event.getAction() == InputConstants.RELEASE) {
                            System.out.println("right up");
                        }
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
            ItemStack stackOff = player.getOffhandItem();
            if (KeyBinds.OPEN_DEBUG_SCREEN.isDown() && event.getAction() == 1) {
                System.out.println("open debug screen");
                if (stackMain.getItem() instanceof IGun || stackOff.getItem() instanceof IGun) {
                    Minecraft.getInstance().setScreen(new GunDebugAdjustScreen());
                }
            }
        }
    }
}
