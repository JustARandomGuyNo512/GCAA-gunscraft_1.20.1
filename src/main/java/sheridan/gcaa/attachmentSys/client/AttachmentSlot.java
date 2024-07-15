package sheridan.gcaa.attachmentSys.client;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AttachmentSlot {
    public static final AttachmentSlot EMPTY = new AttachmentSlot();
    public static final String ROOT = "ROOT";
    public final String slotName;

    private final Set<String> acceptedAttachments;
    private final Map<String, AttachmentSlot> children = new HashMap<>();
    private boolean root = false;
    private boolean locked = false;
    private int depth = 0;

    /**
     * Create a root slot of an attachment tree.
     **/
    public AttachmentSlot() {
        root = true;
        slotName = ROOT;
        acceptedAttachments = Collections.emptySet();
    }


    public AttachmentSlot(String slotName, Set<String> acceptedAttachments) {
        this.slotName = slotName;
        this.acceptedAttachments = acceptedAttachments;

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

    /**
     * Add a child slot to this slot.
     *
     * @param child the child slot to add.
     * @return current slot.
     * */
    public AttachmentSlot addChild(AttachmentSlot child) {
        if (child != null) {
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
        AttachmentSlot slot = new AttachmentSlot(this.slotName, this.acceptedAttachments);
        if (hasChildren()) {
            for (AttachmentSlot child : children.values()) {
                slot.addChild(child.copy());
            }
        }
        return slot;
    }

}
