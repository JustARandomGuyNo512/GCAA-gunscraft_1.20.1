package sheridan.gcaa.client.events;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.client.render.JumpBobbingHandler;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.sounds.ModSounds;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvents {

    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Player player = event.player;
            if (player != null && player.level().isClientSide()) {
                Clients.clientPlayerId = player.getId();
                ItemStack stackMain = player.getMainHandItem();
                IGun gunMain = stackMain.getItem() instanceof IGun ? (IGun) stackMain.getItem() : null;
                Clients.mainHandStatus.holdingGun.set(gunMain != null);
                if (gunMain != null) {
                    Clients.mainHandStatus.fireDelay.set(gunMain.getFireDelay(stackMain));
                }
            }
        }
        if (event.phase == TickEvent.Phase.END && event.player != null && event.player.level().isClientSide())  {
            JumpBobbingHandler jumpBobbingHandler = JumpBobbingHandler.getInstance();
            if (jumpBobbingHandler != null) {
                jumpBobbingHandler.handle((LocalPlayer) event.player);
            }
            ReloadingHandler.INSTANCE.tick();
            if (Clients.debugKeyDown) {
                AnimationDefinition definition = GunModelRegistry.getModel(ModItems.AKM.get()).get("reload");
                definition.print();
            }
        }
    }

}
