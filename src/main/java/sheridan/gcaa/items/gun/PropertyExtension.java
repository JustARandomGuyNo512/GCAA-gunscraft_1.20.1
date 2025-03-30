package sheridan.gcaa.items.gun;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import sheridan.gcaa.data.IJsonSyncable;

public abstract class PropertyExtension implements IJsonSyncable {
    public abstract String getName();
    public PropertyExtension() {}
    public abstract CompoundTag putExtendInitialData(final CompoundTag prevDataTag);
    public abstract boolean hasRateProperty(String name);

    @Override
    public void writeData(JsonObject jsonObject) {}

    @Override
    public void loadData(JsonObject jsonObject) {}
}
