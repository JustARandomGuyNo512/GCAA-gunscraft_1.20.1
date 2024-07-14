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

    public void set(ModelPart cameraPosePart) {
        yaw += cameraPosePart.yRot;
        pitch += cameraPosePart.xRot;
        roll += cameraPosePart.zRot;
    }

    public void apply(ViewportEvent.ComputeCameraAngles event) {
        event.setYaw(yaw + event.getYaw());
        event.setPitch(pitch + event.getPitch());
        event.setRoll(roll + event.getRoll());
    }

    public void clear() {
        yaw = 0;
        pitch = 0;
        roll = 0;
    }

}
