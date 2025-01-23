package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.awt.*;
import java.util.*;
import java.util.List;

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
        return IAttachment.passed();
    }

    public void doSlotOperation(IGun gun, AttachmentSlot root, AttachmentSlot prevSlot, IAttachment attachment) {}

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
    public final AttachResult canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {return IAttachment.passed();}

    @Override
    public final AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {return IAttachment.passed();}

    @Override
    public Attachment get() {return null;}

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("tooltip.gcaa.replaceable_part"));
        if (this.weight > 0) {
            list.add(Component.translatable("tooltip.gun_info.weight").append("" + weight));
        }
        return list;
    }
}
