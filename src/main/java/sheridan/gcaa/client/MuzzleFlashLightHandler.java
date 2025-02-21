package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public class MuzzleFlashLightHandler {
    private static Level level;
    private static Player player;
    private static final AtomicBoolean work = new AtomicBoolean(false);
    private static long timeStamp = 0;
    private static final int DELAY = 35;
    private static boolean firstPersonLightOverride = false;
    private static boolean overrideLight = false;
    private static boolean check = false;
    private static long packBlockPos = 0;
    private static boolean lightUpdateReceived = false;

    public static void onClientShoot(ItemStack itemStack, IGun gun, Player player) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return;
        }
        if (Gun.MUZZLE_STATE_SUPPRESSOR.equals(gun.getMuzzleFlash(itemStack))) {
            firstPersonLightOverride = true;
            return;
        }
        if (timeStamp != 0 && System.currentTimeMillis() - timeStamp < DELAY) {
            return;
        }
        work.set(true);
        level = player.level();
        MuzzleFlashLightHandler.player = player;
    }

    public static boolean isFirstPersonLightOverride() {
        return firstPersonLightOverride;
    }

    public static boolean isOverrideLight() {
        return overrideLight;
    }

    public static void lightUpdateReceived() {
        lightUpdateReceived = true;
    }

    public static long getPackBlockPos() {
        return packBlockPos;
    }
    private static final Set<Long> tempPos = new HashSet<>();
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (player == null || level == null) {
                return;
            }
            long now = System.currentTimeMillis();
            if (work.get()) {
                if (!check) {
                    BlockPos above = player.getOnPos().above((int) player.getEyeHeight() + 1);
                    BlockState blockState = player.level().getBlockState(above);
                    long pos = above.asLong();
                    if (!tempPos.contains(pos) && blockState.getFluidState().isEmpty() && blockState.getLightEmission() < 10) {
                        packBlockPos = pos;
                        level.getLightEngine().checkBlock(above);
                        check = true;
                        timeStamp = now;
                        tempPos.add(pos);
                        overrideLight = true;
                    }
                }
            }
            if (work.get() && !lightUpdateReceived && timeStamp != now) {
                firstPersonLightOverride = true;
            }
            if (timeStamp != 0 && now - timeStamp > DELAY) {
                firstPersonLightOverride = false;
                timeStamp = 0;
                work.set(false);
                check = false;
                overrideLight = false;
                packBlockPos = 0;
                for (Long pos : tempPos) {
                    level.getLightEngine().checkBlock(BlockPos.of(pos));
                }
                lightUpdateReceived = false;
                tempPos.clear();
            }
        }
    }

}
