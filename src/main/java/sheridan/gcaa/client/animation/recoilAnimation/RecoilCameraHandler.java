package sheridan.gcaa.client.animation.recoilAnimation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.guns.IGun;

import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class RecoilCameraHandler {
    public static final RecoilCameraHandler INSTANCE = new RecoilCameraHandler();
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private Player player;
    private float pitchSpeed;
    private float yawSpeed;
    private float pitchControl;
    private float yawControl;

    public void onShoot(IGun gun, ItemStack itemStack, float dx) {
        float pitchVec = gun.getRecoilPitch(itemStack);
        float yawVec = gun.getRecoilYaw(itemStack) * dx;
        if (pitchVec > 0) {
            pitchSpeed = pitchVec;
            pitchControl = gun.getRecoilPitchControl(itemStack) * 0.2f;
            enabled.set(true);
        }
        if (Math.abs(yawVec) != 0) {
            yawSpeed = yawVec;
            yawControl = gun.getRecoilYawControl(itemStack) * 0.2f;
            enabled.set(true);
        }
    }

    public void handle() {
        if (player != null && player.getId() == Clients.clientPlayerId && enabled.get()) {
            pitchSpeed -= pitchControl;
            yawSpeed = yawSpeed > 0 ? yawSpeed - yawControl : yawSpeed + yawControl;
            pitchSpeed *= Mth.clamp(1 - pitchControl * 5f, 0.8f, 0.9f);
            yawSpeed *= Mth.clamp(1 - yawControl * 5f, 0.8f, 0.9f);
            player.setXRot(player.getXRot() - pitchSpeed * 0.2f);
            player.setYRot(player.getYRot() + yawSpeed * 0.2f);
            if ((pitchSpeed < 0.25f && Math.abs(yawSpeed) < 0.25f) || pitchSpeed < 0) {
                clear();
            }
        } else {
            player = Minecraft.getInstance().player;
        }
    }

    public void clear() {
        pitchSpeed = 0;
        yawSpeed = 0;
        pitchControl = 0;
        yawControl = 0;
        enabled.set(false);
    }

}
