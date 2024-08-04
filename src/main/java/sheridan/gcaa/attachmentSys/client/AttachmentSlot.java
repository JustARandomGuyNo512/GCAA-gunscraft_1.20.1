package sheridan.gcaa.attachmentSys.client;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AttachmentSlot {
    public static final AttachmentSlot EMPTY = new AttachmentSlot();
    public static final String ROOT = "__ROOT__";
    public static final String NONE = "__NONE__";
    public final String slotName;
    public final String modelSlotName;
    public String attachmentId;

    private final Set<String> acceptedAttachments;
    private final Map<String, AttachmentSlot> children = new HashMap<>();
    private boolean root = false;
    private boolean locked = false;
    public int depth = 0;

    /**
     * Create a root slot of an attachment tree.
     **/
    protected AttachmentSlot() {
        root = true;
        slotName = ROOT;
        modelSlotName = NONE;
        attachmentId = NONE;
        acceptedAttachments = Collections.emptySet();
    }

    public static AttachmentSlot root() {
        return new AttachmentSlot();
    }

    public AttachmentSlot(String slotName, String modelSlotName, Set<String> acceptedAttachments, String attachmentId) {
        this.slotName = slotName;
        this.acceptedAttachments = acceptedAttachments;
        this.modelSlotName = modelSlotName;
        this.attachmentId = attachmentId;
    }

    public AttachmentSlot(String slotName, Set<String> acceptedAttachments) {
        this(slotName, "s_" + slotName, acceptedAttachments, NONE);
    }

    public AttachmentSlot(String slotName, Set<String> acceptedAttachments, String defaultAttachmentId) {
        this(slotName, "s_" + slotName, acceptedAttachments, defaultAttachmentId);
    }

    /**
     * Returns a mix of attachment item registry name that this slot accepts.
     * */
    public Set<String> getAcceptedAttachments() {
        return acceptedAttachments;
    }

    public boolean acceptsAttachment(String attachmentName) {
        return acceptedAttachments.contains(attachmentName);
    }

    public String getSlotName() {
        return slotName;
    }

    public String getModelSlotName() {
        return modelSlotName;
    }

    /**
     * Add a child slot to this slot.
     *
     * @param child the child slot to add.
     * @return current slot.
     * */
    public AttachmentSlot addChild(AttachmentSlot child) {
        if (child != null) {
            child.depth = this.depth + 1;
            this.children.put(child.getSlotName(), child);
        }
        return this;
    }

    /**
     * Add a mix of child slots to this slot.
     *
     * @param children the mix of child slots to add.
     * @return current slot.
     * */
    public AttachmentSlot addChildren(Set<AttachmentSlot> children) {
        for (AttachmentSlot child : children) {
            child.depth = this.depth + 1;
            this.children.put(child.getSlotName(), child);
        }
        return this;
    }

    /**
     * Returns true if this slot has any children.
     * */
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    /**
     * Gets the child slot by name. Not search its child slot.
     * */
    public AttachmentSlot getChild(String name) {
        return this.children.get(name);
    }

    /**
     * Searches the child slot by name. If not found, search its child slot.
     * */
    public AttachmentSlot searchChild(String name) {
        return searchChild(this, name);
    }

    /**
     * Searches the child slot by name of the given slot. If not found, search its child slot.
     * */
    public AttachmentSlot searchChild(AttachmentSlot slot, String name) {
        AttachmentSlot child = slot.getChild(name);
        if (child != null) {
            return child;
        }
        for (AttachmentSlot s : slot.children.values()) {
            child = searchChild(s, name);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    public boolean isLocked() {
        return locked;
    }

    public AttachmentSlot setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public boolean isRoot() {
        return root;
    }

    /**
     * Deep copy this slot and its children.
     * */
    public AttachmentSlot copy() {
        AttachmentSlot slot = new AttachmentSlot(this.slotName, this.modelSlotName, this.acceptedAttachments, this.attachmentId);
        if (hasChildren()) {
            for (AttachmentSlot child : children.values()) {
                slot.addChild(child.copy());
            }
        }
        return slot;
    }

    public boolean isEmpty() {
        return NONE.equals(attachmentId);
    }

    public Map<String, AttachmentSlot> getChildren() {
        return children;
    }
}
