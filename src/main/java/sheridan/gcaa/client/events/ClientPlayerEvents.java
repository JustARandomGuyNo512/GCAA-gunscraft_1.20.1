package sheridan.gcaa.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.SprintingHandler;
import sheridan.gcaa.client.render.JumpBobbingHandler;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.gun.IGun;

import java.util.Objects;

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
            boolean lastTickHoldingGun = Clients.mainHandStatus.holdingGun.get();
            Clients.mainHandStatus.holdingGun.set(gunMain != null);
            String itemIdentity = Clients.mainHandStatus.identity;
            if (gunMain != null) {
                Clients.mainHandStatus.fireDelay.set(gunMain.getFireDelay(stackMain));
                Clients.mainHandStatus.adsSpeed = Math.min(gunMain.getAdsSpeed(stackMain) * 0.05f, 0.25f);
                Clients.mainHandStatus.attachmentsStatus.checkAndUpdate(stackMain, gunMain, player);
                Clients.mainHandStatus.weapon.set(stackMain);
                Clients.mainHandStatus.identity = gunMain.getGun().getIdentity(stackMain);
            } else {
                Clients.mainHandStatus.weapon.set(ItemStack.EMPTY);
                Clients.mainHandStatus.identity = "";
            }
            if (!Objects.equals(itemIdentity, Clients.mainHandStatus.identity) && (lastTickHoldingGun || gunMain != null)) {
                if (gunMain != null) {
                    gunMain.getGun().shouldCauseReequipAnimation(ItemStack.EMPTY, stackMain, true);
                }
                KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(0), false);
                KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(1), false);
                Clients.mainHandStatus.buttonDown.set(false);
            }
            Clients.mainHandStatus.updatePlayerSpread(stackMain, gunMain, player);
            Clients.mainHandStatus.updateChargeTick(stackMain, gunMain);
            Clients.mainHandStatus.handleAds(stackMain, gunMain, player);
            if (Clients.mainHandStatus.attachmentsStatus.getEffectiveSight() instanceof Scope scope) {
                scope.handleMouseSensitivity();
            }
            SprintingHandler.INSTANCE.tick((LocalPlayer) player);
        }
        if (event.phase == TickEvent.Phase.END)  {
            JumpBobbingHandler jumpBobbingHandler = JumpBobbingHandler.getInstance();
            if (jumpBobbingHandler != null) {
                jumpBobbingHandler.handle((LocalPlayer) player);
            }
            ReloadingHandler.INSTANCE.tick(player);
            HandActionHandler.INSTANCE.tick(player);
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
