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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public class MuzzleFlashLightHandler {
    private static Level level;
    private static final Map<BlockPos, BlockState> temp = new ConcurrentHashMap<>();
    private static final AtomicBoolean hasWork = new AtomicBoolean();

    public static void onClientShoot(ItemStack itemStack, IGun gun, Player player) {
        if (Gun.MUZZLE_STATE_SUPPRESSOR.equals(gun.getMuzzleFlash(itemStack))) {
            return;
        }
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                level = player.level();
                BlockPos above = player.getOnPos().above((int) player.getEyeHeight());
                BlockState blockState = player.level().getBlockState(above);
                if (blockState.isAir() && level.getLightEmission(above) < 10) {
                    temp.put(above, blockState);
                    level.setBlock(above, ModBlocks.AIR_LIGHT_BLOCK.get().defaultBlockState(), 1);
                    hasWork.set(true);
                }
            }
        });
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (hasWork.get() && event.phase == TickEvent.Phase.END && level != null) {
            Set<BlockPos> remove = new HashSet<>();
            for (Map.Entry<BlockPos, BlockState> entry : temp.entrySet()) {
                level.setBlock(entry.getKey(), entry.getValue(), 1);
                remove.add(entry.getKey());
            }
            temp.keySet().removeAll(remove);
            hasWork.set(false);
        }
    }

}
