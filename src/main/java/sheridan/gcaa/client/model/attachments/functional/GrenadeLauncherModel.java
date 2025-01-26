package sheridan.gcaa.client.model.attachments.functional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.GrenadeLauncherReloadTask;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.attachments.*;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.fx.muzzleFlash.CommonMuzzleFlashes;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public abstract class GrenadeLauncherModel extends ArmRendererModel implements IAttachmentModel, IDirectionalModel, IAnimatedModel {
    public static final String RELOAD_ANIMATION_KEY = "grenade_launcher_reload";
    protected ResourceLocation texture;
    protected ModelPart root;
    protected ModelPart left_arm_pistol, left_arm_rifle, body, grenade, grenade_reloading, muzzle;
    protected AnimationDefinition reload;
    protected AnimationDefinition gun_reload;
    protected ModelPart low;
    protected ResourceLocation lowTexture;

    private final DisplayData.MuzzleFlashEntry muzzleFlashEntry =
            new DisplayData.MuzzleFlashEntry(new MuzzleFlashDisplayData().setScale(4f), CommonMuzzleFlashes.SUPPRESSOR_COMMON);

    public GrenadeLauncherModel(ResourceLocation texture, ResourceLocation modelPath,
                                ResourceLocation attachmentReloadAnimationPath, ResourceLocation gunReloadAnimationPath,
                                ModelPart lowModel, ResourceLocation lowTexture)  {
        this.texture = texture;
        this.root = ArsenalLib.loadBedRockGunModel(modelPath).bakeRoot().getChild("root");
        low = lowModel;
        this.lowTexture = lowTexture;
        reload = ArsenalLib.loadBedRockAnimationWithSound(attachmentReloadAnimationPath).get("reload");
        gun_reload = ArsenalLib.loadBedRockAnimationWithSound(gunReloadAnimationPath).get("reload");
        posInit(root);
    }

    protected abstract void posInit(ModelPart root);
    protected abstract void afterAnimation();
    protected abstract void renderMain(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry,
                                       ModelPart pose, boolean showAnimation, boolean hasGrenade);

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    protected ModelPart getLeftArm(GunRenderContext context) {
        return context.renderArmNew ? left_arm_rifle : left_arm_pistol;
    }

    @Override
    protected ModelPart getRightArm(GunRenderContext context) {
        return null;
    }

    @Override
    protected PoseStack lerpArmPose(boolean mainHand, PoseStack prevPose, GunRenderContext context) {
        return ReloadingHandler.INSTANCE.getCustomPayload(false) == GrenadeLauncherReloadTask.CUSTOM_PAYLOAD ?
                prevPose : LerpReloadAnimationPose(false, context, prevPose);
    }

    @Override
    protected boolean shouldRenderArm(boolean mainHand, GunRenderContext context, AttachmentRenderEntry entry) {
        return defaultShouldRenderArm(mainHand, context, entry);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        boolean showAnimation = context.isFirstPerson;
        long lastFire = showAnimation ? GrenadeLauncher.getLastFire(context.itemStack, context.gun) : 0;
        showAnimation = showAnimation && ReloadingHandler.INSTANCE.getCustomPayload(false) == GrenadeLauncherReloadTask.CUSTOM_PAYLOAD;
        if (showAnimation) {
            AnimationHandler.INSTANCE.apply(this, RELOAD_ANIMATION_KEY);
        }
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        if (low != null && context.useLowQuality()) {
            low.copyFrom(body);
            context.render(low, context.solid(lowTexture));
        } else {
            renderMain(context, attachmentRenderEntry, pose, showAnimation, !context.isFirstPerson && GrenadeLauncher.hasGrenade(context.itemStack, context.gun));
        }
        if (lastFire != 0) {
            context.pushPose().translateTo(muzzle);
            context.renderMuzzleFlashEntry(muzzleFlashEntry, lastFire, 1f);
            context.popPose();
        }
        renderArm(false, RenderAndMathUtils.copyPoseStack(context.poseStack), context, attachmentRenderEntry);
        context.popPose();

        if (showAnimation) {
            afterAnimation();
        }
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return root;
    }

    @Override
    public byte getDirection() {
        return LOWER;
    }

    @Override
    public AnimationDefinition getAnimation(String name) {
        if ("gun_reload".equals(name)) {
            return gun_reload;
        }
        if ("attachment_reload".equals(name)) {
            return reload;
        }
        return null;
    }
}