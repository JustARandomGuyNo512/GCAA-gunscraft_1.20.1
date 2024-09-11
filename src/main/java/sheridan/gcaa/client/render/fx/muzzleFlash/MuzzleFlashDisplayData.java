package sheridan.gcaa.client.render.fx.muzzleFlash;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class MuzzleFlashDisplayData {
    public int length = 30;
    public float[] translate = null;
    public float[] scale = new float[]{1,1,1};
    public float[] rotate = null;

    @Deprecated
    public MuzzleFlashDisplayData setDefaultTranslate(float[] translate) {
        this.translate = new float[] {translate[0], translate[1], translate[2]};
        return this;
    }

    public MuzzleFlashDisplayData setDefaultTranslate(float x, float y, float z) {
        this.translate = new float[] {-x / 16f, -y / 16f, z / 16f};
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
    public MuzzleFlashDisplayData setDefaultRotate(float[] rotate) {
        this.rotate = new float[] {rotate[0], rotate[1], rotate[2]};
        return this;
    }

    public MuzzleFlashDisplayData setDefaultRotate(float x, float y, float z) {
        this.rotate = new float[] {
                (float) Math.toRadians(x),
                (float) Math.toRadians(y),
                (float) Math.toRadians(z)};
        return this;
    }

    public MuzzleFlashDisplayData setLength(int length) {
        this.length = length;
        return this;
    }

    public void applyTrans(PoseStack stack, float size) {
        if (translate != null  && (translate[0] != 0 || translate[1] != 0 || translate[2] != 0)) {
            stack.translate(translate[0],
                    translate[1],
                    translate[2]);
        }
        if (rotate != null && (rotate[0] != 0 || rotate[1] != 0 || rotate[2] != 0)) {
            stack.mulPose(new Quaternionf().rotateXYZ(rotate[0], rotate[1], rotate[2]));
        }
        if (size != 1 || (scale[0] != 1 || scale[1] != 1 || scale[2] != 1)) {
            stack.scale(scale[0] * size, scale[1] * size, scale[2] * size);
        }
    }
}
