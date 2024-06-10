package sheridan.gcaa.client.render.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
@OnlyIn(Dist.CLIENT)
public class Quaternions {
    public static Quaternionf rotateDeg(Quaternionf quaternionf, float rx, float ry, float rz) {
        quaternionf.rotationXYZ(
                (float) Math.toRadians(rx),
                (float) Math.toRadians(ry),
                (float) Math.toRadians(rz)
        );
        return quaternionf;
    }

    public static Quaternionf rotateDeg(float rx, float ry, float rz) {
        Quaternionf quaternionf = new Quaternionf();
        return rotateDeg(quaternionf, rx, ry, rz);
    }

    public static Quaternionf rotateDeg(Vector3f vector3f) {
        Quaternionf quaternionf = new Quaternionf();
        return rotateDeg(quaternionf, vector3f.x, vector3f.y, vector3f.z);
    }
}
