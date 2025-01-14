package sheridan.gcaa.client.render.fx.muzzleSmoke;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MuzzleSmokeTask {
    public PoseStack poseStack;
    public long lastShoot;
    public MuzzleSmoke effect;
    private final int randomSeed;
    private final int light;

    public MuzzleSmokeTask(PoseStack poseStack, long lastShoot, MuzzleSmoke effect, int light)  {
        this.poseStack = poseStack;
        this.lastShoot = lastShoot;
        this.effect = effect;
        this.randomSeed = (int) (Math.random() * 1000);
        this.light = light;
    }

    public boolean handleRender(MultiBufferSource bufferSource) {
        boolean finished = isFinished();
        if (!finished) {
            effect.render(lastShoot, poseStack, bufferSource, randomSeed, light);
        }
        return finished;
    }

    private boolean isFinished() {
        return System.currentTimeMillis() - lastShoot > effect.length;
    }
}
