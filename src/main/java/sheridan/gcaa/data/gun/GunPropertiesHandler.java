package sheridan.gcaa.data.gun;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.GunProperties;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = GCAA.MODID)
public class GunPropertiesHandler extends SimplePreparableReloadListener<Map<String, GunProperties>> {
    public static GunPropertiesHandler INSTANCE;

    @Override
    protected @NotNull Map<String, GunProperties> prepare(@NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        return new HashMap<>();
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        GunPropertiesHandler handler = new GunPropertiesHandler();
        event.addListener(handler);
        INSTANCE = handler;
    }

    @Override
    protected void apply(@NotNull Map<String, GunProperties> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {

    }
}
