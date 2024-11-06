package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.AnimationSequence;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.Mark;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.propertyExtensions.AutoShotgunExtension;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunReloadPacket;

@OnlyIn(Dist.CLIENT)
public class AutoShotgunReloadTask extends SingleReloadTask{
    private final int chamberReloadLength;
    private final int triggerChamberReloadDelay;
    private boolean isEmptyReload = false;

    public AutoShotgunReloadTask(ItemStack itemStack, IGun gun, int reloadNum, AutoShotgunExtension extension) {
        super(itemStack, gun, extension.enterDelay, extension.singleReloadLength, extension.enterDelay, reloadNum, extension.triggerReloadDelay);
        this.chamberReloadLength = extension.chamberReloadLength;
        this.triggerChamberReloadDelay = extension.triggerChamberReloadDelay;
        if (gun.getAmmoLeft(itemStack) == 0) {
            length = enterDelay + singleReloadLength * (reloadNum - 1) + chamberReloadLength + exitLength;
            isEmptyReload = true;
        }
        this.trySetHandActionTask = false;
    }

    @Override
    public void tick(Player clientPlayer) {
        if (tick == 0) {
            reloadNum = Math.min(reloadNum, AmmunitionHandler.getAmmunitionCount(itemStack, gun, clientPlayer));
        }
        if (isEmptyReload) {
            int reloadingTick = tick - enterDelay - chamberReloadLength - (reloaded - 1) * singleReloadLength;
            if (reloaded < reloadNum) {
                boolean triggerReload = reloaded == 0 ? reloadingTick == triggerChamberReloadDelay : reloadingTick == triggerReloadDelay;
                if (triggerReload) {
                    PacketHandler.simpleChannel.sendToServer(new GunReloadPacket());
                    gun.reload(itemStack, clientPlayer);
                    reloaded ++;
                }
            }
            if (tick >= length) {
                PlayerStatusProvider.setReloading(clientPlayer, false);
                completed = true;
                return;
            }
            tick++;
        } else {
            super.tick(clientPlayer);
        }
    }

    @Override
    public void start() {
        if (model != null) {
            if (isEmptyReload) {
                AnimationDefinition enter = model.hasAnimation("enter_reload_empty") ? model.get("enter_reload_empty") : model.get("enter_reload");
                AnimationDefinition reloadChamber = model.get("reload_chamber");
                AnimationDefinition single = model.hasAnimation("reload_single_empty") ? model.get("reload_single_empty") : model.get("reload_single");
                AnimationDefinition exit = model.hasAnimation("exit_reload_empty") ? model.get("exit_reload_empty") : model.get("exit_reload");
                AnimationSequence sequence = new AnimationSequence()
                        .append(new Mark(enter))
                        .append(new Mark(reloadChamber).enableSound(true).soundOnServer(true))
                        .append(new Mark(single).setLoopTimes(reloadNum - 1).enableSound(true).soundOnServer(true))
                        .append(new Mark(exit));
                AnimationHandler.INSTANCE.startReload(sequence);
            } else {
                super.start();
            }
        }
        Clients.MAIN_HAND_STATUS.ads = false;
        HandActionHandler.INSTANCE.breakTask();
    }
}
