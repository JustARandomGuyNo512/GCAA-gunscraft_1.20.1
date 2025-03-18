package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class SpringRecoilHandler {
    public static final SpringRecoilHandler INSTANCE = new SpringRecoilHandler();
    private static NewRecoilData recoilData;


    public void apply(PoseStack poseStack) {
        if (recoilData != null) {
            recoilData.apply(poseStack);
        }
    }

    public void onShoot(IGun gun, ItemStack itemStack, float pControl, float yControl,
                        float pRate, float yRate, float directionX, float directionY) {
        NewRecoilData newRecoilData = NewRecoilData.get(gun);
        if (newRecoilData != null) {
            newRecoilData.onShoot(itemStack, gun, pControl, yControl, pRate, yRate, directionX, directionY);
            recoilData = newRecoilData;
        }
    }

    public void update() {
        if (recoilData != null) {
            recoilData.update();
        }
    }
}
