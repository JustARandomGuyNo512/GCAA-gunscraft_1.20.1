package sheridan.gcaa.items.gun;

import net.minecraft.nbt.CompoundTag;

public abstract class PropertyExtension {
    public final String name;
    public PropertyExtension(String name) {
        this.name = name;
    }
    public abstract CompoundTag getExtendInitialData(final CompoundTag prevDataTag);
    public abstract boolean hasRateProperty(String name);
}
