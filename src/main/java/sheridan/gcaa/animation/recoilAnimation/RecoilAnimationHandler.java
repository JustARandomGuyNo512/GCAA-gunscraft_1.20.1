package sheridan.gcaa.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class RecoilAnimationHandler {
    private final Deque<KeyframeAnimations.Mark> recoils = new ArrayDeque<>();
    private static final Timer timer = new Timer();
    public static final InertialRecoilHandler INERTIAL_RECOIL_HANDLER = new InertialRecoilHandler();
    public static final RecoilAnimationHandler INSTANCE = new RecoilAnimationHandler();
    private static final int MAX_LEN = 12;

    protected RecoilAnimationHandler() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                INERTIAL_RECOIL_HANDLER.update();
            }
        }, 0, 10);
    }

    public void onShoot(AnimationDefinition recoilAnimation, long shootTime) {
        if (recoils.size() < MAX_LEN) {
            recoils.add(new KeyframeAnimations.Mark(recoilAnimation, shootTime));
        } else {
            recoils.poll();
            recoils.add(new KeyframeAnimations.Mark(recoilAnimation, shootTime));
        }
    }

    public void onShoot(InertialRecoilData inertialRecoilData, float randomDirectionX, float randomDirectionY) {
        INERTIAL_RECOIL_HANDLER.onShoot(inertialRecoilData, randomDirectionX, randomDirectionY);
    }

    public void onShoot(AnimationDefinition recoilAnimation, InertialRecoilData inertialRecoilData) {

    }

    public void handleRecoil(HierarchicalModel<?> root) {
        for (KeyframeAnimations.Mark mark : recoils) {
            if (!KeyframeAnimations.checkIfOutOfTime(mark.timeStamp, 0, mark.animationDefinition)) {
                KeyframeAnimations.animate(root, mark.animationDefinition, mark.timeStamp, 0, 1, KeyframeAnimations.DEFAULT_DIRECTION);
            } else {
                recoils.remove(mark);
            }
        }
    }

    public void handleInertialRecoil(PoseStack poseStack, InertialRecoilData data) {
        INERTIAL_RECOIL_HANDLER.applyTransform(poseStack, data.id, false);
    }

    public void update() {

    }
}
