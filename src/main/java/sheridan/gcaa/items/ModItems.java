package sheridan.gcaa.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.attachments.handguard.AKImprovedHandguard;
import sheridan.gcaa.items.attachments.muzzle.AKCompensator;
import sheridan.gcaa.items.attachments.muzzle.AKSuppressor;
import sheridan.gcaa.items.attachments.muzzle.PistolSuppressor;
import sheridan.gcaa.items.gun.guns.Akm;
import sheridan.gcaa.items.gun.guns.G19;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GCAA.MODID);

    public static final RegistryObject<G19> G19 = ITEMS.register("g19", G19::new);
    public static final RegistryObject<Akm> AKM = ITEMS.register("akm", Akm::new);

    public static final RegistryObject<UnknownAttachment> UNKNOWN_ATTACHMENT = ITEMS.register("unknown_attachment", UnknownAttachment::new);
    public static final RegistryObject<AKSuppressor> AK_SUPPRESSOR = ITEMS.register("ak_suppressor", AKSuppressor::new);
    public static final RegistryObject<AKCompensator> AK_COMPENSATOR = ITEMS.register("ak_compensator", AKCompensator::new);
    public static final RegistryObject<PistolSuppressor> PISTOL_SUPPRESSOR = ITEMS.register("pistol_suppressor", PistolSuppressor::new);
    public static final RegistryObject<AKImprovedHandguard> AK_IMPROVED_HANDGUARD = ITEMS.register("ak_improved_handguard", AKImprovedHandguard::new);
}
