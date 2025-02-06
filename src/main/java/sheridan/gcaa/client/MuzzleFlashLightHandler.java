package sheridan.gcaa.client;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.items.gun.IGun;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public class MuzzleFlashLightHandler {
    public static final AtomicBoolean OVERRIDE_EMISSION = new AtomicBoolean(false);

    public static void onClientShoot(IGun gun, ItemStack itemStack, Player player) {

    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {

    }

}
