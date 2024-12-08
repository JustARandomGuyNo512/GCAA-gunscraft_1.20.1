package sheridan.gcaa.client.render.fx.muzzleSmoke;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class MuzzleSmokeTask {
    public PoseStack poseStack;
    public long lastShoot;
    public MuzzleSmoke effect;

    public MuzzleSmokeTask(PoseStack poseStack, long lastShoot, MuzzleSmoke effect)  {
        this.poseStack = poseStack;
        this.lastShoot = lastShoot;
        this.effect = effect;
    }

    public boolean handleRender(MultiBufferSource bufferSource) {
        boolean finished = isFinished();
        if (!finished) {
//            if (!Float.isNaN(zDepthModify)) {
//                poseStack.last().pose().set(3, 2, RenderAndMathUtils.getZByPrevProjectionMat(zDepthModify));
//            }
            effect.render(lastShoot, poseStack, bufferSource);
        }
        return finished;
    }

    private boolean isFinished() {
        return System.currentTimeMillis() - lastShoot > effect.length;
    }
}
