package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.attachments.functional.GP_25Model;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GrenadeLauncherReloadPacket;

@OnlyIn(Dist.CLIENT)
public class GrenadeLauncherReloadTask implements IReloadTask {
    public static final int CUSTOM_PAYLOAD = 241;
    private final int length;
    private final AnimationDefinition gunReloadAnimation;
    private final AnimationDefinition attachmentReloadAnimation;
    private final ItemStack itemStack;
    private final IGun gun;
    private int tick = 0;
    private boolean completed = false;
    private final String attachmentId;

    public GrenadeLauncherReloadTask(String attachmentId, int length, AnimationDefinition gunReloadAnimation,
                                     AnimationDefinition attachmentReloadAnimation, IGun gun, ItemStack itemStack) {
        this.length = length;
        this.gunReloadAnimation = gunReloadAnimation;
        this.attachmentReloadAnimation = attachmentReloadAnimation;
        this.gun = gun;
        this.itemStack = itemStack;
        this.attachmentId = attachmentId;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void tick(Player clientPlayer) {
        if (tick >= length) {
            PlayerStatusProvider.setReloading(clientPlayer, false);
            PacketHandler.simpleChannel.sendToServer(new GrenadeLauncherReloadPacket(attachmentId));
            GrenadeLauncher.reload(attachmentId, itemStack, gun, clientPlayer);
            completed = true;
            return;
        }
        tick++;
    }

    @Override
    public ItemStack getStack() {
        return itemStack;
    }

    @Override
    public int getCustomPayload() {
        return CUSTOM_PAYLOAD;
    }

    @Override
    public void onBreak() {
        onCancel();
    }

    @Override
    public void onCancel() {
        PlayerStatusProvider.setReloading(Minecraft.getInstance().player, false);
        AnimationHandler.INSTANCE.clearReload();
        AnimationHandler.INSTANCE.clearAnimation(GP_25Model.RELOAD_ANIMATION_KEY);
    }

    @Override
    public void start() {
        AnimationHandler.INSTANCE.startReload(gunReloadAnimation);
        AnimationHandler.INSTANCE.startAnimation(GP_25Model.RELOAD_ANIMATION_KEY, attachmentReloadAnimation, false, false);
        Clients.mainHandStatus.ads = false;
        HandActionHandler.INSTANCE.breakTask();
    }

    @Override
    public float getProgress() {
        return length == 0 ? 0 : tick / (float) length;
    }
}
