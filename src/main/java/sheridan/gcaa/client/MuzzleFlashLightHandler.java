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
import sheridan.gcaa.blocks.ModBlocks;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public class MuzzleFlashLightHandler {
    private static Level level;
    private static Player player;
    private static final Map<BlockPos, BlockState> temp = new HashMap<>();
    private static final AtomicBoolean work = new AtomicBoolean(false);
    private static long timeStamp = 0;
    private static final int DELAY = 35;
    private static boolean firstPersonLightOverride = false;

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

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (player == null || level == null) {
                return;
            }
            if (work.get()) {
                BlockPos above = player.getOnPos().above((int) player.getEyeHeight() + 1);
                BlockState blockState = player.level().getBlockState(above);
                if (blockState.getFluidState().isEmpty() && !temp.containsKey(above) && blockState.getLightEmission() < 10) {
                    temp.put(above, blockState);
                    boolean added = level.setBlock(above, ModBlocks.AIR_LIGHT_BLOCK.get().defaultBlockState(), 1);
                    if (added) {
                        level.getLightEngine().checkBlock(above);
                        firstPersonLightOverride = false;
                    } else {
                        firstPersonLightOverride = true;

                    }
                    timeStamp = System.currentTimeMillis();
                }
                work.set(false);
            } else if (timeStamp != 0 && System.currentTimeMillis() - timeStamp > DELAY) {
                for (Map.Entry<BlockPos, BlockState> entry : temp.entrySet()) {
                    BlockPos key = entry.getKey();
                    if (level.getBlockState(key).getFluidState().isEmpty()) {
                        level.setBlock(key, entry.getValue(), 1);
                        level.getLightEngine().checkBlock(key);
                    }
                }
                firstPersonLightOverride = false;
                temp.clear();
                timeStamp = 0;
            }
        }
    }

}
