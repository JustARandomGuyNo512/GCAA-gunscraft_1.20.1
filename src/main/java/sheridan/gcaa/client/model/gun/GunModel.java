package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
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
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public abstract class GunModel extends HierarchicalModel<Entity> implements IGunModel {
    public static final String LOW_QUALITY_KEY = "__low_quality__";
    public static final String LOW_QUALITY_DISABLE = "__low_quality_disable__";
    protected ResourceLocation texture, lowQualityTexture;
    public final ModelPart root, gun, main, left_arm, right_arm, camera;
    public ModelPart lowQualityRoot, lowQualityGun, lowQualityMain;
    protected Map<String, AnimationDefinition> animations;
    protected Map<String, List<ModelPart>> attachmentSlotPathMap = new Object2ObjectArrayMap<>();
    protected Map<ModelPart, ModelPart> mainToLowMapping = new Object2ObjectArrayMap<>();
    protected Map<ModelPart, ModelPart> lowToMainMapping = new Object2ObjectArrayMap<>();
    protected Map<ModelPart, List<VisibleController>> visibleControllerMap = new Object2ObjectArrayMap<>();
    protected boolean lowQualityLoaded = false;

    public GunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
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
        if (lowQualityModelPath != null && lowQualityTexture != null) {
            lowQualityRoot = ArsenalLib.loadBedRockGunModel(lowQualityModelPath).bakeRoot().getChild("root");
            lowQualityRoot.meshingAll();
            lowQualityGun = lowQualityRoot.getChild("gun");
            lowQualityMain = lowQualityGun.getChild("main");
            this.lowQualityTexture = lowQualityTexture;
            lowQualityLoaded = true;
        }
        buildAttachmentSlotMap(main);
        processModelScript(main);
        postInit(main, gun, root);
    }

    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {}

    //提取脚本，重命名part
    private void preScriptProcess(
            ModelPart topLayer, Map<ModelPart, String[]> scripts, Map<String, ModelPart> flatDir, List<Runnable> renameTasks) {
        for (Map.Entry<String, ModelPart> entry : topLayer.getChildren().entrySet()) {
            String name = entry.getKey();
            //配件槽定位点不做处理
            if (name.startsWith("_s")) {
                continue;
            }
            String[] split = name.split("&");
            if (split.length > 1) {
                //添加延迟任务
                renameTasks.add(() -> {
                    System.out.println("Find script in: '" + name + "' rename to '" + split[0] + "'");
                    topLayer.resetChildLayerName(name, split[0]);
                });
                String script = split[1];
                String[] rules = script.split(",");
                scripts.put(entry.getValue(), rules);
            }
            //添加
            flatDir.put(name, entry.getValue());
            preScriptProcess(entry.getValue(), scripts, flatDir, renameTasks);
        }
    }

    protected void processModelScript(ModelPart topLayer) {
        Map<ModelPart, String[]> scripts = new HashMap<>();
        Map<String, ModelPart> flatDir = new HashMap<>();
        List<Runnable> renameTasks = new ArrayList<>();
        preScriptProcess(topLayer, scripts, flatDir, renameTasks);
        for (Runnable task : renameTasks) {
            task.run();
        }
        if (lowQualityLoaded) {
            buildLodMapping(main, lowQualityMain);
        }
        for (Map.Entry<ModelPart, String[]> entry : scripts.entrySet()) {
            String[] rules = entry.getValue();
            if (rules.length == 0) {
                continue;
            }
            List<VisibleController> controllers = new ArrayList<>();
            ModelPart key = entry.getKey();
            for (String rule : rules) {
                boolean flag = rule.charAt(0) == '^';
                rule = flag ? rule.substring(1) : rule;
                if (rule.startsWith(".") || rule.startsWith("s_")) {
                    // 绑定配件槽替换
                    rule = rule.startsWith(".") ? rule.substring(1) : rule.substring(2);
                    controllers.add(new SlotBoundVisibleController(key, flag, Attachment.getConstantNameField(rule)));
                } else if ("SCOPES".equals(rule)) {
                    // 是否包含瞄具
                    controllers.add(new containScopeVisibleController(flag, key));
                } else {
                    // 绑定其它配件可见性
                    if (flatDir.containsKey(rule)) {
                        controllers.add(new PartBoundVisibleController(flag, key, flatDir.get(rule)));
                    }
                }
            }
            if (!controllers.isEmpty()) {
                visibleControllerMap.put(key, controllers);
            }
        }
    }

    protected void buildLodMapping(ModelPart mainLayer, ModelPart lowQualityMainLayer) {
        for (Map.Entry<String, ModelPart> entry : mainLayer.getChildren().entrySet()) {
            //忽略配件定位槽
            if (entry.getKey().startsWith("s_")) {
                continue;
            }
            if (lowQualityMainLayer.hasChild(entry.getKey())) {
                ModelPart value = entry.getValue();
                ModelPart child = lowQualityMainLayer.getChild(entry.getKey());
                mainToLowMapping.put(value, child);
                lowToMainMapping.put(child, value);
                buildLodMapping(value, child);
            }
        }
    }

    protected void preGunRender(GunRenderContext context, boolean lowQuality) {
        runVisibleProcess(context, lowQuality);
    }

    protected void runVisibleProcess(GunRenderContext context, boolean lowQuality) {
        if (lowQuality) {
            for (Map.Entry<ModelPart, List<VisibleController>> entry : visibleControllerMap.entrySet()) {
                //需要存在映射才处理
                if (!mainToLowMapping.containsKey(entry.getKey())) {
                    continue;
                }
                List<VisibleController> value = entry.getValue();
                for (VisibleController controller : value) {
                    controller.processLowQuality(context);
                }
            }
            return;
        }
        Collection<List<VisibleController>> values = visibleControllerMap.values();
        for (List<VisibleController> value : values) {
            for (VisibleController controller : value) {
                controller.process(context);
            }
        }
    }

    public GunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        this(modelPath, animationPath, texture, null, null);
    }

    public static void disableLowQuality(GunRenderContext context) {
        context.saveInLocal(LOW_QUALITY_KEY, null);
        context.saveInLocal(LOW_QUALITY_DISABLE, true);
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
            preGunRender(gunRenderContext, true);
            renderGunModelLowQuality(gunRenderContext);
        } else {
            preGunRender(gunRenderContext, false);
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
            if (gunRenderContext.isFirstPerson) {
                AnimationHandler.INSTANCE.applyReload(this);
                CameraAnimationHandler.INSTANCE.mix(camera);
            }
        }
    }

    private abstract class VisibleController {
        protected final boolean flag;
        protected final ModelPart part, lowPart;
        public VisibleController(boolean flag, ModelPart part) {
            this.flag = flag;
            this.part = part;
            this.lowPart = mainToLowMapping.get(part);
        }
        abstract void process(GunRenderContext gunRenderContext);
        private void processLowQuality(GunRenderContext gunRenderContext) {
            if (lowPart != null) {
                processLow(gunRenderContext);
            }
        }
        abstract void processLow(GunRenderContext gunRenderContext);
    }

    private class SlotBoundVisibleController extends VisibleController {
        private final String slotName;
        public SlotBoundVisibleController(ModelPart part, boolean flag, String slotName) {
            super(flag, part);
            this.slotName = slotName;
        }
        @Override
        public void process(GunRenderContext gunRenderContext) {
            part.visible = flag == gunRenderContext.has(slotName);
        }

        @Override
        void processLow(GunRenderContext gunRenderContext) {
            lowPart.visible = flag == gunRenderContext.has(slotName);
        }
    }

    private class PartBoundVisibleController extends VisibleController {
        private final ModelPart other;
        public PartBoundVisibleController(boolean flag, ModelPart part, ModelPart other)  {
            super(flag, part);
            this.other = other;
        }
        @Override
        void process(GunRenderContext gunRenderContext) {
            part.visible = flag == other.visible;
        }

        @Override
        void processLow(GunRenderContext gunRenderContext) {
            ModelPart lowOther = mainToLowMapping.get(other);
            if (lowOther != null) {
                lowPart.visible = flag == lowOther.visible;
            }
        }
    }

    private class containScopeVisibleController extends VisibleController {
        public containScopeVisibleController(boolean flag, ModelPart part) {
            super(flag, part);
        }
        @Override
        void process(GunRenderContext gunRenderContext) {
            part.visible = flag != gunRenderContext.notContainsScope();
        }

        @Override
        void processLow(GunRenderContext gunRenderContext) {
            lowPart.visible = flag != gunRenderContext.notContainsScope();
        }
    }
}
