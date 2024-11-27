package sheridan.gcaa.attachmentSys;

import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.attachments.ReplaceableGunPart;

import java.util.*;

public class AttachmentSlot {
    public static final String ROOT = "__ROOT__";
    public static final String NONE = "__NONE__";
    public static final byte UPPER = 1;
    public static final byte LOWER = 2;
    public static final byte NO_DIRECTION = 0;
    public static final AttachmentSlot EMPTY = new AttachmentSlot(NONE, NONE, Set.of(), NONE, null, NONE, NO_DIRECTION);
    public final String slotName;
    public final String modelSlotName;
    private String attachmentId;
    private String id;
    private byte direction;
    private ReplaceableGunPart replaceableGunPart;

    private final Set<String> acceptedAttachments;
    private final Map<String, AttachmentSlot> children = new HashMap<>();
    private AttachmentSlot parent = EMPTY;
    private boolean root = false;
    private boolean locked = false;

    /**
     * * Create a root slot of an attachment tree.
     **/
    protected AttachmentSlot() {
        root = true;
        slotName = ROOT;
        modelSlotName = NONE;
        attachmentId = NONE;
        id = ROOT;
        direction = NO_DIRECTION;
        acceptedAttachments = Set.of();
        replaceableGunPart = null;
    }

    public static AttachmentSlot root() {
        return new AttachmentSlot();
    }
    
    protected AttachmentSlot(String slotName, String modelSlotName, Set<String> acceptedAttachments, String attachmentId, AttachmentSlot parent, String id, byte direction) {
        this.slotName = slotName;
        this.acceptedAttachments = new HashSet<>(acceptedAttachments);
        this.modelSlotName = modelSlotName;
        this.attachmentId = attachmentId;
        this.parent = parent;
        this.id = id;
        this.direction = direction;
    }

    public AttachmentSlot(String slotName, Set<String> acceptedAttachments, byte direction) {
        this(slotName, "s_" + slotName, acceptedAttachments, NONE, EMPTY, NONE, direction);
    }

    public AttachmentSlot(String slotName, Set<String> acceptedAttachments) {
        this(slotName, "s_" + slotName, acceptedAttachments, NONE, EMPTY, NONE, NO_DIRECTION);
    }

    public AttachmentSlot(String slotName, String modelSlotName, Set<String> acceptedAttachments, byte direction) {
        this(slotName, modelSlotName, acceptedAttachments, NONE, EMPTY, NONE, direction);
    }

    public AttachmentSlot(String slotName, String modelSlotName, Set<String> acceptedAttachments) {
        this(slotName, modelSlotName, acceptedAttachments, NONE, EMPTY, NONE, NO_DIRECTION);
    }

    public AttachmentSlot setReplaceableGunPart(ReplaceableGunPart replaceableGunPart) {
        this.replaceableGunPart = replaceableGunPart;
        return this;
    }

    @Nullable
    public ReplaceableGunPart getReplaceableGunPart() {
        return replaceableGunPart;
    }

    /**
     * * Returns a set of attachment item registry name that this slot accepts.
     * */
    public Set<String> getAcceptedAttachments() {
        return acceptedAttachments;
    }

    public boolean acceptsAttachment(String attachmentName) {
        return attachmentName != null && acceptedAttachments.contains(attachmentName);
    }

    public byte getDirection() {
        return direction;
    }

    public String getSlotName() {
        return slotName;
    }

    public String getModelSlotName() {
        return modelSlotName;
    }

    public AttachmentSlot getParent() {
        return parent;
    }

    public AttachmentSlot setParent(AttachmentSlot parent) {
        if (!isRoot() && this != EMPTY) {
            this.parent = parent;
        }
        return this;
    }

    public AttachmentSlot upper() {
        this.direction = UPPER;
        return this;
    }

    public AttachmentSlot lower() {
        this.direction = LOWER;
        return this;
    }

    public boolean hasParent() {
        return parent != EMPTY;
    }

    /**
     * Add a child slot to this slot.
     *
     * @param child the child slot to add.
     * @return current slot.
     * */
    public AttachmentSlot addChild(AttachmentSlot child) {
        if (child != null) {
            child.setParent(this);
            this.children.put(child.getSlotName(), child);
        }
        return this;
    }

    public AttachmentSlot removeChild(String slotName) {
        this.children.remove(slotName);
        return this;
    }

    public AttachmentSlot removeChild(AttachmentSlot slot) {
        this.children.remove(slot.getSlotName());
        return this;
    }

    public AttachmentSlot removeChildren(AttachmentSlot... children) {
        for (AttachmentSlot child : children) {
            this.children.remove(child.getSlotName());
        }
        return this;
    }

    public AttachmentSlot removeChildren(String... children) {
        for (String id : children) {
            this.children.remove(id);
        }
        return this;
    }

    public AttachmentSlot removeAllChildren() {
        this.children.clear();
        return this;
    }

    /**
     * Add a mix of child slots to this slot.
     *
     * @param children the mix of child slots to add.
     * @return current slot.
     * */
    public AttachmentSlot addChildren(AttachmentSlot... children) {
        for (AttachmentSlot child : children) {
            child.setParent(this);
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

    public void cleanAll() {
        if (this == EMPTY) {
            return;
        }
        this.attachmentId = NONE;
        if (hasChildren()) {
            for (AttachmentSlot child : children.values())  {
                child.cleanAll();
            }
        }
    }

    public void clean() {
        this.attachmentId = NONE;
    }

    public boolean isLocked() {
        return locked;
    }

    public AttachmentSlot setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public AttachmentSlot lock() {
        this.locked = true;
        return this;
    }

    public AttachmentSlot unlock() {
        this.locked = false;
        return this;
    }

    public boolean isRoot() {
        return root;
    }

    public AttachmentSlot copy() {
        return new AttachmentSlot(this.slotName, this.modelSlotName, this.acceptedAttachments, this.attachmentId, EMPTY, this.id, this.direction)
                .setReplaceableGunPart(this.replaceableGunPart).setLocked(this.isLocked());
    }

    public static AttachmentSlot copyAll(AttachmentSlot original) {
        if (original == null) {
            return null;
        }
        if (original == EMPTY) {
            return EMPTY;
        }
        AttachmentSlot copiedSlot = original.copy();
        for (Map.Entry<String, AttachmentSlot> entry : original.children.entrySet()) {
            AttachmentSlot childCopy = copyAll(entry.getValue());
            childCopy.parent = copiedSlot;
            copiedSlot.children.put(entry.getKey(), childCopy);
        }

        return copiedSlot;
    }

    public boolean isEmpty() {
        return NONE.equals(attachmentId);
    }

    public Map<String, AttachmentSlot> getChildren() {
        return children;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAttachmentId(String attachmentId) {
        if (acceptsAttachment(attachmentId)) {
            this.attachmentId = attachmentId;
        }
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public interface Visitor {
        void visit(AttachmentSlot slot);
    }

    public void onTravel(Visitor visitor) {
        visitor.visit(this);
        if (hasChildren()) {
            for (AttachmentSlot child : children.values()) {
                child.onTravel(visitor);
            }
        }
    }
}
