package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class RecoilData {
    public SteadyStateSpring back;
    public ClampedSpring rot;
    public MassDampingSpring randomX;
    public MassDampingSpring randomY;
    public MassDampingSpring randomZ;
    public Param recoilBack, recoilRot, recoilRandomX, recoilRandomY, recoilRandomZ;
    protected long lastFire = 0;
    protected int zRotIndex = 1;

    public RecoilData(SteadyStateSpring back, ClampedSpring rot, MassDampingSpring randomX, MassDampingSpring randomY, ClampedSpring randomZ,
                      String recoilBack, String recoilRot, String recoilRandomX, String recoilRandomY, String recoilRandomZ) {
        this.back = back;
        this.rot = rot;
        this.randomX = randomX;
        this.randomY = randomY;
        this.randomZ = randomZ;
        this.recoilBack = Param.of(recoilBack);
        this.recoilRot = Param.of(recoilRot);
        this.recoilRandomX = Param.of(recoilRandomX);
        this.recoilRandomY = Param.of(recoilRandomY);
        this.recoilRandomZ = Param.of(recoilRandomZ);
    }

    public void update() {
        back.update();
        rot.update();
        randomX.update();
        randomY.update();
        randomZ.update();
    }

    public void onShoot(float randomDirectionX, float randomDirectionY, float pRate,  float yRate) {
        back.applyImpulse(recoilBack.val());
        rot.applyImpulse(recoilRot.val());
        randomX.applyImpulse(recoilRandomX.val() * randomDirectionY);
        randomY.applyImpulse(recoilRandomY.val() * randomDirectionX);
        randomZ.clear();
        randomZ.applyImpulse(recoilRandomZ.val() * zRotIndex * Math.random());
        zRotIndex *= -1;
    }


    public void apply(PoseStack poseStack) {
        //poseStack.translate(0,0, back.getPosition() * 0.5f);
        poseStack.mulPose(new Quaternionf().rotateXYZ(
                (float) (- rot.getPosition() - randomY.getPosition()),
                (float) randomX.getPosition(),
                (float) randomZ.getPosition()
        ));
        poseStack.translate(0,0, back.getPosition());
    }


}
