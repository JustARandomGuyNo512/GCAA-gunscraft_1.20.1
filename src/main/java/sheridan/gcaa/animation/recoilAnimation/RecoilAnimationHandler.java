package sheridan.gcaa.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.animation.frameAnimation.KeyframeAnimations;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@OnlyIn(Dist.CLIENT)
public class RecoilAnimationHandler {
    private final List<KeyframeAnimations.Mark> recoilAnimationList = new ArrayList<>();
    private static final Timer timer = new Timer();
    public static final InertialRecoilHandler INERTIAL_RECOIL_HANDLER = new InertialRecoilHandler();
    public static final RecoilAnimationHandler INSTANCE = new RecoilAnimationHandler();
    private static final int MAX_LEN = 12;

    protected RecoilAnimationHandler() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                INERTIAL_RECOIL_HANDLER.update(0.001f);
            }
        }, 0, 10);
    }

    public void onShoot(AnimationDefinition recoilAnimation) {

    }

    public void onShoot(InertialRecoilData inertialRecoilData) {

    }

    public void onShoot(AnimationDefinition recoilAnimation, InertialRecoilData inertialRecoilData) {

    }

    public void handleTranslation(PoseStack poseStack) {

    }

    public void update() {

    }
}
