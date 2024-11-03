package sheridan.gcaa.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunReloadPacket;

@OnlyIn(Dist.CLIENT)
public class ReloadTask implements IReloadTask {
    public int length;
    public int tick;
    public int ammoLeft;
    public ItemStack itemStack;
    public IGun gun;
    public IGunModel model;
    public boolean completed;
    private boolean isGenericReloading = false;

    public ReloadTask(ItemStack itemStack, IGun gun) {
        this.itemStack = itemStack;
        this.gun = gun;
        model = GunModelRegister.getModel(gun);
        tick = 0;
        ammoLeft = gun.getAmmoLeft(itemStack);
        length = gun.getReloadLength(itemStack, ammoLeft == 0);
        completed = false;
        Player player = Minecraft.getInstance().player;
        if (gun.canUseWithShield() && player != null && player.getOffhandItem().getItem() instanceof ShieldItem) {
            isGenericReloading = true;
        }
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
            gun.reload(itemStack, clientPlayer);
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
    }

    @Override
    public boolean isGenericReloading() {
        return isGenericReloading;
    }

    @Override
    public void start() {
        if (model != null && !isGenericReloading) {
            AnimationHandler.INSTANCE.startReload(ammoLeft == 0 ? model.getFullReload() : model.getReload());
        }
        Clients.mainHandStatus.ads = false;
        HandActionHandler.INSTANCE.breakTask();
        if (isGenericReloading) {
            Clients.setEquipDelay(length);
            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(1), false);
        }
    }

    @Override
    public float getProgress() {
        return length == 0 ? 0 : tick / (float) length;
    }

}
