package sheridan.gcaa.client.model.attachments.functional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.GrenadeLauncherReloadTask;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.attachments.ArmRendererModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.IDirectionalModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.fx.muzzleFlash.CommonMuzzleFlashes;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class GP_25Model extends ArmRendererModel implements IAttachmentModel, IDirectionalModel {
    public static GP_25Model INSTANCE;
    public static final String RELOAD_ANIMATION_KEY = "gp_25_reload";
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/gp_25.png");
    private final ModelPart root;
    private final ModelPart left_arm, left_arm_new, body, grenade, grenade_reloading, muzzle;
    private final AnimationDefinition reload;
    private final AnimationDefinition rifle_ak_reload;
    private final ModelPart low;

    private final DisplayData.MuzzleFlashEntry muzzleFlashEntry =
            new DisplayData.MuzzleFlashEntry(new MuzzleFlashDisplayData().setScale(4f), CommonMuzzleFlashes.SUPPRESSOR_COMMON);

    public GP_25Model() {
        this.root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/gp_25.geo.json"))
                .bakeRoot().getChild("root");
        left_arm = root.getChild("left_arm");
        left_arm_new = root.getChild("left_arm_long");
        body = root.getChild("body").meshing();
        grenade = root.getChild("grenade").meshing();
        grenade_reloading = root.getChild("grenade_reloading").meshing();
        muzzle = root.getChild("muzzle");
        reload = ArsenalLib.loadBedRockAnimationWithSound(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/reload_rifle.animation.json")).get("reload");
        rifle_ak_reload = ArsenalLib.loadBedRockAnimationWithSound(
                new ResourceLocation(GCAA.MODID, "model_assets/guns/generic/gp_25_reload_ak_rifle.animation.json")).get("reload");
        INSTANCE = this;
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("gp_25").meshing();
    }

    public AnimationDefinition getGunReload() {
        return rifle_ak_reload;
    }
    public AnimationDefinition getAttachmentReload() {
        return reload;
    }

    @Override
    protected ModelPart getLeftArm(GunRenderContext context) {
        return context.renderArmNew ? left_arm_new : left_arm;
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
        long lastFire = (showAnimation || context.isThirdPerson()) ? GrenadeLauncher.getLastFire(context.itemStack, context.gun) : 0;
        showAnimation = showAnimation && ReloadingHandler.INSTANCE.getCustomPayload(false) == GrenadeLauncherReloadTask.CUSTOM_PAYLOAD;
        if (showAnimation) {
            AnimationHandler.INSTANCE.apply(this, RELOAD_ANIMATION_KEY);
        }
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        if (context.useLowQuality()) {
            low.copyFrom(body);
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
        } else {
            boolean hasGrenade = !context.isFirstPerson && GrenadeLauncher.hasGrenade(context.itemStack, context.gun);
            context.render(body, vertexConsumer);
            context.renderIf(vertexConsumer, hasGrenade, grenade);
            context.renderIf(vertexConsumer, showAnimation, grenade_reloading);
        }
        if (lastFire != 0) {
            context.pushPose().translateTo(muzzle);
            context.renderMuzzleFlashEntry(muzzleFlashEntry, lastFire, 1f);
            context.popPose();
        }
        renderArm(false, RenderAndMathUtils.copyPoseStack(context.poseStack), context, attachmentRenderEntry);
        context.popPose();

        if (showAnimation) {
            left_arm.resetPose();
            left_arm_new.resetPoseAll();
            grenade_reloading.resetPose();
        }
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    public byte getDirection() {
        return LOWER;
    }

    @Override
    public ModelPart root() {
        return getRoot();
    }
}
