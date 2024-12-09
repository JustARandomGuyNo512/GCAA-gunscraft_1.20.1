package sheridan.gcaa.client.render.fx.muzzleFlash;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.client.render.fx.muzzleSmoke.CommonMuzzleSmokeEffects;
import sheridan.gcaa.client.render.fx.muzzleSmoke.MuzzleSmoke;
import sheridan.gcaa.client.render.fx.muzzleSmoke.MuzzleSmokeRenderer;

import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class MuzzleFlash {
    private static final Random RANDOM = new Random();
    private final List<MuzzleFlashTexture> textures;
    private boolean randomRotate;
    private float rotation;
    private int rotateSeed;
    private MuzzleSmoke muzzleSmoke = CommonMuzzleSmokeEffects.COMMON;


    public MuzzleFlash(List<MuzzleFlashTexture> textures, boolean randomRotate, int rotateSeed) {
        this(textures);
        if (rotateSeed > 0) {
            this.randomRotate = randomRotate;
            this.rotateSeed = rotateSeed;
            this.rotation = (float) Math.toRadians(360f / rotateSeed);
        }
    }

    /**
     * create a muzzle flash without random rotation
     * */
    public MuzzleFlash(List<MuzzleFlashTexture> textures) {
        this.textures = textures;
        this.randomRotate = false;
        this.rotateSeed = 0;
        this.rotation = 0;
    }

    public MuzzleFlash resetMuzzleSmoke(MuzzleSmoke muzzleSmoke) {
        this.muzzleSmoke = muzzleSmoke;
        return this;
    }

    public MuzzleFlash noMuzzleSmoke() {
        this.muzzleSmoke = null;
        return this;
    }

    public boolean hasMuzzleSmoke() {
        return muzzleSmoke != null;
    }

    public MuzzleSmoke getMuzzleSmoke() {
        return muzzleSmoke;
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, MuzzleFlashDisplayData displayData, float scale, long startTime, boolean isFirstPerson) {
        if (displayData != null && !textures.isEmpty()) {
            boolean muzzleFlashNotEnded = (System.currentTimeMillis() - startTime) <= displayData.length;
            boolean hasSmokeEffect = MuzzleSmokeRenderer.INSTANCE.hasTask();
            if (muzzleFlashNotEnded || hasSmokeEffect) {
                stack.pushPose();
                displayData.applyTrans(stack, scale);
                MuzzleSmokeRenderer.INSTANCE.renderOrPushEffect(bufferSource, muzzleSmoke, stack, startTime);
                if (muzzleFlashNotEnded) {
                    int texNum = textures.size();
                    int texIndex = texNum > 1 ? RANDOM.nextInt(texNum) : 0;
                    MuzzleFlashTexture muzzleFlashTexture = textures.get(texIndex);
                    if (randomRotate) {
                        int seed = Math.max(0, RANDOM.nextInt(6)) % rotateSeed;
                        if (seed != 0) {
                            stack.mulPose(new Quaternionf().rotateXYZ(0,0,seed * rotation));
                        }
                    }
                    int index = RANDOM.nextInt(muzzleFlashTexture.getCount());
                    muzzleFlashTexture.render(index, stack, bufferSource, isFirstPerson);
                }
                stack.popPose();
            }
        }
    }
}
