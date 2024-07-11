package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class RecoilAnimationHandler {
    private final Deque<KeyframeAnimations.Mark> recoils = new ArrayDeque<>();
    private static final Timer timer = new Timer();
    public static final InertialRecoilHandler INERTIAL_RECOIL_HANDLER = new InertialRecoilHandler();
    public static final RecoilAnimationHandler INSTANCE = new RecoilAnimationHandler();
    private static final int MAX_KEYFRAME_ANIMATION_LEN = 12;
    private static final AtomicBoolean enableInertialRecoil = new AtomicBoolean(true);

    protected RecoilAnimationHandler() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (enableInertialRecoil.get()) {
                        INERTIAL_RECOIL_HANDLER.update();
                    } else {
                        INERTIAL_RECOIL_HANDLER.clear();
                    }
                    RecoilCameraHandler.INSTANCE.handle();
                } catch (Exception ignore) {}
            }
        }, 0, 10L);
    }

    /**
     * push keyframe animation recoil
     * */
    public void onShoot(AnimationDefinition recoilAnimation, long shootTime) {
        if (recoils.size() < MAX_KEYFRAME_ANIMATION_LEN) {
            recoils.add(new KeyframeAnimations.Mark(recoilAnimation, shootTime));
        } else {
            recoils.poll();
            recoils.add(new KeyframeAnimations.Mark(recoilAnimation, shootTime));
        }
    }

    /**
     * push inertial recoil
     * */
    public void onShoot(InertialRecoilData inertialRecoilData, float randomDirectionX, float randomDirectionY) {
        INERTIAL_RECOIL_HANDLER.onShoot(inertialRecoilData, randomDirectionX, randomDirectionY);
    }

    /**
     * apply keyframe recoil animation to a model
     * */
    public void handleRecoil(HierarchicalModel<?> root) {
        for (KeyframeAnimations.Mark mark : recoils) {
            if (!KeyframeAnimations.checkIfOutOfTime(mark.timeStamp, 0, mark.animationDefinition)) {
                KeyframeAnimations.animate(root, mark.animationDefinition, mark.timeStamp, mark.scales);
            } else {
                recoils.remove(mark);
            }
        }
    }

    /**
     * apply inertial recoil transform to a pose stack
     * */
    public void handleInertialRecoil(PoseStack poseStack, InertialRecoilData data) {
        INERTIAL_RECOIL_HANDLER.applyTransform(poseStack, data.id, false);
    }

    /**
     * false to stop inertial recoil handler update work and clear data
     * */
    public void setEnableInertialRecoil(boolean enable) {
        enableInertialRecoil.set(enable);
    }

}
