package sheridan.gcaa;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import sheridan.gcaa.capability.PlayerStatusEvents;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.events.Test;
import sheridan.gcaa.client.model.assets.ModelLoader;
import sheridan.gcaa.client.model.modelPart.LayerDefinition;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.network.PacketHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GCAA.MODID)
public class GCAA {

    public static final String MODID = "gcaa";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GCAA() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::onClientSetup));
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherDataEvent);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void gatherDataEvent(GatherDataEvent event) {
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(Test.class);
        LayerDefinition testModel = ModelLoader.loadModelAsset(new ResourceLocation(MODID, "custom_models/guns/glock19.geo.json"));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(PlayerStatusEvents.class);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilityEvent);
        PacketHandler.register();
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }


    public void attachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(PlayerStatusProvider.CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(MODID, "player_status"), new PlayerStatusProvider());
            }
        }
    }
}
