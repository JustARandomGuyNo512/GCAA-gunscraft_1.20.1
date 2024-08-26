package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
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

    public HandActionTask(ItemStack itemStack, HandActionGun gun, int startDelay, int length, String handActionAnimationName){
        this.itemStack = itemStack;
        this.length = length + startDelay;
        this.startDelay = startDelay;
        this.handActionAnimationName = handActionAnimationName;
        this.gun = gun;
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
        if (tick == length) {
            gun.setNeedHandAction(itemStack, gun.getAmmoLeft(itemStack) == 0);
            PacketHandler.simpleChannel.sendToServer(new DoneHandActionPacket());
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
    public void start() {

    }
}