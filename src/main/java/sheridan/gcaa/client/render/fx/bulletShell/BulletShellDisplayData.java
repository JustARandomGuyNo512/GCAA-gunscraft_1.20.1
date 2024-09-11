package sheridan.gcaa.client.render.fx.bulletShell;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BulletShellDisplayData {
    public float[] pos = new float[] {0, 0, 0};
    public float[] scale = new float[] {1, 1, 1};
    public Vector3f velocity = new Vector3f(1, 1, 1);
    public float dropRate = 0.1f;
    public float rotateSpeed = 1f;
    public final String type;
    public int maxDisplayTime = 1000;

    public BulletShellDisplayData(float x, float y, float z, String type) {
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
        this.type = type;
    }

    public BulletShellDisplayData setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
        return this;
    }

    public BulletShellDisplayData setScale(float scale) {
        return setScale(scale, scale, scale);
    }

    public BulletShellDisplayData setVelocity(float x, float y, float z) {
        velocity = new Vector3f(x, y, z);
        return this;
    }

    public BulletShellDisplayData setDropRate(float rate) {
        dropRate = rate;
        return this;
    }

    public BulletShellDisplayData setRotateSpeed(float speed) {
        rotateSpeed = speed;
        return this;
    }

    public BulletShellDisplayData setMaxDisplayTime(int time) {
        maxDisplayTime = time;
        return this;
    }
}
