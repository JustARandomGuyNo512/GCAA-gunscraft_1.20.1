package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatus;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.MuzzleFlashLightHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.config.ClientConfig;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellDisplayData;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellRenderer;
import sheridan.gcaa.client.render.fx.muzzleSmoke.MuzzleSmokeRenderer;
import sheridan.gcaa.client.screens.AttachmentsGuiContext;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

import static net.minecraft.world.item.ItemDisplayContext.*;

@OnlyIn(Dist.CLIENT)
public class GunRenderer{
    private static long tempLastFire = 0;

    public static void renderInAttachmentScreen(PoseStack poseStack, ItemStack itemStack, IGun gun, IGunModel model, MultiBufferSource bufferSource, DisplayData displayData, float x, float y, float rx, float ry, float scale, AttachmentsGuiContext context) {
        if (displayData != null) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            displayData.applyAttachmentScreenTransform(poseStack, x, y, rx, ry, scale);
            poseStack.pushPose();
            GunRenderContext gunRenderContext = GunRenderContext.getClientMainHand(bufferSource, poseStack, itemStack, gun, FIXED, null, 15728880, 15728880, 655360);
            gunRenderContext.inAttachmentScreen = true;
            model.render(gunRenderContext);
            poseStack.popPose();
            if (context != null) {
                context.updateIconPos(poseStack, model);
            }
        }
    }

    public static void justRenderModel(ItemStack itemStackIn, ItemDisplayContext transformTypeIn, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, IGun gun, IGunModel model, DisplayData displayData) {
        if (model != null && displayData != null) {
            poseStackIn.mulPose(Axis.ZP.rotationDegrees(180));
            displayData.applyTransform(transformTypeIn, poseStackIn);
            if (transformTypeIn == GUI) {
                model.render(GunRenderContext.getGUI(bufferIn, poseStackIn, itemStackIn, gun, combinedLightIn, combinedOverlayIn, ClientConfig.renderAttachmentsInGuiView.get()));
            } else if (transformTypeIn == GROUND) {
                model.render(new GunRenderContext(bufferIn, poseStackIn, itemStackIn, gun, transformTypeIn, combinedLightIn, combinedOverlayIn, ClientConfig.renderAttachmentsInGroundView.get()));
            } else {
                model.render(new GunRenderContext(bufferIn, poseStackIn, itemStackIn, gun, transformTypeIn, combinedLightIn, combinedOverlayIn, true));
            }
        }
    }

    public static void renderWithEntity(LivingEntity entityIn, PoseStack stackIn, ItemStack itemStackIn, ItemDisplayContext type, MultiBufferSource bufferIn, IGun gun, int combinedLightIn, int combinedOverlayIn, boolean leftHand, IGunModel model, DisplayData displayData) {
        if (entityIn == null) {
            justRenderModel(itemStackIn, type, stackIn, bufferIn, combinedLightIn, combinedOverlayIn, gun, model, displayData);
            return;
        }
        if (model != null && displayData != null) {
            boolean isFirstPerson = type.firstPerson();
            String muzzleFlash = gun.getMuzzleFlash(itemStackIn);
            DisplayData.MuzzleFlashEntry muzzleFlashEntry = displayData.getMuzzleFlashEntry(muzzleFlash);
            if (isFirstPerson) {
                PoseStack poseStack = new PoseStack();
                CameraAnimationHandler.INSTANCE.applyToPose(poseStack);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                displayData.applyTransform(type, poseStack);
                if (Clients.handleWeaponBobbing) {
                    GlobalWeaponBobbing.INSTANCE.handleTranslation(poseStack);
                }
                CameraAnimationHandler.INSTANCE.clear();
                InertialRecoilData inertialRecoilData = displayData.getInertialRecoilData();
                boolean newShoot = false;
                if (tempLastFire != Clients.lastShootMain()) {
                    tempLastFire = Clients.lastShootMain();
                    newShoot = true;
                    if (gun.shootCreateBulletShell()) {
                        BulletShellDisplayData bulletShellDisplayData = displayData.getBulletShellDisplayData();
                        if (bulletShellDisplayData != null) {
                            BulletShellRenderer.push(bulletShellDisplayData, tempLastFire);
                        }
                    }
                    if (muzzleFlashEntry.muzzleFlash.hasMuzzleSmoke()) {
                        MuzzleSmokeRenderer.INSTANCE.openTaskQueue();
                    }
                }
                if (Clients.shouldHideFPRender) {
                    return;
                }
                if (ClientConfig.useDynamicWeaponLighting.get() &&
                        (!ClientConfig.enableMuzzleFlashLighting.get() || MuzzleFlashLightHandler.isFirstPersonLightOverride())) {
                    long dis = (System.currentTimeMillis() - tempLastFire);
                    if (dis < 35) {
                        float particleTick = Minecraft.getInstance().getPartialTick();
                        int blockLight = entityIn.isOnFire() ? 15 :
                                entityIn.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(entityIn.getEyePosition(particleTick)));
                        int lightInc = Gun.MUZZLE_STATE_SUPPRESSOR.equals(gun.getMuzzleFlash(itemStackIn)) ?
                                5 : 10;
                        combinedLightIn = LightTexture.pack((int) Math.min(15, blockLight + Math.min(lightInc, dis)), entityIn.level().getBrightness(LightLayer.SKY,
                                BlockPos.containing(entityIn.getEyePosition(particleTick))));
                    }
                }
                GunRenderContext context = GunRenderContext.getClientMainHand(bufferIn, poseStack, itemStackIn, gun, type, muzzleFlashEntry, combinedLightIn, combinedLightIn, combinedOverlayIn);
                if (newShoot) {
                    AnimationDefinition recoil = model.getRecoil(context);
                    if (recoil != null) {
                        AnimationHandler.INSTANCE.pushRecoil(recoil, tempLastFire);
                    }
                }
                if (context.isFirstPerson) {
                    PoseStack original = RenderAndMathUtils.copyPoseStack(poseStack);
                    context.saveInLocal(GunRenderContext.ORIGINAL_GUN_VIEW_POSE_FP, original);
                }
                if (inertialRecoilData != null) {
                    AnimationHandler.INSTANCE.applyInertialRecoil(context.poseStack, inertialRecoilData);
                }
                model.render(context);
            } else {
                if (entityIn instanceof Player player) {
                    stackIn.mulPose(Axis.ZP.rotationDegrees(180));
                    displayData.applyTransform(type, stackIn);
                    boolean isLocalPlayer = entityIn.getId() == Clients.clientPlayerId;
                    if (isLocalPlayer) {
                        model.render(GunRenderContext.getClientMainHand(bufferIn, stackIn, itemStackIn, gun, type, muzzleFlashEntry, combinedLightIn, combinedLightIn, combinedOverlayIn));
                    } else {
                        PlayerStatus status = PlayerStatusProvider.getStatus(player);
                        long timeDist = status.getLocalTimeOffset() - Clients.localTimeOffset;
                        long currentLastShoot = status.getLastShoot() - timeDist + status.getLatency() + Clients.getLocalLatency() + 50L;
                        model.render(new GunRenderContext(bufferIn, stackIn, itemStackIn, gun, type, combinedLightIn, combinedOverlayIn, combinedLightIn, muzzleFlashEntry, currentLastShoot, true));
                    }
                }
            }
        }
    }
}
