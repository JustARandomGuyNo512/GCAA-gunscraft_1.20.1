package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.config.ClientConfig;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.AttachmentsRenderContext;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public abstract class NewGunModel extends HierarchicalModel<Entity> implements IGunModel {
    public static final String LOW_QUALITY_KEY = "__low_quality__";
    public static final String LOW_QUALITY_DISABLE = "__low_quality_disable__";
    protected ResourceLocation texture, lowQualityTexture;
    public final ModelPart root, gun, main, left_arm, right_arm, camera;
    public ModelPart lowQualityRoot, lowQualityGun, lowQualityMain;
    protected Map<String, AnimationDefinition> animations;
    protected Map<String, List<ModelPart>> attachmentSlotPathMap;
    protected boolean lowQualityLoaded = false;

    public NewGunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                       @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture) {
        this.texture = texture;
        root = ArsenalLib.loadBedRockGunModel(modelPath).bakeRoot().getChild("root");
        root.meshingAll();
        camera = root.getChild("camera");
        gun = root.getChild("gun");
        main = gun.getChild("main");
        left_arm = gun.getChild("left_arm");
        right_arm = gun.getChild("right_arm");
        animations = ArsenalLib.loadBedRockAnimationWithSound(animationPath);
        attachmentSlotPathMap = new HashMap<>();
        buildAttachmentSlotMap(main);
        if (lowQualityModelPath != null && lowQualityTexture != null) {
            lowQualityRoot = ArsenalLib.loadBedRockGunModel(lowQualityModelPath).bakeRoot().getChild("root");
            lowQualityRoot.meshingAll();
            lowQualityGun = lowQualityRoot.getChild("gun");
            lowQualityMain = lowQualityGun.getChild("main");
            this.lowQualityTexture = lowQualityTexture;
            lowQualityLoaded = true;
        }
        postInit(main, gun, root);
    }

    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {}

    public NewGunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        this(modelPath, animationPath, texture, null, null);
    }

    protected void buildAttachmentSlotMap(ModelPart layer) {
        Map<String, ModelPart> children = layer.getChildren();
        if (children != null) {
            for (Map.Entry<String, ModelPart> child : children.entrySet()) {
                String key = child.getKey();
                if (key.startsWith("s_") && !attachmentSlotPathMap.containsKey(key)) {
                    Stack<ModelPart> pathStack = new Stack<>();
                    ModelPart part = child.getValue();
                    pathStack.add(part);
                    ModelPart parent = part.parent;
                    while (parent != main) {
                        pathStack.add(parent);
                        parent = parent.parent;
                    }
                    List<ModelPart> pathList = new ArrayList<>();
                    while (!pathStack.isEmpty()) {
                        pathList.add(pathStack.pop());
                    }
                    attachmentSlotPathMap.put(key, pathList);
                }
                buildAttachmentSlotMap(child.getValue());
            }
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return attachmentSlotPathMap.containsKey(modelSlotName);
    }

    @Override
    public void render(GunRenderContext gunRenderContext) {
        saveOriginalPoseInFirstPerson(gunRenderContext);
        animationGlobal(gunRenderContext);
        handleGunTranslate(gunRenderContext.poseStack);
        if (shouldRenderLowQuality(gunRenderContext)) {
            gunRenderContext.saveInLocal(LOW_QUALITY_KEY, Boolean.TRUE);
            renderGunModelLowQuality(gunRenderContext);
        } else {
            renderGunModel(gunRenderContext);
        }
        renderAttachmentsModel(gunRenderContext);
        renderPostEffect(gunRenderContext);
        afterRender(gunRenderContext);
    }

    protected abstract void renderGunModel(GunRenderContext context);
    protected abstract void animationGlobal(GunRenderContext context);
    protected abstract void renderGunModelLowQuality(GunRenderContext context);

    protected VertexConsumer getDefaultVertex(GunRenderContext context) {
        return context.solid(texture);
    }

    protected VertexConsumer getDefaultVertexLow(GunRenderContext context) {
        return context.solid(lowQualityTexture);
    }

    protected void renderAttachmentsModel(GunRenderContext context) {
        AttachmentsRenderContext attachmentsRenderContext = context.attachmentsRenderContext;
        if (attachmentsRenderContext == null) {
            return;
        }
        for (AttachmentRenderEntry entry : attachmentsRenderContext.modelSlotLayer.values()) {
            if (entry.rendered) {
                continue;
            }
            List<ModelPart> modelParts = attachmentSlotPathMap.get(entry.modelSlotName);
            if (modelParts != null) {
                int length = modelParts.size();
                context.pushPose();
                if (length > 1) {
                    for (int i = 0; i < length - 1; i++) {
                        modelParts.get(i).translateAndRotate(context.poseStack);
                    }
                    entry.render(context, modelParts.get(length - 1));
                } else {
                    entry.render(context, modelParts.get(0));
                }
                context.popPose();
            }
        }
    }

    protected boolean shouldRenderLowQuality(GunRenderContext context) {
        if (context.isFirstPerson || !lowQualityLoaded || context.inAttachmentScreen) {
            return false;
        }
        if (context.localRenderStorage != null && context.localRenderStorage.containsKey(LOW_QUALITY_DISABLE)) {
            return false;
        }
        ItemDisplayContext transformType = context.transformType;
        switch (transformType) {
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {return ClientConfig.renderLowQualityModelInTPView.get();}
            case GROUND -> {return ClientConfig.renderLowQualityModelInGroundView.get();}
            default -> {return ClientConfig.renderLowQualityModelInOtherView.get();}
        }
    }

    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    protected void afterRender(GunRenderContext context) {
        root.resetPoseAll();
    }

    protected void saveOriginalPoseInFirstPerson(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
            Object original = gunRenderContext.getLocalSaved(GunRenderContext.ORIGINAL_GUN_VIEW_POSE_FP);
            if (original instanceof PoseStack stack) {
                handleGunTranslate(stack);
                gunRenderContext.saveInLocal(GunRenderContext.ORIGINAL_GUN_VIEW_POSE_FP, stack);
            }
        }
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
        main.translateAndRotate(poseStack);
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return animations.get("recoil");
    }

    @Override
    public AnimationDefinition getReload() {
        return animations.get("reload");
    }

    @Override
    public AnimationDefinition getFullReload() {
        return animations.get("full_reload");
    }

    @Override
    public AnimationDefinition get(String name) {
        return animations.get(name);
    }

    @Override
    public boolean hasAnimation(String name) {
        return animations.containsKey(name);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
        List<ModelPart> path = attachmentSlotPathMap.get(modelSlotName);
        if (path != null) {
            handleGunTranslate(poseStack);
            for (ModelPart part : path) {
                part.translateAndRotate(poseStack);
            }
        }
    }

    @Override
    public ModelPart root() {
        return root;
    }

    protected boolean getShouldRenderLowQuality(GunRenderContext context) {
        return context.localRenderStorage != null && context.localRenderStorage.get(LOW_QUALITY_KEY) != null;
    }

    protected void defaultPistolAnimation(GunRenderContext gunRenderContext, AnimationDefinition shoot)  {
        if (gunRenderContext.isFirstPerson || gunRenderContext.isThirdPerson()) {
            KeyframeAnimations.animate(this, shoot, gunRenderContext.lastShoot, 1);
            if (gunRenderContext.isFirstPerson) {
                AnimationHandler.INSTANCE.applyRecoil(this);
                AnimationHandler.INSTANCE.applyReload(this);
                CameraAnimationHandler.INSTANCE.mix(camera);
            }
        }
    }

    protected void defaultRifleAnimation(GunRenderContext gunRenderContext, AnimationDefinition shoot)  {
        if (gunRenderContext.isFirstPerson || gunRenderContext.isThirdPerson()) {
            KeyframeAnimations.animate(this, shoot, gunRenderContext.lastShoot,1);
            AnimationDefinition recoil = getRecoil(gunRenderContext);
            if (recoil != null) {
                KeyframeAnimations.animate(this, recoil, gunRenderContext.lastShoot, 1);
            }
            if (gunRenderContext.isFirstPerson) {
                AnimationHandler.INSTANCE.applyReload(this);
                CameraAnimationHandler.INSTANCE.mix(camera);
            }
        }
    }
}
