package sheridan.gcaa.client.render.fx.muzzleFlash;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class MuzzleFlash {
    private static final Random RANDOM = new Random();
    private final List<MuzzleFlashTexture> textures;
    private boolean randomRotate;
    private float rotation;
    private int rotateSeed;


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

    public void render(PoseStack stack, MultiBufferSource bufferSource, MuzzleFlashDisplayData displayData, float scale, long startTime, boolean isFirstPerson) {
        if (displayData != null && (System.currentTimeMillis() - startTime) <= displayData.length && !textures.isEmpty()) {
            int texNum = textures.size();
            int texIndex = texNum > 1 ? Math.abs(RANDOM.nextInt()) % texNum : 0;
            MuzzleFlashTexture muzzleFlashTexture = textures.get(texIndex);
            displayData.applyTrans(stack, scale);
            boolean popStack = false;
            if (randomRotate) {
                int seed = Math.max(0, RANDOM.nextInt()) % rotateSeed;
                if (seed != 0) {
                    stack.pushPose();
                    popStack = true;
                    stack.mulPose(new Quaternionf().rotateXYZ(0,0,seed * rotation));
                }
            }
            int index = Math.abs(RANDOM.nextInt()) % muzzleFlashTexture.getCount();
            muzzleFlashTexture.render(index, stack, bufferSource, isFirstPerson);
            if (popStack) {
                stack.popPose();
            }
        }
    }
}
