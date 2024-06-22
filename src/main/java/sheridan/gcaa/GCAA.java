package sheridan.gcaa;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import sheridan.gcaa.capability.PlayerStatusEvents;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.events.*;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.network.PacketHandler;

@Mod(GCAA.MODID)
public class GCAA {

    public static final String MODID = "gcaa";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final int INNER_VERSION = 1;

    public GCAA() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::onClientSetup));
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(modEventBus);
        ModTabs.MOD_TABS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherDataEvent);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void gatherDataEvent(GatherDataEvent event) {
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(Test.class);
        MinecraftForge.EVENT_BUS.register(RenderEvents.class);
        MinecraftForge.EVENT_BUS.register(ControllerEvents.class);
        MinecraftForge.EVENT_BUS.register(ClientPlayerEvents.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        Clients.onSetUp(event);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(PlayerStatusEvents.class);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilityEvent);
        PacketHandler.register();
    }


    public void attachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(PlayerStatusProvider.CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(MODID, "player_status"), new PlayerStatusProvider());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {

        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
            KeyBinds.register(event);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onRegisterParticleFactories(RegisterParticleProvidersEvent event) {

        }

    }
}
