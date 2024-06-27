package sheridan.gcaa.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.animation.frameAnimation.KeyframeAnimations;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RecoilAnimationHandler {
    public static final RecoilAnimationHandler INSTANCE = new RecoilAnimationHandler();
    private final List<KeyframeAnimations.Mark> recoilAnimationList = new ArrayList<>();
    private static final int MAX_LEN = 12;

    public void onShoot() {

    }

    public void handleTranslation(PoseStack poseStack) {

    }

    public void update() {

    }
}
