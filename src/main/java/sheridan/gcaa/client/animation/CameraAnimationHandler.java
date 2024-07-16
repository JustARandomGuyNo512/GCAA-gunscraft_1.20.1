package sheridan.gcaa.client.animation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import sheridan.gcaa.client.model.modelPart.ModelPart;

@OnlyIn(Dist.CLIENT)
public class CameraAnimationHandler {
    public static final CameraAnimationHandler INSTANCE = new CameraAnimationHandler();
    public float yaw;
    public float pitch;
    public float roll;
    private boolean dirty = false;

    public void set(ModelPart cameraPosePart) {
        yaw = cameraPosePart.yRot;
        pitch = cameraPosePart.xRot;
        roll = cameraPosePart.zRot;
        if (yaw != 0 || pitch != 0 || roll != 0) {
            dirty = true;
        }
    }

    public void mix(ModelPart cameraPosePart) {
        yaw += cameraPosePart.yRot;
        pitch += cameraPosePart.xRot;
        roll += cameraPosePart.zRot;
        if (yaw != 0 || pitch != 0 || roll != 0) {
            dirty = true;
        }
    }

    public void apply(ViewportEvent.ComputeCameraAngles event) {
        if (dirty) {
            event.setYaw((float) Math.toDegrees(yaw) + event.getYaw());
            event.setPitch((float) Math.toDegrees(pitch) + event.getPitch());
            event.setRoll((float) Math.toDegrees(roll) + event.getRoll());
        }
    }

    public void clear() {
        yaw = 0;
        pitch = 0;
        roll = 0;
        dirty = false;
    }

}
