package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellDisplayData;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellRenderer;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.DoneHandActionPacket;

@OnlyIn(Dist.CLIENT)
public class HandActionTask implements IHandActionTask{
    private final ItemStack itemStack;
    private final HandActionGun gun;
    private int tick = 0;
    private final int startDelay;
    private final int length;
    private final String handActionAnimationName;
    public int throwBulletShellDelay;

    public HandActionTask(ItemStack itemStack, HandActionGun gun, int startDelay, int length, String handActionAnimationName, int throwBulletShellDelay){
        this.itemStack = itemStack;
        this.length = length + startDelay;
        this.startDelay = startDelay;
        this.handActionAnimationName = handActionAnimationName;
        this.gun = gun;
        this.throwBulletShellDelay = throwBulletShellDelay;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void tick(Player clientPlayer) {
        if (tick == startDelay) {
            IGunModel model = GunModelRegister.getModel((IGun) (itemStack.getItem()));
            if (model != null){
                AnimationDefinition definition = model.get(handActionAnimationName);
                if (definition != null){
                    AnimationHandler.INSTANCE.startHandAction(definition);
                }
            }
        }
        if (startDelay != 0 && tick == throwBulletShellDelay + startDelay) {
            DisplayData displayData = GunModelRegister.getDisplayData(gun);
            BulletShellDisplayData bulletShellDisplayData = displayData.getBulletShellDisplayData();
            if (bulletShellDisplayData != null) {
                BulletShellRenderer.push(bulletShellDisplayData, System.currentTimeMillis());
            }
        }
        if (tick == length) {
            if (gun.getAmmoLeft(itemStack) > 0) {
                PacketHandler.simpleChannel.sendToServer(new DoneHandActionPacket());
                gun.setNeedHandAction(itemStack, false);
            }
        }
        tick++;
    }

    @Override
    public boolean isCompleted() {
        return tick > length;
    }

    @Override
    public void stop() {
        AnimationHandler.INSTANCE.clearHandAction();
    }

    @Override
    public void start() {}
}
