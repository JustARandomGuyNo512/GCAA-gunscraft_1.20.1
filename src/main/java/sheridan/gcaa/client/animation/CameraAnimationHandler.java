package sheridan.gcaa.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import org.joml.Quaternionf;
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

    public void mix(float yaw, float pitch, float roll)  {
        if (yaw != 0 || pitch != 0 || roll != 0) {
            dirty = true;
        } else {
            return;
        }
        this.yaw += yaw;
        this.pitch += pitch;
        this.roll += roll;
    }


    public void apply(ViewportEvent.ComputeCameraAngles event) {
        if (dirty) {
            event.setYaw((float) Math.toDegrees(yaw) + event.getYaw());
            event.setPitch((float) Math.toDegrees(pitch) + event.getPitch());
            event.setRoll((float) Math.toDegrees(roll) + event.getRoll());
        }
    }

    public void applyToPose(PoseStack poseStack) {
        if (dirty) {
            poseStack.mulPose(new Quaternionf().rotateXYZ(pitch, yaw, roll));
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clear() {
        yaw = 0;
        pitch = 0;
        roll = 0;
        dirty = false;
    }

}
