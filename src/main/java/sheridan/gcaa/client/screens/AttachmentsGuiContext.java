package sheridan.gcaa.client.screens;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.ISubSlotProvider;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AttachmentsGuiContext {
    private static final ResourceLocation EMPTY = new ResourceLocation(GCAA.MODID, "textures/gui/screen/empty.png");
    private static final ResourceLocation OCCUPIED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/occupied.png");
    private static final ResourceLocation EMPTY_SELECTED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/empty_selected.png");
    private static final ResourceLocation OCCUPIED_SELECTED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/occupied_selected.png");
    private static final Vector3f OUT_SCREEN = new Vector3f(-1, -1, -1);
    private final AttachmentSlotProxy proxy;
    private final AttachmentSlot root;
    private AttachmentSlot selected;
    private final Map<AttachmentSlot, Vector3f> guiPosMap = new HashMap<>();

    public AttachmentsGuiContext(IGun gun, AttachmentSlot root) {
        this.root = root;
        this.proxy = AttachmentsRegister.getProxiedAttachmentSlot(gun, root);
        initPosMap(this.root);
    }

    public AttachmentSlot getRoot() {
        return this.root;
    }

    public AttachmentSlotProxy getProxy() {
        return this.proxy;
    }

    public AttachmentSlot getSelected() {
        return selected;
    }

    private void initPosMap(AttachmentSlot attachmentSlot) {
        for (AttachmentSlot child : attachmentSlot.getChildren().values()) {
            guiPosMap.put(child, new Vector3f());
            if (!child.getChildren().isEmpty()) {
                initPosMap(child);
            }
        }
    }

    public void renderIcons(GuiGraphics guiGraphics, Font font) {
        for (Map.Entry<AttachmentSlot, Vector3f> entry : guiPosMap.entrySet()) {
            if (entry.getKey().isLocked()) {
                continue;
            }
            ResourceLocation texture = chooseTexture(entry.getKey());
            Vector3f pos = entry.getValue();
            if (pos.z < 0.05f)  {
                continue;
            }
            proxy.preSlotIconRender(entry.getKey(), pos, guiGraphics, font, this);
            int scale = entry.getKey() == selected ? 6 : 4;
            guiGraphics.blit(texture, (int) pos.x - scale / 2, (int) pos.y - scale / 2,  0,0, scale, scale, scale, scale);
        }
    }

    public void updateIconPos(PoseStack poseStack, ISlotProviderModel model) {
        updateIconPos(poseStack, model, root);
    }

    private void updateIconPos(PoseStack poseStack, ISlotProviderModel model, AttachmentSlot slot) {
        Map<PoseStack, Pair<AttachmentSlot, ISlotProviderModel>> subSlotMap = new HashMap<>();
        updateModelLayerSlots(poseStack, model, slot, subSlotMap);
        if (!subSlotMap.isEmpty()) {
            for (Map.Entry<PoseStack, Pair<AttachmentSlot, ISlotProviderModel>> entry : subSlotMap.entrySet()) {
                updateIconPos(entry.getKey(), entry.getValue().second, entry.getValue().first);
            }
        }
    }

    private void updateModelLayerSlots(PoseStack poseStack, ISlotProviderModel model, AttachmentSlot slot, Map<PoseStack, Pair<AttachmentSlot, ISlotProviderModel>> subSlotMap) {
        Map<String, AttachmentSlot> children = slot.getChildren();
        for (Map.Entry<String, AttachmentSlot> entry : children.entrySet()) {
            AttachmentSlot child = entry.getValue();
            poseStack.pushPose();
            if (model.hasSlot(child.modelSlotName)) {
                model.handleSlotTranslate(poseStack, child.getModelSlotName());
                updateScreenPosWhenRender(poseStack.last().pose(), child);
                IAttachment attachment = AttachmentsRegister.get(child.getAttachmentId());
                if (attachment instanceof ISubSlotProvider) {
                    IAttachmentModel attachmentModel = AttachmentsRegister.getModel(attachment);
                    if (attachmentModel instanceof ISlotProviderModel slotProviderModel) {
                        subSlotMap.put(RenderAndMathUtils.copyPoseStack(poseStack), Pair.of(child, slotProviderModel));
                    }
                }
                if (!child.getChildren().isEmpty()) {
                    updateModelLayerSlots(poseStack, model, child, subSlotMap);
                }
            }
            poseStack.popPose();
        }
    }


    private void updateScreenPosWhenRender(Matrix4f matrix4f, AttachmentSlot slot) {
        if (guiPosMap.containsKey(slot))  {
            Matrix4f m0 = new Matrix4f(RenderSystem.getModelViewMatrix());
            Matrix4f m1 = new Matrix4f(RenderSystem.getProjectionMatrix());
            Matrix4f m2 = new Matrix4f(matrix4f);
            Vector4f vector4f = m2.transform(new Vector4f(0, 0, 0, 1.0F));
            Vector4f v = vector4f.mul(m0).mul(m1);
//            if (Math.abs((v.x / v.w)) > 1 || Math.abs((v.y / v.w)) > 1 || v.z < 0.05) {
//                guiPosMap.put(slot, OUT_SCREEN);
//                return;
//            }
            float w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            float h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            float screenX = ((v.x / v.w) * w + w) * 0.5f;
            float screenY = (-(v.y / v.w) * h + h) * 0.5f;
            float screenDepth = v.z;
            Vector3f pos = new Vector3f(screenX, screenY, screenDepth);
            guiPosMap.put(slot, pos);
        }
    }

    private ResourceLocation chooseTexture(AttachmentSlot slot) {
        return slot == selected ?
                (slot.isEmpty() ? EMPTY_SELECTED : OCCUPIED_SELECTED) :
                (slot.isEmpty() ? EMPTY : OCCUPIED);
    }

    public boolean onClick(int mx, int my) {
        float minDis = Float.MAX_VALUE;
        boolean hasSelected = false;
        for (Map.Entry<AttachmentSlot, Vector3f> entry : guiPosMap.entrySet()) {
            if (entry.getKey().isLocked()) {
                continue;
            }
            Vector3f pos = entry.getValue();
            if (OUT_SCREEN == pos) {
                continue;
            }
            float dx = (int)(pos.x) - mx;
            float dy = (int)(pos.y) - my;
            float dis = (dx * dx + dy * dy);
            if (dis < (entry.getKey() == selected ? 10 : 5)) {
                if (dis < minDis) {
                    minDis = dis;
                    selected = entry.getKey();
                    hasSelected = true;
                }
            }
        }
        return hasSelected;
    }

}
