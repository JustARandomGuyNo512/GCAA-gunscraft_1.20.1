package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunReloadPacket;

@OnlyIn(Dist.CLIENT)
public class ReloadingTask implements IReloadingTask{
    public int length;
    public int tick;
    public int ammoLeft;
    public ItemStack itemStack;
    public IGun gun;
    public IGunModel model;
    public boolean completed;

    public ReloadingTask(ItemStack itemStack, IGun gun) {
        this.itemStack = itemStack;
        this.gun = gun;
        model = GunModelRegister.getModel(gun);
        tick = 0;
        ammoLeft = gun.getAmmoLeft(itemStack);
        length = gun.getReloadLength(itemStack, ammoLeft == 0);
        completed = false;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void tick(Player clientPlayer) {
        if (tick >= length) {
            PlayerStatusProvider.setReloading(clientPlayer, false);
            PacketHandler.simpleChannel.sendToServer(new GunReloadPacket());
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
        return 0;
    }

    @Override
    public void onBreak() {
       onCancel();
    }

    @Override
    public void onCancel() {
        PlayerStatusProvider.setReloading(Minecraft.getInstance().player, false);
        AnimationHandler.INSTANCE.clearReload();
        System.out.println("canceling");
    }

    @Override
    public void start() {
        if (model != null) {
            AnimationHandler.INSTANCE.startReload(ammoLeft == 0 ? model.getFullReload() : model.getReload());
        }
        Clients.mainHandStatus.ads = false;
    }

}
