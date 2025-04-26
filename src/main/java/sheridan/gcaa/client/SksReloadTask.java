package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.AnimationSequence;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.Mark;
import sheridan.gcaa.items.gun.IGun;

public class SksReloadTask extends SingleReloadTask{
    private final boolean emptyReload;
    private boolean tryShoot;
    public SksReloadTask(ItemStack itemStack, IGun gun, int enterDelay, int singleReloadLength, int exitLength, int reloadNum, int triggerReloadDelay) {
        super(itemStack, gun, enterDelay, singleReloadLength, exitLength, reloadNum, triggerReloadDelay);
        emptyReload = gun.getAmmoLeft(itemStack) == 0;
        tryShoot = false;
    }

    @Override
    public void tick(Player clientPlayer) {
        if (tryShoot) {
            if (tick >= length) {
                completed = true;
                if (adsOnFinished) {
                    Clients.MAIN_HAND_STATUS.ads = true;
                }
            }
            tick ++;
            return;
        }
        super.tick(clientPlayer);
    }

    @Override
    public void start() {
        if (model != null && !isGenericReloading()) {
            AnimationDefinition enter = model.get(emptyReload ? "enter_reload_empty" : "enter_reload");
            AnimationDefinition single = model.get(emptyReload ? "reload_empty" : "reload");
            AnimationDefinition exit = model.get(emptyReload ? "exit_reload_empty" : "exit_reload");
            AnimationSequence sequence = new AnimationSequence()
                    .append(new Mark(enter).enableSound(true).soundOnServer(true))
                    .append(new Mark(single).setLoopTimes(reloadNum).enableSound(true).soundOnServer(true))
                    .append(new Mark(exit).enableSound(true).soundOnServer(true));
            AnimationHandler.INSTANCE.startReload(sequence);
        }
        if (Clients.MAIN_HAND_STATUS.ads) {
            adsOnFinished = true;
        }
        Clients.MAIN_HAND_STATUS.ads = false;
        HandActionHandler.INSTANCE.breakTask();
    }

    @Override
    public void onMouseButton(int btn, int action) {
        super.onMouseButton(btn, action);
        if (tryShoot) {
            return;
        }
        if (btn == 0 && action == 1) {
            tryShoot = true;
            if (model != null && !isGenericReloading()) {
                AnimationDefinition exit = model.get(emptyReload ? "exit_reload_empty" : "exit_reload");
                AnimationHandler.INSTANCE.startReload(exit);
            }
            length = exitLength;
            tick = 0;
        }
    }

}
