package sheridan.gcaa.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.attachments.akStuff.AKImprovedDustCover;
import sheridan.gcaa.items.attachments.akStuff.AKRailBracket;
import sheridan.gcaa.items.attachments.grips.VerticalGrip;
import sheridan.gcaa.items.attachments.handguard.AKImprovedHandguard;
import sheridan.gcaa.items.attachments.muzzle.AKCompensator;
import sheridan.gcaa.items.attachments.muzzle.AKSuppressor;
import sheridan.gcaa.items.attachments.muzzle.PistolSuppressor;
import sheridan.gcaa.items.attachments.scope.ScopeX10;
import sheridan.gcaa.items.attachments.sight.Holographic;
import sheridan.gcaa.items.attachments.sight.MicroRedDot;
import sheridan.gcaa.items.attachments.sight.RedDot;
import sheridan.gcaa.items.gun.guns.Akm;
import sheridan.gcaa.items.gun.guns.Awp;
import sheridan.gcaa.items.gun.guns.G19;
import sheridan.gcaa.items.gun.guns.M870;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GCAA.MODID);

    public static final RegistryObject<G19> G19 = ITEMS.register("g19", G19::new);
    public static final RegistryObject<Akm> AKM = ITEMS.register("akm", Akm::new);
    public static final RegistryObject<Awp> AWP = ITEMS.register("awp", Awp::new);
    public static final RegistryObject<M870> M870 = ITEMS.register("m870", M870::new);

    public static final RegistryObject<UnknownAttachment> UNKNOWN_ATTACHMENT = ITEMS.register("unknown_attachment", UnknownAttachment::new);
    public static final RegistryObject<AKSuppressor> AK_SUPPRESSOR = ITEMS.register("ak_suppressor", AKSuppressor::new);
    public static final RegistryObject<AKCompensator> AK_COMPENSATOR = ITEMS.register("ak_compensator", AKCompensator::new);
    public static final RegistryObject<PistolSuppressor> PISTOL_SUPPRESSOR = ITEMS.register("pistol_suppressor", PistolSuppressor::new);
    public static final RegistryObject<AKImprovedHandguard> AK_IMPROVED_HANDGUARD = ITEMS.register("ak_improved_handguard", AKImprovedHandguard::new);
    public static final RegistryObject<AKRailBracket> AK_RAIL_BRACKET = ITEMS.register("ak_rail_bracket", AKRailBracket::new);
    public static final RegistryObject<AKImprovedDustCover> AK_IMPROVED_DUST_COVER = ITEMS.register("ak_improved_dust_cover", AKImprovedDustCover::new);
    public static final RegistryObject<MicroRedDot> MICRO_RED_DOT = ITEMS.register("micro_red_dot", MicroRedDot::new);
    public static final RegistryObject<RedDot> RED_DOT = ITEMS.register("red_dot", RedDot::new);
    public static final RegistryObject<Holographic> HOLOGRAPHIC = ITEMS.register("holographic", Holographic::new);
    public static final RegistryObject<ScopeX10> SCOPE_X10 = ITEMS.register("scope_x10", ScopeX10::new);
    public static final RegistryObject<VerticalGrip> VERTICAL_GRIP = ITEMS.register("vertical_grip", VerticalGrip::new);
}
