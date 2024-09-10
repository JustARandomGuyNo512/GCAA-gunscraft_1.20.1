package sheridan.gcaa.client.model.guns;

import net.minecraft.client.model.geom.PartPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class BulletChainHandler {
    public static void handleBulletChain(GunRenderContext context, ModelPart[] bullets, long showBulletsReloadingTimeDis) {
        boolean shouldHandleChainAnimation = context.isFirstPerson;
        boolean alwaysShowBullets = ReloadingHandler.isReloading() && ReloadingHandler.disFromLastReload(showBulletsReloadingTimeDis);
        int bulletLeft = context.ammoLeft;
        int count = bullets.length;
        float fireProgress = context.getFireProgress();
        for (int i = 0; i < count; i++) {
            bullets[i].visible = (count - i) <= bulletLeft || alwaysShowBullets;
            if (shouldHandleChainAnimation && (bulletLeft > i + 1) && fireProgress != 0) {
                ModelPart next = i + 1 < bullets.length ? bullets[i + 1] : null;
                if (next != null) {
                    translateBullet(fireProgress, bullets[i], next);
                }
            }
        }
    }

    private static void translateBullet(float fireProgress, ModelPart prevBulletModel, ModelPart nextBulletModel) {
        PartPose partPose = nextBulletModel.getInitialPose();
        float x, y, z;
        x = (partPose.x - prevBulletModel.x) * fireProgress;
        y = (partPose.y - prevBulletModel.y) * fireProgress;
        z = (partPose.z - prevBulletModel.z) * fireProgress;
        prevBulletModel.x += x;
        prevBulletModel.y += y;
        prevBulletModel.z += z;
    }
}
