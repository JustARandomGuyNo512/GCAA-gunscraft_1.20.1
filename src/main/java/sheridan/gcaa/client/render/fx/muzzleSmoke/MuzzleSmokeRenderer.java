package sheridan.gcaa.client.render.fx.muzzleSmoke;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.ArrayDeque;
import java.util.Deque;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class MuzzleSmokeRenderer {
    private static final BufferBuilder DELAYED_TASK_BUFFER = new BufferBuilder(1024);
    private static final Deque<MuzzleSmokeTask> tasks = new ArrayDeque<>();
    public static final int MAX_DELAYED_TASKS = 5;
    public static final MuzzleSmokeRenderer INSTANCE = new MuzzleSmokeRenderer();
    private boolean isTaskQueueOpen = false;
    private boolean renderImmediate = true;

    /**
     * Only call this method on render thread!!!
     * */
    public void openTaskQueue() {
        this.isTaskQueueOpen = true;
    }

    /**
     * Only call this method on render thread!!!
     * */
    public void renderOrPushEffect(MultiBufferSource bufferSource, MuzzleSmoke effect, PoseStack poseStack, long lastShoot)  {
        if (effect == null) {
            return;
        }
        renderImmediate = Clients.currentStage == RenderLevelStageEvent.Stage.AFTER_LEVEL;
        if (isTaskQueueOpen) {
            if (tasks.size() > MAX_DELAYED_TASKS) {
                tasks.pollLast();
            }
            if (tasks.size() < MAX_DELAYED_TASKS) {
                PoseStack poseStack1 = RenderAndMathUtils.copyPoseStack(poseStack);
                poseStack1.translate(0, 0, -0.005f);
                tasks.offerFirst(new MuzzleSmokeTask(poseStack1, lastShoot, effect));
            }
            isTaskQueueOpen = false;
        }
        if (renderImmediate) {
            tasks.removeIf((task) -> task.handleRender(bufferSource));
        }
    }

    public void clearEffects() {
        tasks.clear();
    }

    public boolean hasTask() {
        return !tasks.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!INSTANCE.renderImmediate && !tasks.isEmpty() &&
                event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(DELAYED_TASK_BUFFER);
            tasks.removeIf((task) -> task.handleRender(bufferSource));
            bufferSource.endBatch();
        }
    }

}
