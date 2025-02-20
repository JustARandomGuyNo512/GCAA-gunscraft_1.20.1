package sheridan.gcaa.client.model.attachments.other;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class GlockMountModel implements IAttachmentModel, ISlotProviderModel {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/others/glock_mount.png");
    private final ModelPart root, mount_grip, mount_side, mount_scope, low, body;

    public GlockMountModel() {
        root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/others/glock_mount.geo.json")).bakeRoot().getChild("root");
        root.meshingAll();
        mount_grip = root.getChild("s_mount_grip");
        mount_side = root.getChild("s_mount_side");
        mount_scope = root.getChild("s_mount_scope");
        low = root.getChild("low");
        body = root.getChild("body");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
        root.translateAndRotate(poseStack);
        root.getChild(modelSlotName).translateAndRotate(poseStack);
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return root.hasChild(modelSlotName);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        root.copyFrom(pose);
        low.visible = context.useLowQuality();
        body.visible = !low.visible;
        context.render(root, context.solid(TEXTURE));
        AttachmentRenderEntry mountGrip = attachmentRenderEntry.getChild("s_mount_grip");
        AttachmentRenderEntry mountSide = attachmentRenderEntry.getChild("s_mount_side");
        AttachmentRenderEntry mountScope = attachmentRenderEntry.getChild("s_mount_scope");
        if (mountGrip != null || mountSide != null || mountScope != null) {
            context.pushPose().translateTo(root);
            context.renderEntry(mountGrip, mount_grip);
            context.renderEntry(mountSide, mount_side);
            context.renderEntry(mountScope, mount_scope);
            context.popPose();
        }
        root.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return root;
    }
}
