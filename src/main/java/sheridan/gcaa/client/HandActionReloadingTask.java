package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.DoneHandActionPacket;

@OnlyIn(Dist.CLIENT)
public class HandActionReloadingTask extends ReloadTask {
    private final boolean needHandAction;
    public HandActionReloadingTask(ItemStack itemStack, HandActionGun gun) {
        super(itemStack, gun);
        needHandAction = gun.needHandAction(itemStack);
        length = gun.getReloadLength(itemStack, needHandAction);
    }

    @Override
    public void tick(Player clientPlayer) {
        if (tick >= length && needHandAction) {
            ((HandActionGun) gun).setNeedHandAction(itemStack, false);
            PacketHandler.simpleChannel.sendToServer(new DoneHandActionPacket());
        }
        super.tick(clientPlayer);
    }

    @Override
    public void start() {
        if (model != null && !isGenericReloading()) {
            AnimationHandler.INSTANCE.startReload(needHandAction ? model.getFullReload() : model.getReload());
        }
        Clients.mainHandStatus.ads = false;
        HandActionHandler.INSTANCE.breakTask();
    }
}
