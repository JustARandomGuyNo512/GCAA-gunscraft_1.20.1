package sheridan.gcaa.items.gun;

import net.minecraft.nbt.CompoundTag;

public abstract class PropertyExtension {
    public final String name;
    PropertyExtension(String name) {
        this.name = name;
    }
    abstract void extendInitialData(CompoundTag dataTag);
    abstract boolean hasRateProperty();
}
