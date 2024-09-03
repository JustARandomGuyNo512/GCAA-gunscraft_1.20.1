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
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunReloadPacket;

@OnlyIn(Dist.CLIENT)
public class SingleReloadTask extends ReloadingTask{
    public int enterDelay;
    public int singleReloadLength;
    public int exitLength;
    public int reloadNum;
    public int reloaded;
    public boolean trySetHandActionTask;

    public SingleReloadTask(ItemStack itemStack, IGun gun, int enterDelay, int singleReloadLength, int exitLength, int reloadNum) {
        this(itemStack, gun, enterDelay, singleReloadLength, exitLength, reloadNum, true);
    }

    public SingleReloadTask(ItemStack itemStack, IGun gun, int enterDelay, int singleReloadLength, int exitLength,
                            int reloadNum, boolean trySetHandActionTask) {
        super(itemStack, gun);
        this.enterDelay = enterDelay;
        this.singleReloadLength = singleReloadLength;
        this.exitLength = exitLength;
        this.reloadNum = reloadNum;
        this.trySetHandActionTask = trySetHandActionTask;
        length = enterDelay + singleReloadLength * reloadNum + exitLength;
        tick = 0;
    }

    @Override
    public void tick(Player clientPlayer) {
        if ((tick - enterDelay) % singleReloadLength == 0 && reloaded < reloadNum) {
            PacketHandler.simpleChannel.sendToServer(new GunReloadPacket());
            reloaded ++;
        }
        if (tick >= length) {
            PlayerStatusProvider.setReloading(clientPlayer, false);
            completed = true;
            if (trySetHandActionTask && gun instanceof HandActionGun handActionGun && handActionGun.needHandAction(itemStack)) {
                IHandActionTask task = handActionGun.getHandActionTask(itemStack, true);
                HandActionHandler.INSTANCE.setHandActionTask(task);
            }
            return;
        }
        tick++;
    }

    @Override
    public boolean restrictNBT() {
        return false;
    }

    @Override
    public void start() {
        if (model != null) {
            AnimationDefinition enter = model.get("enter_reload");
            AnimationDefinition single = model.get("reload_single");
            AnimationDefinition exit = model.get("exit_reload");
            AnimationSequence sequence = new AnimationSequence()
                    .append(new KeyframeAnimations.Mark(enter).stopAtLastFrame())
                    .append(new KeyframeAnimations.Mark(single).setLoopTimes(reloadNum).enableSound(true).soundOnServer(true))
                    .append(new KeyframeAnimations.Mark(exit).stopAtLastFrame()).finishBuild();
            AnimationHandler.INSTANCE.startReload(sequence);
        }
        Clients.mainHandStatus.ads = false;
        HandActionHandler.INSTANCE.breakTask();
    }
}
