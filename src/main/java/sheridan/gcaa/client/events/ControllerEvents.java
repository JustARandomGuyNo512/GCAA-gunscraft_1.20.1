package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.screens.GunDebugAdjustScreen;
import sheridan.gcaa.items.guns.IGun;


@Mod.EventBusSubscriber(Dist.CLIENT)
public class ControllerEvents {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
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
