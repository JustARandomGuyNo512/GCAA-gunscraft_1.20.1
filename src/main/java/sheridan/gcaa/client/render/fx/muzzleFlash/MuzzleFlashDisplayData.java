package sheridan.gcaa.client.render.fx.muzzleFlash;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class MuzzleFlashDisplayData {
    public int length = 30;
    public float[] translate = new float[]{0,0,0};
    public float[] scale = new float[]{1,1,1};
    public float[] rotate = new float[]{0,0,0};

    @Deprecated
    public MuzzleFlashDisplayData setTranslate(float[] translate) {
        this.translate = translate;
        return this;
    }

    public MuzzleFlashDisplayData setTranslate(float x, float y, float z) {
        this.translate[0] = -x / 16f;
        this.translate[1] = -y / 16f;
        this.translate[2] = z / 16f;
        return this;
    }


    public MuzzleFlashDisplayData setScale(float scale) {
        this.scale[0] = scale;
        this.scale[1] = scale;
        this.scale[2] = scale;
        return this;
    }

    public MuzzleFlashDisplayData setScale(float x, float y, float z) {
        this.scale[0] = x;
        this.scale[1] = y;
        this.scale[2] = z;
        return this;
    }

    @Deprecated
    public MuzzleFlashDisplayData setMulPose(float[] rotate) {
        this.rotate = rotate;
        return this;
    }

    public MuzzleFlashDisplayData setMulPose(float x, float y, float z) {
        this.rotate[0] = (float) Math.toRadians(x);
        this.rotate[1] = (float) Math.toRadians(y);
        this.rotate[2] = (float) Math.toRadians(z);
        return this;
    }

    public MuzzleFlashDisplayData setLength(int length) {
        this.length = length;
        return this;
    }

    public void applyTrans(PoseStack stack, float size) {
        stack.translate(translate[0],
                translate[1],
                translate[2]);
        stack.mulPose(new Quaternionf().rotateXYZ(rotate[0], rotate[1], rotate[2]));
        stack.scale(scale[0] * size, scale[1] * size, scale[2] * size);
    }
}
