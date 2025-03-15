package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpringRecoilHandler {
    public static final SpringRecoilHandler INSTANCE = new SpringRecoilHandler();
    public static RecoilData test = new RecoilData(
            new SteadyStateSpring("10", "0.7", "2", "8"),
            new ClampedSpring("10", "0.3", "0.5", "5", "10", "2.5"),
            new MassDampingSpring("10", "0.1", "0.5", "0.5"),
            new MassDampingSpring("10", "0.5", "1", "1"),
            new ClampedSpring("10", "0.5", "1.2", "1.2", "2", "2"),
            "1.5", "0.12", "0.01", "0.03", "0.15"
    );


    public void apply(PoseStack poseStack) {
        test.apply(poseStack);
    }

    public void onShoot(float randomDX, float randomDY) {
        test.onShoot(randomDX, randomDY, 1, 1);
    }

    public void update() {
        test.update();
    }
}
