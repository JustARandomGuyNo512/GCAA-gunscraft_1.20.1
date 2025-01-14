package sheridan.gcaa.addon;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.resource.DelegatingPackResources;
import net.minecraftforge.resource.PathPackResources;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.ModTabs;
import sheridan.gcaa.items.BaseItem;
import sheridan.gcaa.items.ModItems;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AddonHandler {
    private Pack pack;
    private final Map<String, Addon> addonMap = new HashMap<>();
    private final RepositorySource repositorySource = new RepositorySource() {
        @Override
        public void loadPacks(@NotNull Consumer<Pack> pOnLoad) {
            if (!addonMap.isEmpty() &&
                    pack != null) {
                pOnLoad.accept(pack);
            }
        }
    };
    public static final AddonHandler INSTANCE = new AddonHandler();

    public RepositorySource getRepositorySource() {
        return repositorySource;
    }

    public void readAddonPack(Dist dist) {
        Path path = FMLPaths.GAMEDIR.get().resolve("gcaa_addons");
        GCAA.LOGGER.info("Start addon scanning in dir: " + path);
        List<PathPackResources> addonPacks = new ArrayList<>();
        boolean isClient = dist.isClient();
        PackType packType = isClient ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;
        scanAddon(isClient, packType, path);
        for (Map.Entry<String, Addon> entry : addonMap.entrySet()) {
            Addon addon = entry.getValue();
            String name = entry.getKey();
            addonPacks.add(new PathPackResources(name, false, addon.path));
        }
        pack = Pack.readMetaAndCreate(
                "gcaa_addons",
                Component.literal("GCAA ADDON LOADER"),
                true,
                (id) -> new DelegatingPackResources(
                        id,
                        false,
                        new PackMetadataSection(
                                Component.literal("loaded: " + addonMap.values().size()),
                                SharedConstants.getCurrentVersion().getPackVersion(packType)),
                        addonPacks), packType,
                Pack.Position.BOTTOM,
                PackSource.DEFAULT);
    }

    private void scanAddon(boolean isClient, PackType packType, Path path) {
        GCAA.LOGGER.info("Scanning directory: " + path + "<=====");
        String directoryPath = path.toString();
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            GCAA.LOGGER.info("not a directory, scanning stopped...");
            return;
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            GCAA.LOGGER.info("empty directory, scanning stopped...");
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                GCAA.LOGGER.info("Directory: " + file.getName());
                try {
                    Addon addon = Addon.read(file.toPath());
                    if (addon.completed) {
                        addonMap.put(addon.name, addon);
                    }
                } catch (Exception e) {
                    GCAA.LOGGER.info("Error reading: " + file.toPath() + " " + e.getMessage());
                }
            }
        }
    }

    private void scanZipFile(File zipFile) throws IOException {
        throw new NotImplementedException();
        //TODO nothing
    }

    public void handleRegister(Dist dist) {
        for (Map.Entry<String, Addon> entry : addonMap.entrySet()) {
            String addonName  = entry.getKey();
            Addon addon = entry.getValue();
            ModTabs.MOD_TABS.register(addonName, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + addonName))
                    .icon(() -> new ItemStack(ModItems.G19.get()))
                    .displayItems((parameters, tab) -> {
                        for (Supplier<Item> supplier : addon.guns) {
                            tab.accept(supplier.get());
                        }
                    }).build());
        }
    }

}
