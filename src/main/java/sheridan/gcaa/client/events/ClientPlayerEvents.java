package sheridan.gcaa.client.events;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
                updatePlayerSpread(stackMain, gunMain, player);
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

    private static void updatePlayerSpread(ItemStack stack, IGun gun, Player player) {
        if (gun != null) {
            float[] spread = gun.getSpread(stack);
            float minSpread = spread[0];
            float maxSpread = spread[1];
            if (player.isCrouching()) {
                minSpread *= 0.75f;
                maxSpread *= 0.75f;
            }
            if (player.xxa != 0 || player.yya != 0 || player.zza != 0) {
                float spreadFactor = player.isSprinting() ? gun.getSprintingSpreadFactor(stack) : gun.getWalkingSpreadFactor(stack);
                minSpread *= spreadFactor;
                maxSpread *= spreadFactor;
            }
            float fallFactor = player.fallDistance < 10 ? player.fallDistance * 0.15f : 1.5f;
            minSpread += fallFactor;
            maxSpread += fallFactor;
            Clients.mainHandStatus.spread = Mth.clamp(Clients.mainHandStatus.spread - gun.getSpreadRecover(stack), minSpread, maxSpread);
        } else {
            if (Clients.mainHandStatus.spread != 0) {
                Clients.mainHandStatus.spread = Math.max(Clients.mainHandStatus.spread - 0.1f, 0);
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
