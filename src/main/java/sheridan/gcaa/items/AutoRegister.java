package sheridan.gcaa.items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.Map;

public interface AutoRegister {
    void clientRegister(Map.Entry<ResourceKey<Item>, Item> entry);
    void serverRegister(Map.Entry<ResourceKey<Item>, Item> entry);
}
