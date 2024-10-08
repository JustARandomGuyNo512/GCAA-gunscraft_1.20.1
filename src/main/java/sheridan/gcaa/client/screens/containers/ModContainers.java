package sheridan.gcaa.client.screens.containers;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;

import static net.minecraftforge.registries.ForgeRegistries.MENU_TYPES;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(MENU_TYPES, GCAA.MODID);

    public static final RegistryObject<MenuType<AttachmentsMenu>> ATTACHMENTS = REGISTER.register("attachments", () -> new MenuType<>(AttachmentsMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
