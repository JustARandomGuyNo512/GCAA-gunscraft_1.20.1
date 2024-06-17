package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.guns.IGun;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvents {

    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (player != null && player.level().isClientSide()) {
                ItemStack stackMain = player.getMainHandItem();
                ItemStack stackOff = player.getOffhandItem();
                IGun gunMain = stackMain.getItem() instanceof IGun ? (IGun) stackMain.getItem() : null;
                IGun gunOff = stackOff.getItem() instanceof IGun ? (IGun) stackOff.getItem() : null;
                Clients.setHandPose(getHandPos(stackOff, gunMain, gunOff));
                updateClientHoldingGun(gunMain, gunOff);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static DisplayData.HandPos getHandPos(ItemStack stackOff, IGun gunMain, IGun gunOff) {
        Clients.mainHandStatus.holdingGun.set(gunMain != null);
        DisplayData.HandPos pos = DisplayData.HandPos.MAIN_HAND_RIFLE;
        if (gunMain != null && gunOff == null) {
            if (gunMain.canHoldInOneHand() && stackOff.getItem() != Items.AIR) {
                pos = DisplayData.HandPos.RIGHT_PISTOL;
            }
        }
        if (gunMain == null && gunOff != null) {
            if (gunOff.canHoldInOneHand()) {
                pos = DisplayData.HandPos.LEFT_PISTOL;
            }
        }
        if (gunMain != null && gunOff != null) {
            if (gunMain.canHoldInOneHand() && gunOff.canHoldInOneHand()) {
                pos = DisplayData.HandPos.DOUBLE_PISTOL;
            } else if (gunMain.canHoldInOneHand() && !gunOff.canHoldInOneHand()) {
                pos = DisplayData.HandPos.RIGHT_PISTOL;
            }
        }
        if (gunMain == null && gunOff == null) {
            pos = DisplayData.HandPos.NONE;
        }
        return pos;
    }

    private static void updateClientHoldingGun(IGun gunMain, IGun gunOff) {
        Clients.mainHandStatus.holdingGun.set(gunMain != null);
        Clients.offHandStatus.holdingGun.set(gunOff != null);
        if (gunOff != null) {
            if (gunMain != null && !gunMain.canHoldInOneHand()) {
                Clients.offHandStatus.holdingGun.set(false);
            }
            if (!gunOff.canHoldInOneHand()) {
                Clients.offHandStatus.holdingGun.set(false);
            }
        }
    }
}
