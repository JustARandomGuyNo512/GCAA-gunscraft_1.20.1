package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.attachments.IAnimatedModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.functional.GP_25Model;
import sheridan.gcaa.client.model.attachments.functional.GrenadeLauncherModel;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GrenadeLauncherReloadPacket;

@OnlyIn(Dist.CLIENT)
public class GrenadeLauncherReloadTask implements IReloadTask {
    public static final int CUSTOM_PAYLOAD = 241;
    private final int length;
    private final ItemStack itemStack;
    private final IGun gun;
    private int tick = 0;
    private boolean completed = false;
    private final String attachmentId;

    public GrenadeLauncherReloadTask(String attachmentId, int length, IGun gun, ItemStack itemStack) {
        this.length = length;
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
        AnimationHandler.INSTANCE.clearAnimation(GrenadeLauncherModel.RELOAD_ANIMATION_KEY);
    }

    @Override
    public void start() {
        IAttachmentModel model = AttachmentsRegister.getModel(attachmentId);
        if (model instanceof IAnimatedModel animatedModel) {
            AnimationHandler.INSTANCE.startReload(animatedModel.getAnimation("gun_reload"));
            AnimationHandler.INSTANCE.startAnimation(GrenadeLauncherModel.RELOAD_ANIMATION_KEY, animatedModel.getAnimation("attachment_reload"), false, false);
        }
        Clients.MAIN_HAND_STATUS.ads = false;
        HandActionHandler.INSTANCE.breakTask();
    }

    @Override
    public float getProgress() {
        return length == 0 ? 0 : tick / (float) length;
    }
}
