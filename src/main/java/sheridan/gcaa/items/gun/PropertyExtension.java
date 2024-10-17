package sheridan.gcaa.items.gun;

import net.minecraft.nbt.CompoundTag;

public abstract class PropertyExtension {
    public abstract String getName();
    public PropertyExtension() {}
    public abstract CompoundTag getExtendInitialData(final CompoundTag prevDataTag);
    public abstract boolean hasRateProperty(String name);
}
