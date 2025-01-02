package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.config.ClientConfig;
import sheridan.gcaa.client.model.gun.LodGunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellRenderer;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class GunRenderContext {
    public static final String LEFT_ARM_RENDER_REPLACE = "left_arm_render_replace";
    public static final String RIGHT_ARM_RENDER_REPLACE = "right_arm_render_replace";
    public static final String ORIGINAL_GUN_VIEW_POSE_FP = "original_gun_view_pose_fp";
    public MultiBufferSource bufferSource;
    public PoseStack poseStack;
    public ItemStack itemStack;
    public IGun gun;
    public ItemDisplayContext transformType;
    public boolean isFirstPerson;
    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;
    public int packedLight;
    public int packedOverlay;
    public long lastShoot;
    public long lastReload;
    public DisplayData.MuzzleFlashEntry muzzleFlashEntry;
    public AttachmentsRenderContext attachmentsRenderContext;
    public Map<String, Object> localRenderStorage;
    public int ammoLeft;

    public boolean renderArmNew = false;
    public boolean inAttachmentScreen = false;

    private static String lastAttachmentContextUUID = "none";
    private static AttachmentsRenderContext tempAttachmentContext = null;
    public static GunRenderContext getClientMainHand(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType, DisplayData.MuzzleFlashEntry muzzleFlashEntry, int packedLight, int packedOverlay) {
        GunRenderContext context = new GunRenderContext(bufferSource, poseStack, itemStack, gun, transformType, packedLight, packedOverlay, muzzleFlashEntry, Clients.lastShootMain() + 10L, false);
        String uuid = gun.getAttachmentsModifiedUUID(itemStack);
        if (!lastAttachmentContextUUID.equals(uuid)) {
            lastAttachmentContextUUID = uuid;
            tempAttachmentContext = AttachmentsHandler.INSTANCE.getRenderContext(itemStack, gun);
        } else {
            if (tempAttachmentContext != null) {
                tempAttachmentContext.reset();
            }
        }
        context.attachmentsRenderContext = tempAttachmentContext;
        return context;
    }

    public static GunRenderContext getGUI(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, int packedLight, int packedOverlay, boolean useAttachmentContext) {
        GunRenderContext context = new GunRenderContext(bufferSource, poseStack, itemStack, gun, ItemDisplayContext.GUI, packedLight, packedOverlay, false);
        if (useAttachmentContext) {
            String uuid = gun.getAttachmentsModifiedUUID(itemStack);
            context.attachmentsRenderContext = lastAttachmentContextUUID.equals(uuid) ? tempAttachmentContext : AttachmentsHandler.INSTANCE.getRenderContext(itemStack, gun);
            if (tempAttachmentContext != null) {
                tempAttachmentContext.reset();
            }
        }
        return context;
    }

    public GunRenderContext(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType, int packedLight, int packedOverlay, boolean useAttachmentContext) {
        this.bufferSource = bufferSource;
        this.poseStack = poseStack;
        this.itemStack = itemStack;
        this.transformType = transformType;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
        this.gun = gun;
        this.isFirstPerson = transformType.firstPerson();
        lastShoot = Clients.lastShootMain();
        ammoLeft = gun.getAmmoLeft(itemStack);
        if (useAttachmentContext) {
            this.attachmentsRenderContext = AttachmentsHandler.INSTANCE.getRenderContext(itemStack, gun);
        }
        lastReload = ReloadingHandler.INSTANCE.getLastStartReload();
    }

    public GunRenderContext(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType, int packedLight, int packedOverlay, DisplayData.MuzzleFlashEntry muzzleFlashEntry, long lastShoot, boolean useAttachmentContext) {
        this.bufferSource = bufferSource;
        this.poseStack = poseStack;
        this.itemStack = itemStack;
        this.transformType = transformType;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
        this.gun = gun;
        this.isFirstPerson = transformType.firstPerson();
        this.muzzleFlashEntry = muzzleFlashEntry;
        this.lastShoot = lastShoot;
        ammoLeft = gun.getAmmoLeft(itemStack);
        attachmentsRenderContext = useAttachmentContext ? AttachmentsHandler.INSTANCE.getRenderContext(itemStack, gun) : null;
        lastReload = ReloadingHandler.INSTANCE.getLastStartReload();
    }

    public float getFireProgress() {
        if (lastShoot == 0) {
            return 0;
        }
        long timeDis = (System.currentTimeMillis()) - lastShoot;
        float shootDelay = (float) gun.getFireDelay(itemStack) * 5;
        return timeDis >= shootDelay ? 0 : timeDis / shootDelay;
    }

    public void render(ModelPart part, VertexConsumer vertexConsumer) {
        part.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a, true);
    }

    public void render(ModelPart part, int light, int overlay, float alpha, VertexConsumer vertexConsumer) {
        part.render(poseStack, vertexConsumer, light, overlay, r, g, b, alpha, true);
    }

    public VertexConsumer getBuffer(RenderType renderType) {
        return bufferSource.getBuffer(renderType);
    }

    public VertexConsumer muzzleFlash(ResourceLocation texture)  {
        return bufferSource.getBuffer(RenderTypes.getMuzzleFlash(texture));
    }

    public VertexConsumer solid(ResourceLocation texture) {
        return bufferSource.getBuffer(RenderType.entityCutout(texture));
    }

    public VertexConsumer solidMipMap(ResourceLocation texture) {
        return bufferSource.getBuffer(RenderType.entityCutout(texture));
    }

    public VertexConsumer solidNoCull(ResourceLocation texture) {
        return bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
    }

    public VertexConsumer solidNoCullMipMap(ResourceLocation texture) {
        return bufferSource.getBuffer(RenderTypes.getCutOutNoCullMipmap(texture));
    }

    public void render(VertexConsumer vertexConsumer, ModelPart... parts) {
        for (ModelPart part : parts) {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a, true);
        }
    }

    public void renderIf(ModelPart part, VertexConsumer vertexConsumer, boolean condition)  {
        if (condition) {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a, true);
        }
    }

    public void renderIfOrElse(ModelPart ifTrue, ModelPart orElse,  boolean condition, VertexConsumer vertexConsumer)  {
        if (condition) {
            ifTrue.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a, true);
        } else {
            orElse.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a, true);
        }
    }

    public void renderIf(VertexConsumer vertexConsumer, boolean condition, ModelPart ...parts)  {
        if (condition) {
            for (ModelPart part : parts) {
                part.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a, true);
            }
        }
    }

    public void renderArm(ModelPart poseLayer, boolean mainHand) {
        if (isFirstPerson) {
            if (!shouldRenderArmImmediately(mainHand, poseLayer, true)) {
                renderArmNew = true;
                return;
            }
            NewPlayerArmRenderer.INSTANCE.renderByLayer(poseLayer, 1, 1, 1, packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }

    public void renderArmOldStylePistol(ModelPart pose, boolean mainHand) {
        if (isFirstPerson && shouldRenderArmImmediately(mainHand, pose, false)) {
            NewPlayerArmRenderer.INSTANCE.renderOldStylePistolByLayer(pose, mainHand, packedLight, packedOverlay, bufferSource, poseStack, true);
        }
    }


    private boolean shouldRenderArmImmediately(boolean mainHand, ModelPart poseLayer, boolean newArmRenderer) {
        AttachmentSlot armReplace = mainHand ? Clients.MAIN_HAND_STATUS.getRightArmReplace() : Clients.MAIN_HAND_STATUS.getLeftArmReplace();
        if (armReplace != null) {
            String key = mainHand ? RIGHT_ARM_RENDER_REPLACE : LEFT_ARM_RENDER_REPLACE;
            PoseStack poseStack = RenderAndMathUtils.copyPoseStack(this.poseStack);
            poseLayer.translateAndRotate(poseStack);
            if (newArmRenderer) {
                boolean isSlim = NewPlayerArmRenderer.isSlim();
                if (mainHand) {
                    poseLayer.getChild(isSlim ? "right_arm_slim" : "right_arm_normal").translateAndRotate(poseStack);
                } else {
                    poseLayer.getChild(isSlim ? "left_arm_slim" : "left_arm_normal").translateAndRotate(poseStack);
                }
            }
            saveInLocal(key, poseStack);
            return false;
        }
        return true;
    }

    public GunRenderContext pushPose() {
        poseStack.pushPose();
        return this;
    }

    public boolean hasLocalKey(String key) {
        return localRenderStorage != null && localRenderStorage.containsKey(key);
    }

    public boolean useLowQuality() {
        return hasLocalKey(LodGunModel.LOW_QUALITY_KEY);
    }

    public void popPose() {
        poseStack.popPose();
    }

    /**
     * apply translation and rotation to a layer
     * */
    public GunRenderContext translateTo(ModelPart posePart) {
        posePart.translateAndRotate(poseStack);
        return this;
    }

    public GunRenderContext translateTo(ModelPart ...poseParts) {
        for (ModelPart posePart : poseParts) {
            posePart.translateAndRotate(poseStack);
        }
        return this;
    }

    public AttachmentRenderEntry getAttachmentRenderEntry(String slotName) {
        return attachmentsRenderContext == null ? null : attachmentsRenderContext.modelSlotLayer.get(slotName);
    }

    public void renderBulletShell() {
        if (isFirstPerson) {
            BulletShellRenderer.render(this);
        }
    }

    public void renderMuzzleFlash(float scale) {
        if (muzzleFlashEntry != null) {
            if (ClientConfig.enableMuzzleFlashScaleModifyOnUsingScope.get() && isFirstPerson) {
                scale *= Float.isNaN(Clients.gunModelFovModify) ? 1 : Mth.clamp(Clients.gunModelFovModify / 70f, 0.5f, 1f);
            }
            muzzleFlashEntry.muzzleFlash.render(poseStack, bufferSource, muzzleFlashEntry.displayData, scale, lastShoot, isFirstPerson);
        }
    }

    public void renderMuzzleFlashEntry(DisplayData.MuzzleFlashEntry muzzleFlashEntry, long lastShoot, float scale) {
        if (ClientConfig.enableMuzzleFlashScaleModifyOnUsingScope.get() && isFirstPerson) {
            scale *= Float.isNaN(Clients.gunModelFovModify) ? 1 : Mth.clamp(Clients.gunModelFovModify / 70f, 0.5f, 1f);
        }
        muzzleFlashEntry.muzzleFlash.render(poseStack, bufferSource, muzzleFlashEntry.displayData, scale, lastShoot, isFirstPerson);
    }

    public void clearMuzzleFlashEntry() {
        this.muzzleFlashEntry = null;
    }

    public boolean isThirdPerson() {
        return transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    public void saveInLocal(String key, Object obj) {
        if (localRenderStorage == null) {
            localRenderStorage = new HashMap<>();
        }
        localRenderStorage.put(key, obj);
    }

    public Object getLocalSaved(String key) {
        if (localRenderStorage != null) {
            return localRenderStorage.get(key);
        }
        return null;
    }

    public boolean notContainsScope() {
        return attachmentsRenderContext == null || !attachmentsRenderContext.containsScope;
    }

    public boolean has(String slotName) {
        return attachmentsRenderContext != null && attachmentsRenderContext.has(slotName);
    }

    public boolean notHasMuzzle() {
        return !has(Attachment.MUZZLE);
    }

    public boolean notHasStock() {
        return !has(Attachment.STOCK);
    }

    public boolean notHasGrip() {
        return !has(Attachment.GRIP);
    }

    public boolean notHasScope() {
        return !has(Attachment.SCOPE);
    }

    public boolean notHasHandguard() {
        return !has(Attachment.HANDGUARD);
    }

    public boolean notHasMag() {
        return !has(Attachment.MAG);
    }

    public GunRenderContext renderScopeAttachment(ModelPart pose) {
        if (attachmentsRenderContext != null) {
            renderEntry(attachmentsRenderContext.slotLayer.get(Attachment.SCOPE), pose);
        }
        return this;
    }

    public GunRenderContext renderMuzzleAttachment(ModelPart pose) {
        if (attachmentsRenderContext != null) {
            renderEntry(attachmentsRenderContext.slotLayer.get(Attachment.MUZZLE), pose);
        }
        return this;
    }

    public void renderMagAttachmentIf(ModelPart pose, boolean condition) {
        if (attachmentsRenderContext != null && condition) {
            renderMagAttachment(pose);
        }
    }

    public void renderMagAttachment(ModelPart pose) {
        if (attachmentsRenderContext != null) {
            renderEntry(attachmentsRenderContext.slotLayer.get(Attachment.MAG), pose);
        }
    }

    public void renderAllAttachmentsLeft(ModelPart layer) {
        if (attachmentsRenderContext != null) {
            attachmentsRenderContext.renderAll(this, layer);
        }
    }

    public void renderEntry(AttachmentRenderEntry entry, ModelPart pose) {
        if (entry != null) {
            entry.render(this, pose);
        }
    }

    public void renderAttachmentEntry(String slotName, ModelPart pose) {
        renderEntry(attachmentsRenderContext.slotLayer.get(slotName), pose);
    }


    public boolean isEffectiveSight(AttachmentRenderEntry entry) {
        return isFirstPerson && Clients.MAIN_HAND_STATUS.adsProgress > 0.7f && Objects.equals(entry.slotUUID, Clients.getEffectiveSightUUID());
    }

    public boolean shouldShowLeftArm() {
        Player player = Minecraft.getInstance().player;
        if (gun == null || player == null) {
            return false;
        }
        if (!gun.canUseWithShield()) {
            return true;
        } else {
            return !(player.getOffhandItem().getItem() instanceof ShieldItem);
        }
    }

    public boolean shouldBulletRender() {
        return isFirstPerson && ReloadingHandler.isReloading() && (ammoLeft > 0 || ReloadingHandler.disFromLastReload(1000)) && !useLowQuality();
    }
}
