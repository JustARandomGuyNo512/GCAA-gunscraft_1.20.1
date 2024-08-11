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
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GunRenderContext {
    public static final Map<String, PoseStack> GLOBAL_POSE_STORAGE = new HashMap<>();
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
    public DisplayData.MuzzleFlashEntry muzzleFlashEntry;
    public AttachmentsRenderContext attachmentsRenderContext;
    public AttachmentRenderEntry leftArmRelocate;
    public AttachmentRenderEntry rightArmRelocate;
    public AttachmentRenderEntry scope;
    public PoseStack[] localPoseStorage;
    public int ammoLeft;

    private static String lastAttachmentContextUUID = "不同意的请举手    没有！！！ 没有！！！ 没有！！！ 。。。";
    private static AttachmentsRenderContext tempAttachmentContext = null;
    public static GunRenderContext getLocalMainHand(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType, DisplayData.MuzzleFlashEntry muzzleFlashEntry, int packedLight, int packedOverlay) {
        GunRenderContext context = new GunRenderContext(bufferSource, poseStack, itemStack, gun, transformType, packedLight, packedOverlay, muzzleFlashEntry, Clients.lastShootMain() + 10L);
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

    public GunRenderContext(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType, int packedLight, int packedOverlay) {
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
    }

    public GunRenderContext(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType, int packedLight, int packedOverlay, DisplayData.MuzzleFlashEntry muzzleFlashEntry, long lastShoot) {
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
    }

    public boolean reloading() {
        return ReloadingHandler.INSTANCE.reloading();
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

    public void renderArmLong(ModelPart pose, boolean mainHand) {
        if (isFirstPerson) {
            PlayerArmRenderer.INSTANCE.renderLong(pose, packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }

    public void renderArm(ModelPart pose, boolean mainHand) {
        if (isFirstPerson) {
            PlayerArmRenderer.INSTANCE.render(pose, packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
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
    public GunRenderContext translateAndRotateTo(ModelPart posePart) {
        posePart.translateAndRotate(poseStack);
        return this;
    }

    public void renderMuzzleFlash(float scale) {
        if (muzzleFlashEntry != null) {
            muzzleFlashEntry.muzzleFlash.render(poseStack, bufferSource, muzzleFlashEntry.displayData, scale, lastShoot, isFirstPerson);
        }
    }

    /**
     * copy prev poseStack
     * */
    public PoseStack copyPrevPose() {
        return RenderAndMathUtils.copyPoseStack(poseStack);
    }

    public boolean isThirdPerson() {
        return transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    /**
     * copy and save a poseStack instance in local renderByModelSlotName context in given index(0~9);
     * */
    public void saveInLocal(int index, PoseStack poseStack) {
        if (localPoseStorage == null) {
            localPoseStorage = new PoseStack[10];
        }
        localPoseStorage[index] = RenderAndMathUtils.copyPoseStack(poseStack);
    }

    public void savePrevStack(int index) {
        saveInLocal(index, poseStack);
    }

    public PoseStack getLocalSavedPose(int index) {
        if (localPoseStorage != null && index >=0 && index <=9) {
            return localPoseStorage[index];
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

    public void renderAllAttachmentsLeft(ModelPart layer) {
        if (attachmentsRenderContext != null) {
            attachmentsRenderContext.renderAll(this, layer);
        }
    }

    public void renderScope(ModelPart pose) {
        if (attachmentsRenderContext != null) {
            attachmentsRenderContext.renderScope(this, pose);
        }
    }
}
