package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
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

    public boolean renderLongArm = false;

    private static String lastAttachmentContextUUID = "none";
    private static AttachmentsRenderContext tempAttachmentContext = null;
    public static GunRenderContext getLocalMainHand(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType, DisplayData.MuzzleFlashEntry muzzleFlashEntry, int packedLight, int packedOverlay) {
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

    public boolean reloading() {
        return ReloadingHandler.INSTANCE.reloading();
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

    public VertexConsumer getBuffer(RenderType renderType) {
        return bufferSource.getBuffer(renderType);
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

    public void renderArmLong(ModelPart pose, boolean mainHand) {
        if (isFirstPerson) {
            if (!shouldRenderArmImmediately(mainHand, pose)) {
                renderLongArm = true;
                return;
            }
            PlayerArmRenderer.INSTANCE.renderLong(pose, packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }

    public void renderArm(ModelPart pose, boolean mainHand) {
        if (isFirstPerson && shouldRenderArmImmediately(mainHand, pose)) {
            PlayerArmRenderer.INSTANCE.render(pose, packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }

    private boolean shouldRenderArmImmediately(boolean mainHand, ModelPart pose) {
        AttachmentSlot armReplace = mainHand ? Clients.mainHandStatus.getRightArmReplace() : Clients.mainHandStatus.getLeftArmReplace();
        if (armReplace != null) {
            String key = mainHand ? RIGHT_ARM_RENDER_REPLACE : LEFT_ARM_RENDER_REPLACE;
            PoseStack poseStack = RenderAndMathUtils.copyPoseStack(this.poseStack);
            pose.translateAndRotate(poseStack);
            saveInLocal(key, poseStack);
            return false;
        }
        return true;
    }

    public void renderArmLong(PoseStack poseStack, boolean mainHand) {
        if (isFirstPerson) {
            PlayerArmRenderer.INSTANCE.renderLong(packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }

    public void renderArm(PoseStack poseStack, boolean mainHand) {
        if (isFirstPerson) {
            PlayerArmRenderer.INSTANCE.render(packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }

    public GunRenderContext pushPose() {
        poseStack.pushPose();
        return this;
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

    public void renderMuzzleFlash(float scale) {
        if (muzzleFlashEntry != null) {
            muzzleFlashEntry.muzzleFlash.render(poseStack, bufferSource, muzzleFlashEntry.displayData, scale, lastShoot, isFirstPerson);
        }
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

    public boolean has(String slotName) {
        return attachmentsRenderContext != null && attachmentsRenderContext.has(slotName);
    }

    public boolean hasMuzzle() {
        return has(Attachment.MUZZLE);
    }

    public boolean hasStock() {
        return has(Attachment.STOCK);
    }

    public boolean hasGrip() {
        return has(Attachment.GRIP);
    }

    public boolean hasScope() {
        return has(Attachment.SCOPE);
    }

    public boolean hasHandguard() {
        return has(Attachment.HANDGUARD);
    }

    public boolean hasMag() {
        return has(Attachment.MAG);
    }

    public GunRenderContext renderScope(ModelPart pose) {
        if (attachmentsRenderContext != null) {
            renderEntry(attachmentsRenderContext.slotLayer.get(Attachment.SCOPE), pose);
        }
        return this;
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

    public boolean isEffectiveSight(AttachmentRenderEntry entry) {
        return isFirstPerson && Clients.mainHandStatus.adsProgress > 0.7f && Objects.equals(entry.slotUUID, Clients.getEffectiveSightUUID());
    }
}
