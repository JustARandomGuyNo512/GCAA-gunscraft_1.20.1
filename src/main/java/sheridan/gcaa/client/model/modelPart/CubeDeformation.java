package sheridan.gcaa.client.model.modelPart;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CubeDeformation {
    public static final CubeDeformation NONE = new CubeDeformation(0.0F);
    final float growX;
    final float growY;
    final float growZ;

    public CubeDeformation(float x, float y, float z) {
        this.growX = x;
        this.growY = y;
        this.growZ = z;
    }

    public CubeDeformation(float size) {
        this(size, size, size);
    }

    public CubeDeformation extend(float extend) {
        return new CubeDeformation(this.growX + extend, this.growY + extend, this.growZ + extend);
    }

    public CubeDeformation extend(float extendX, float extendY, float extendZ) {
        return new CubeDeformation(this.growX + extendX, this.growY + extendY, this.growZ + extendZ);
    }
}