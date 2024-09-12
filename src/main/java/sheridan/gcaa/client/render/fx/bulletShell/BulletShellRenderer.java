package sheridan.gcaa.client.render.fx.bulletShell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.client.model.BulletShellModel;
import sheridan.gcaa.client.render.GunRenderContext;

import java.util.ArrayDeque;
import java.util.Deque;

@OnlyIn(Dist.CLIENT)
public class BulletShellRenderer {
    public static final int MAX_NUM = 20;
    private static final Deque<Task> bulletShells = new ArrayDeque<>(MAX_NUM);

    private static class Task {
        public BulletShellDisplayData displayData;
        public long timeStamp;
        public float[] randomSeeds;

        public Task(BulletShellDisplayData displayData, long timeStamp) {
            this.displayData = displayData;
            this.timeStamp = timeStamp;
            Vector3f random = new Vector3f(
                    (float) (Math.random() * 2 - 1),
                    (float) (Math.random() * 2 - 1),
                    (float) (Math.random() * 2 - 1)).normalize().mul(displayData.randomRate);
            randomSeeds = new float[] {
                    (1 + random.x),
                    (1 + random.y),
                    (1 + random.z),
                    1 - (float) Math.random() * displayData.randomRate
            };
        }

        public boolean outOfTime() {
            return System.currentTimeMillis() - timeStamp > displayData.maxDisplayTime;
        }

        public void render(PoseStack poseStack, GunRenderContext context, VertexConsumer globalVertexConsumer) {
            boolean finished = outOfTime();
            if (!finished) {
                poseStack.pushPose();
                displayData.render(timeStamp, poseStack, context, globalVertexConsumer, randomSeeds);
                poseStack.popPose();
            }
        }
    }

    public static void push(BulletShellDisplayData bulletShellDisplayData, long timeStamp) {
        if (bulletShells.size() >= MAX_NUM) {
            bulletShells.removeFirst();
        }
        bulletShells.push(new Task(bulletShellDisplayData, timeStamp));
    }

    public static void update() {
        if (bulletShells.isEmpty()) {
            return;
        }
        bulletShells.removeIf(Task::outOfTime);
    }

    public static void render(GunRenderContext context) {
        if (bulletShells.isEmpty()) {
            return;
        }
        VertexConsumer vertexConsumer = BulletShellModel.getVertexConsumer(context.bufferSource);
        Object obj = context.getLocalSaved(GunRenderContext.ORIGINAL_GUN_VIEW_POSE_FP);
        PoseStack poseStack = obj == null ? context.poseStack : (PoseStack) obj;
        for (Task task : bulletShells) {
            task.render(poseStack, context, vertexConsumer);
        }
    }

    public static void clear() {
        bulletShells.clear();
    }
}
