package sheridan.gcaa.attachmentSys.client;

import java.util.Set;

public class AttachmentSlot {
    public final String slotName;
    private final float[] translation = new float[] {
            0, 0, 0,
            0, 0, 0,
            1, 1, 1
    };

    private final Set<String> acceptedAttachments;

    public AttachmentSlot(String slotName, Set<String> acceptedAttachments, float x, float y, float z, float rx , float ry, float rz, float sx, float sy, float sz) {
        this.slotName = slotName;
        this.acceptedAttachments = acceptedAttachments;
        translation[0] = x;
        translation[1] = y;
        translation[2] = z;
        translation[3] = rx;
        translation[4] = ry;
        translation[5] = rz;
        translation[6] = sx;
        translation[7] = sy;
        translation[8] = sz;
    }

    public AttachmentSlot(String slotName, Set<String> acceptedAttachments, float[] translation) {
        this.slotName = slotName;
        this.acceptedAttachments = acceptedAttachments;
        System.arraycopy(translation, 0, this.translation, 0, this.translation.length);
    }

    public float[] getTranslation() {
        return translation;
    }

    public Set<String> getAcceptedAttachments() {
        return acceptedAttachments;
    }

    public boolean acceptsAttachment(String attachmentName) {
        return acceptedAttachments.contains(attachmentName);
    }
}
