package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.Sniper;
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
            if (adsOnFinished) {
                Clients.MAIN_HAND_STATUS.ads = true;
            }
        }
        super.tick(clientPlayer);
    }

    @Override
    public void start() {
        if (model != null && !isGenericReloading()) {
            if (gun instanceof Sniper) {
                AnimationDefinition definition;
                if (needHandAction) {
                    definition = gun.getAmmoLeft(itemStack) == 0 ?
                            model.getFullReload() : model.get("reload_bolt_action");
                    if (definition == null) {
                        definition = model.getFullReload();
                    }
                } else {
                    definition = model.getReload();
                }
                AnimationHandler.INSTANCE.startReload(definition);
            } else {
                AnimationHandler.INSTANCE.startReload(needHandAction ?
                        model.getFullReload() :
                        model.getReload());
            }
        }
        if (Clients.MAIN_HAND_STATUS.ads) {
            adsOnFinished = true;
        }
        Clients.MAIN_HAND_STATUS.ads = false;
        HandActionHandler.INSTANCE.breakTask();
    }
}
