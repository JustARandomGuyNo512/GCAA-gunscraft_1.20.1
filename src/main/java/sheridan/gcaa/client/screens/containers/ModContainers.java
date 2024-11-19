package sheridan.gcaa.client.screens.containers;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;

import static net.minecraftforge.registries.ForgeRegistries.MENU_TYPES;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(MENU_TYPES, GCAA.MODID);

    public static final RegistryObject<MenuType<GunModifyMenu>> ATTACHMENTS = REGISTER.register("attachments", () -> new MenuType<>(GunModifyMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<MenuType<AmmunitionModifyMenu>> AMMUNITION_MODIFY_MENU = REGISTER.register("ammunition_modify_menu", () -> new MenuType<>(AmmunitionModifyMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<MenuType<VendingMachineMenu>> VENDING_MACHINE_MENU = REGISTER.register("vending_machine_menu", () -> new MenuType<>(VendingMachineMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
