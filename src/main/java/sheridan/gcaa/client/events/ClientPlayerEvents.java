package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.JumpBobbingHandler;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.IGun;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvents {

    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player == null || !player.level().isClientSide || player != Minecraft.getInstance().player) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            Clients.clientPlayerId = player.getId();
            ItemStack stackMain = player.getMainHandItem();
            IGun gunMain = stackMain.getItem() instanceof IGun ? (IGun) stackMain.getItem() : null;
            Clients.mainHandStatus.holdingGun.set(gunMain != null);
            if (gunMain != null) {
                Clients.mainHandStatus.fireDelay.set(gunMain.getFireDelay(stackMain));
                Clients.mainHandStatus.adsSpeed = Math.min(gunMain.getAdsSpeed(stackMain) * 0.05f, 0.25f);
                Clients.mainHandStatus.attachmentsStatus.checkAndUpdate(stackMain, gunMain, player);
            }
            Clients.mainHandStatus.updatePlayerSpread(stackMain, gunMain, player);
            Clients.mainHandStatus.handleAds(stackMain, gunMain, player);
        }
        if (event.phase == TickEvent.Phase.END )  {
            JumpBobbingHandler jumpBobbingHandler = JumpBobbingHandler.getInstance();
            if (jumpBobbingHandler != null) {
                jumpBobbingHandler.handle((LocalPlayer) player);
            }
            ReloadingHandler.INSTANCE.tick(player);
            HandActionHandler.INSTANCE.tick(player);
            if (Clients.debugKeyDown) {
                AnimationDefinition definition = GunModelRegister.getModel(ModItems.AKM.get()).get("reload");
                definition.print();
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void playerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getId() == Clients.clientPlayerId) {
                Clients.mainHandStatus.spread += 0.5f;
            }
        }
    }

}
