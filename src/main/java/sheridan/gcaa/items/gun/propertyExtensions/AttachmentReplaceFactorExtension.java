package sheridan.gcaa.items.gun.propertyExtensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.PropertyExtension;

public class AttachmentReplaceFactorExtension extends PropertyExtension {
    public static final String NAME = new ResourceLocation(GCAA.MODID, "attachment_replace_factor_extension").toString();

    public AttachmentReplaceFactorExtension() {
        super(NAME);
    }

    public void onAttachmentAttached(IGun gun, ItemStack stack, CompoundTag propertiesTag, IAttachment attachment, String slotName) {

    }

    public void onAttachmentDetached(IGun gun, ItemStack stack, CompoundTag propertiesTag, IAttachment attachment, String slotName) {

    }

    @Override
    public CompoundTag getExtendInitialData(CompoundTag prevDataTag) {
        return null;
    }

    @Override
    public boolean hasRateProperty(String name) {
        return false;
    }
}
