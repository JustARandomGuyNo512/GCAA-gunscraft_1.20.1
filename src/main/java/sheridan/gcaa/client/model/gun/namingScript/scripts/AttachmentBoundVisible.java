package sheridan.gcaa.client.model.gun.namingScript.scripts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.gun.namingScript.IScript;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.IAttachment;

@OnlyIn(Dist.CLIENT)
public record AttachmentBoundVisible(String slot,
                                     IAttachment attachment) implements IScript<AttachmentBoundVisible> {
    public AttachmentBoundVisible(String slot, IAttachment attachment) {
        this.slot = "s_" + slot;
        this.attachment = attachment;
    }

    @Override
    public boolean value(GunRenderContext context) {
        AttachmentRenderEntry attachmentRenderEntry = context.getAttachmentRenderEntry(slot);
        return attachmentRenderEntry != null && attachmentRenderEntry.attachment == attachment;
    }

    @Override
    public boolean valueLowQuality(GunRenderContext context) {
        return value(context);
    }

    @Override
    public AttachmentBoundVisible parse(String script, GunModel gunModel) {
        if (script.indexOf('@') != -1) {
            String[] split = script.split("@");
            if (split.length != 2) {
                return null;
            }
            IAttachment attachment = AttachmentsRegister.get(split[1]);
            if (attachment != null) {
                return new AttachmentBoundVisible(split[0], attachment);
            }
        }
        return null;
    }
}
