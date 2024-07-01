package sheridan.gcaa.client.events;

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
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.SwitchFireModePacket;


@Mod.EventBusSubscriber(Dist.CLIENT)
public class ControllerEvents {

    @SubscribeEvent
    public static void mouseEvent(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().isWindowActive() && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().screen == null) {
            Player player = Minecraft.getInstance().player;
            if (player != null && !player.isSpectator() && !player.isSwimming()) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof IGun) {
                    if (event.getButton() == 0) {
                        if (event.getAction() == 1) {
                            Clients.mainHandStatus.buttonDown.set(true);
                        } else if (event.getAction() == 0) {
                            Clients.mainHandStatus.buttonDown.set(false);
                        }
                        event.setCanceled(true);
                    } else if (event.getButton() == 1) {
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
            } else if (KeyBinds.SWITCH_FIRE_MODE.isDown() && event.getAction() == 1) {
                if (stackMain.getItem() instanceof IGun) {
                    Clients.mainHandStatus.buttonDown.set(false);
                    PacketHandler.simpleChannel.sendToServer(new SwitchFireModePacket());
                }
            }
        }
    }
}
