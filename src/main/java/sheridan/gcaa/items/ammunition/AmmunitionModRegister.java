package sheridan.gcaa.items.ammunition;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AmmunitionModRegister {
    private static final Map<String, IAmmunitionMod> ammunitionModMap = new HashMap<>();

    public static void registerAmmunitionMod(IAmmunitionMod ammunitionMod) {
        ammunitionModMap.put(ammunitionMod.getId().toString(), ammunitionMod);
    }

    public static IAmmunitionMod getAmmunitionMod(String id) {
        return ammunitionModMap.get(id);
    }

    public static IAmmunitionMod getAmmunitionMod(ResourceLocation id) {
        return ammunitionModMap.get(id.toString());
    }
}
