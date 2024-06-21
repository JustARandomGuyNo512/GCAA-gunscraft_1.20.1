package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlobalWeaponBobbing {
    public static GlobalWeaponBobbing INSTANCE = new GlobalWeaponBobbing();

    float particleTicks = 0;

    GlobalWeaponBobbing() {}

    public void handleTranslation(PoseStack poseStack) {

    }

    public void update(float particleTicks) {
        this.particleTicks = particleTicks;
    }
}
