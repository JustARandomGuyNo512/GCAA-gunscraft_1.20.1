package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.config.ClientConfig;
import sheridan.gcaa.client.model.gun.namingScript.ScriptQueue;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.AttachmentsRenderContext;
import sheridan.gcaa.client.render.GunRenderContext;
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
    protected Map<String, ModelPart> flatDir = new Object2ObjectArrayMap<>();
    protected List<NamingScriptQueuePair> namingScriptQueuePairs = new ArrayList<>();
    protected boolean lowQualityLoaded = false;
    protected AnimationDefinition shoot;
    protected AnimationDefinition recoil;
    protected AnimationDefinition recoil_ads;
    protected AnimationDefinition shake;
    protected float adsShakeFactor = 0.35f;
    protected VertexConsumerGetter vertexConsumerGetter;
    protected VertexConsumerGetter vertexConsumerGetterLow;

    private static class NamingScriptQueuePair{
        public ModelPart modelPart;
        public ScriptQueue scriptQueue;
        NamingScriptQueuePair(ModelPart modelPart, ScriptQueue scriptQueue){
            this.modelPart = modelPart;
            this.scriptQueue = scriptQueue;
        }
    }

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
        shoot = animations.get("shoot");
        recoil = animations.get("recoil");
        recoil_ads = animations.get("recoil_ads");
        shake = animations.get("shake");
        vertexConsumerGetter = (context -> context.solid(texture));
        vertexConsumerGetterLow = (context -> context.solid(lowQualityTexture));
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

    public GunModel setOptions(String... args) {
        if (args != null && args.length % 2 == 0) {
            Map<String, String> options = new HashMap<>();
            for (int i = 0; i < args.length; i += 2) {
                options.put(args[i], args[i + 1]);
            }
            handleOptions(options);
        } else {
            GCAA.LOGGER.warn("Invalid options for gun model, must be key value pairs");
        }
        return this;
    }

    public GunModel setOptions(Map<String, String> options) {
        handleOptions(options);
        return this;
    }

    protected void handleOptions(Map<String, String> options) {
        VertexConsumerGetter renderType = createVertexConsumerGetter(options.getOrDefault("renderType", null));
        if (renderType != null) {
            vertexConsumerGetter = renderType;
        }
        VertexConsumerGetter renderTypeLow = createVertexConsumerGetter(options.getOrDefault("renderTypeLow", null));
        if (renderTypeLow != null) {
            vertexConsumerGetterLow = renderTypeLow;
        }

    }

    protected VertexConsumerGetter createVertexConsumerGetter(String type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case "solidMipMap" -> (context -> context.solidMipMap(texture));
            case "solidNoCull" -> (context -> context.solidNoCull(texture));
            case "solidNoCullMipMap" -> (context -> context.solidNoCullMipMap(texture));
            default -> null;
        };
    }

    private void preScriptProcess(
            ModelPart topLayer, Map<ModelPart, List<String>> scripts, Map<String, ModelPart> flatDir, List<Runnable> renameTasks) {
        for (Map.Entry<String, ModelPart> entry : topLayer.getChildren().entrySet()) {
            String name = entry.getKey();
            if (name.startsWith("_s")) {
                continue;
            }
            List<String> split = new ArrayList<>(List.of(name.split("&")));
            if (split.size() > 1) {
                String realName = split.get(0);
                renameTasks.add(() -> {
                    System.out.println("Find script in: '" + name + "' rename to '" + realName + "'");
                    topLayer.resetChildLayerName(name, realName);
                    flatDir.put(realName, entry.getValue());
                });
                split.remove(0);
                scripts.put(entry.getValue(), split);
            } else {
                flatDir.put(name, entry.getValue());
            }
            preScriptProcess(entry.getValue(), scripts, flatDir, renameTasks);
        }
    }

    protected void processModelScript(ModelPart topLayer) {
        List<Runnable> renameTasks = new ArrayList<>();
        Map<ModelPart, List<String>> scripts = new HashMap<>();
        preScriptProcess(topLayer, scripts, flatDir, renameTasks);
        for (Runnable task : renameTasks) {
            task.run();
        }
        if (lowQualityLoaded) {
            buildLodMapping(main, lowQualityMain);
        }
        Map<ModelPart, ScriptQueue> namingScriptQueueMap = new Object2ObjectArrayMap<>();
        for (Map.Entry<ModelPart, List<String>> entry : scripts.entrySet()) {
            ModelPart target = entry.getKey();
            List<String> rawScripts = entry.getValue();
            ScriptQueue scriptQueue = new ScriptQueue(rawScripts.toArray(new String[0]), this);
            if (!scriptQueue.isEmpty()) {
                namingScriptQueueMap.put(target, scriptQueue);
            }
        }
        sortScriptQueuesByDependency(namingScriptQueueMap);
    }

    public void sortScriptQueuesByDependency(Map<ModelPart, ScriptQueue> namingScriptQueueMap) {
        List<ModelPart> res = new ArrayList<>();
        Set<ModelPart> visited = new HashSet<>();
        for (Map.Entry<ModelPart, ScriptQueue> entry : namingScriptQueueMap.entrySet()) {
            if (visited.contains(entry.getKey())){
                continue;
            }
            ScriptQueue value = entry.getValue();
            if (value.hasFrontDepends()) {
                Stack<ModelPart> depends = new Stack<>();
                collectDepends(entry.getKey(), depends, new LinkedHashSet<>(), value, namingScriptQueueMap);
                while (!depends.isEmpty()) {
                    ModelPart pop = depends.pop();
                    if (!visited.contains(pop)) {
                        visited.add(pop);
                        res.add(pop);
                    }
                }
            }
            visited.add(entry.getKey());
            res.add(entry.getKey());
        }
        for (ModelPart modelPart : res) {
            ScriptQueue scriptQueue = namingScriptQueueMap.get(modelPart);
            if (scriptQueue != null) {
                namingScriptQueuePairs.add(new NamingScriptQueuePair(modelPart, scriptQueue));
            }
        }
    }

    private void collectDepends(ModelPart prevNode, Stack<ModelPart> depends, Set<ModelPart> visited,
                                ScriptQueue queue, Map<ModelPart, ScriptQueue> namingScriptQueueMap) {
        List<ModelPart> frontDepends = queue.getFrontDepends();
        visited.add(prevNode);
        for (ModelPart dependency : frontDepends) {
            if (!visited.contains(dependency)) {
                depends.push(dependency);
                ScriptQueue scriptQueue = namingScriptQueueMap.get(dependency);
                if (scriptQueue != null && scriptQueue.hasFrontDepends()) {
                    collectDepends(dependency, depends, visited, scriptQueue, namingScriptQueueMap);
                }
            } else {
                StringBuilder builder = new StringBuilder("Found circular dependency while building naming script queue, please check: '" + dependency.debug_name
                    + "' These " + visited.size() + " guys: ");
                for (ModelPart dependency1 : visited) {
                    builder.append(dependency1.debug_name).append(", ");
                }
                String string = builder.toString();
                GCAA.LOGGER.error(string);
                throw new RuntimeException(string);
            }
        }
    }

    protected void buildLodMapping(ModelPart mainLayer, ModelPart lowQualityMainLayer) {
        for (Map.Entry<String, ModelPart> entry : mainLayer.getChildren().entrySet()) {
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

    public Map<String, AnimationDefinition> getAnimations() {
        return animations;
    }

    public Map<String, List<ModelPart>> getAttachmentSlotPathMap() {
        return attachmentSlotPathMap;
    }

    public Map<ModelPart, ModelPart> getMainToLowMapping() {
        return mainToLowMapping;
    }

    public Map<ModelPart, ModelPart> getLowToMainMapping() {
        return lowToMainMapping;
    }

    public boolean isLowQualityLoaded() {
        return lowQualityLoaded;
    }

    public Map<String, ModelPart> getFlatDir() {
        return flatDir;
    }

    protected void preGunRender(GunRenderContext context, boolean lowQuality) {
        runNamingScriptProcess(context, lowQuality);
    }

    protected void runNamingScriptProcess(GunRenderContext context, boolean lowQuality) {
        for (NamingScriptQueuePair pair : namingScriptQueuePairs) {
            if (lowQuality) {
                ModelPart modelPart = mainToLowMapping.get(pair.modelPart);
                if (modelPart != null) {
                    pair.scriptQueue.process(modelPart, context, true);
                }
            } else {
                pair.scriptQueue.process(pair.modelPart, context, false);
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

    static long total = 0;
    static long t = 0;
    static int times = 0;
    static long avg = 0;

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
            t = System.nanoTime();
            renderGunModel(gunRenderContext);
            t = System.nanoTime() - t;
            total += t;
            times ++;
            if (times >= 200) {
                avg = total / times;
                times = 0;
                total = 0;
                if (Clients.isInAds()) {
                    System.out.println("avg time: " + ((double) avg / 1000000d) + " ms new render: " + ModelPart.__TEST__USE_NEW_RENDER__);
                }
            }
        }
        renderAttachmentsModel(gunRenderContext);
        renderPostEffect(gunRenderContext);
        if (gunRenderContext.delayedTasks != null) {
            for (Runnable task : gunRenderContext.delayedTasks) {
                task.run();
            }
        }
        afterRender(gunRenderContext);
    }

    protected abstract void renderGunModel(GunRenderContext context);
    protected abstract void animationGlobal(GunRenderContext context);
    protected abstract void renderGunModelLowQuality(GunRenderContext context);

    protected VertexConsumer getDefaultVertex(GunRenderContext context) {
        return vertexConsumerGetter.get(context);
    }

    protected VertexConsumer getDefaultVertexLow(GunRenderContext context) {
        return vertexConsumerGetterLow.get(context);
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
        return Clients.getAdsProgress() > 0.5f ?
                recoil_ads == null ? recoil : recoil_ads :
                recoil;
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

    protected void defaultAnimation(GunRenderContext gunRenderContext)  {
        if (gunRenderContext.isFirstPerson || gunRenderContext.isThirdPerson()) {
            if (this.shoot != null) {
                KeyframeAnimations.animate(this, this.shoot, gunRenderContext.lastShoot, 1);
            }
            if (gunRenderContext.isFirstPerson) {
                if (shake != null) {
                    float scale = Mth.lerp(Clients.getAdsProgress(), 1f, adsShakeFactor);
                    KeyframeAnimations.animate(this, shake, gunRenderContext.lastShoot, scale);
                }
                AnimationHandler.INSTANCE.applyRecoil(this);
                AnimationHandler.INSTANCE.applyReload(this);
                CameraAnimationHandler.INSTANCE.mix(camera);
            }
        }
    }

    public interface VertexConsumerGetter {
        VertexConsumer get(GunRenderContext context);
    }

}
