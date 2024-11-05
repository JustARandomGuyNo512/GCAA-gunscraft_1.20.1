package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ReplaceableGunPart implements IAttachment{
    private static final Map<String, Map.Entry<String, ReplaceableGunPart>> REGISTRY = new HashMap<>();
    public float weight;
    public final String ID;

    public ReplaceableGunPart(float weight) {
        super();
        this.weight = weight;
        ID = UUID.randomUUID().toString();
        register(ID, this);
    }

    public static ReplaceableGunPart get(String id) {
        Map.Entry<String, ReplaceableGunPart> entry = REGISTRY.get(id);
        return entry == null ? null : entry.getValue();
    }

    public static String getId(ReplaceableGunPart part) {
        Map.Entry<String, ReplaceableGunPart> entry = REGISTRY.get(part.ID);
        return entry == null ? null : entry.getKey();
    }

    public static void register(String id, ReplaceableGunPart replaceableGunPart) {
        REGISTRY.put(id, new Map.Entry<>() {
            @Override
            public String getKey() {
                return id;
            }

            @Override
            public ReplaceableGunPart getValue() {
                return replaceableGunPart;
            }

            @Override
            public ReplaceableGunPart setValue(ReplaceableGunPart value) {
                return value;
            }
        });
    }


    protected AttachResult canReplace(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return IAttachment.PASSED;
    }

    public void doSlotOperation(IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {}

    public void onOccupied(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.addWeight(data, - weight);
    }

    public void onEmpty(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.addWeight(data, weight);
    }

    @Override
    public final void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {}

    @Override
    public final void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {}

    @Override
    public final AttachResult canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {return PASSED;}

    @Override
    public final AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {return PASSED;}

    @Override
    public Attachment get() {return null;}
}
