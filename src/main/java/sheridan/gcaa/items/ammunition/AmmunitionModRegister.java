package sheridan.gcaa.items.ammunition;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AmmunitionModRegister {
    private static final Map<String, ModEntry> ammunitionModMap = new HashMap<>();
    private static final Map<Integer, String> idMapping = new HashMap<>();
    private static final ArrayList<IAmmunitionMod> all = new ArrayList<>();
    private static int count = 0;

    public static void registerAmmunitionMod(IAmmunitionMod ammunitionMod) {
        ammunitionModMap.put(ammunitionMod.getId().toString(), new ModEntry(count, ammunitionMod));
        idMapping.put(count, ammunitionMod.getId().toString());
        all.add(ammunitionMod);
        count++;
    }

    public static IAmmunitionMod getAmmunitionMod(String id) {
        ModEntry entry = ammunitionModMap.get(id);
        if (entry != null) {
            return entry.mod;
        }
        return null;
    }

    public static IAmmunitionMod getAmmunitionMod(ResourceLocation id) {
        return ammunitionModMap.get(id.toString()).mod;
    }

    public static int getModIndex(String id) {
        ModEntry entry = ammunitionModMap.get(id);
        if (entry != null) {
            return entry.index;
        }
        return -1;
    }

    public static String getModId(int index) {
        return idMapping.get(index);
    }

    public static IAmmunitionMod getByIndex(int index) {
        String id = idMapping.get(index);
        if (id == null) {
            return null;
        }
        return ammunitionModMap.get(id).mod;
    }

    public static ArrayList<IAmmunitionMod> getAll() {
        return all;
    }

    public static ModEntry getEntry(IAmmunitionMod mod) {
        return ammunitionModMap.get(mod.getId().toString());
    }

    public static ArrayList<ModEntry> getAllEntries() {
        return new ArrayList<>(ammunitionModMap.values());
    }

    public record ModEntry(int index, IAmmunitionMod mod) {}
}
