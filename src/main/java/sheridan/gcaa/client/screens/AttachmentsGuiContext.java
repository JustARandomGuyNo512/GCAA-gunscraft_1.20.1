package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.client.AttachmentSlot;
import sheridan.gcaa.client.model.guns.IGunModel;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AttachmentsGuiContext {
    private static final ResourceLocation EMPTY = new ResourceLocation(GCAA.MODID, "textures/gui/screen/empty.png");
    private static final ResourceLocation OCCUPIED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/occupied.png");
    private static final ResourceLocation EMPTY_SELECTED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/empty_selected.png");
    private static final ResourceLocation OCCUPIED_SELECTED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/occupied_selected.png");
    // public static final float Z_FACTOR = 5f;
    private AttachmentSlot attachmentSlot;
    private AttachmentSlot selected;
    private final Map<AttachmentSlot, Vector3f> guiPosMap = new HashMap<>();

    public AttachmentsGuiContext(AttachmentSlot attachmentSlot) {
        this.attachmentSlot = attachmentSlot;
        initPosMap(this.attachmentSlot);
    }

    private void initPosMap(AttachmentSlot attachmentSlot) {
        for (AttachmentSlot child : attachmentSlot.getChildren().values()) {
            guiPosMap.put(child, new Vector3f());
            if (!child.getChildren().isEmpty()) {
                initPosMap(child);
            }
        }
    }

    public void renderIcons(GuiGraphics guiGraphics) {
        for (Map.Entry<AttachmentSlot, Vector3f> entry : guiPosMap.entrySet()) {
            ResourceLocation texture = chooseTexture(entry.getKey());
            Vector3f pos = entry.getValue();
            guiGraphics.blit(texture, (int) pos.x, (int) pos.y,  0,0, 4, 4, 4, 4);
        }
    }

    public void updateIconPos(PoseStack poseStack, IGunModel gunModel) {
        updateIconPos(poseStack, gunModel, attachmentSlot);
    }

    private void updateIconPos(PoseStack poseStack, IGunModel gunModel, AttachmentSlot slot) {
        Map<String, AttachmentSlot> children = slot.getChildren();
        for (Map.Entry<String, AttachmentSlot> entry : children.entrySet()) {
            poseStack.pushPose();
            gunModel.handleSlotTranslate(poseStack, entry.getValue().getModelSlotName());
            updateScreenPosWhenRender(poseStack.last().pose(), entry.getValue());
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
        return slot == selected ? EMPTY_SELECTED : EMPTY;
    }

    public void onClick(float mx, float my) {

    }

}
