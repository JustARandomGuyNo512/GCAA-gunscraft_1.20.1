package sheridan.gcaa.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.attachments.akStuff.AKImprovedDustCover;
import sheridan.gcaa.items.attachments.akStuff.AKRailBracket;
import sheridan.gcaa.items.attachments.arStuff.ARGasBlock;
import sheridan.gcaa.items.attachments.arStuff.ARStockTube;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.attachments.grips.VerticalGrip;
import sheridan.gcaa.items.attachments.handguard.AKImprovedHandguard;
import sheridan.gcaa.items.attachments.handguard.ARRailedHandguard;
import sheridan.gcaa.items.attachments.muzzle.*;
import sheridan.gcaa.items.attachments.scope.Acog;
import sheridan.gcaa.items.attachments.scope.ScopeX10;
import sheridan.gcaa.items.attachments.sight.Holographic;
import sheridan.gcaa.items.attachments.sight.MicroRedDot;
import sheridan.gcaa.items.attachments.sight.RedDot;
import sheridan.gcaa.items.gun.guns.*;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GCAA.MODID);

    public static final RegistryObject<G19> G19 = ITEMS.register("g19", G19::new);
    public static final RegistryObject<Python357> PYTHON_357 = ITEMS.register("python_357", Python357::new);
    public static final RegistryObject<Akm> AKM = ITEMS.register("akm", Akm::new);
    public static final RegistryObject<M4a1> M4A1 = ITEMS.register("m4a1", M4a1::new);
    public static final RegistryObject<Awp> AWP = ITEMS.register("awp", Awp::new);
    public static final RegistryObject<M870> M870 = ITEMS.register("m870", M870::new);
    public static final RegistryObject<M249> M249 = ITEMS.register("m249", M249::new);

    public static final RegistryObject<UnknownAttachment> UNKNOWN_ATTACHMENT = ITEMS.register("unknown_attachment", UnknownAttachment::new);
    public static final RegistryObject<AKSuppressor> AK_SUPPRESSOR = ITEMS.register("ak_suppressor", AKSuppressor::new);
    public static final RegistryObject<ARSuppressor> AR_SUPPRESSOR = ITEMS.register("ar_suppressor", ARSuppressor::new);
    public static final RegistryObject<ShotGunSuppressor> SHOTGUN_SUPPRESSOR = ITEMS.register("shotgun_suppressor", ShotGunSuppressor::new);
    public static final RegistryObject<SniperSuppressor> SNIPER_SUPPRESSOR = ITEMS.register("sniper_suppressor", SniperSuppressor::new);
    public static final RegistryObject<OspreySuppressor> OSPREY_SUPPRESSOR = ITEMS.register("osprey_suppressor", OspreySuppressor::new);
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
    public static final RegistryObject<GrenadeLauncher> GP_25 = ITEMS.register("gp_25", GrenadeLauncher::new);
    public static final RegistryObject<Acog> ACOG = ITEMS.register("acog", Acog::new);
    public static final RegistryObject<ARGasBlock> AR_GAS_BLOCK = ITEMS.register("ar_gas_block", ARGasBlock::new);
    public static final RegistryObject<ARStockTube> AR_STOCK_TUBE = ITEMS.register("ar_stock_tube", ARStockTube::new);
    public static final RegistryObject<ARRailedHandguard> AR_RAILED_HANDGUARD = ITEMS.register("ar_railed_handguard", ARRailedHandguard::new);
}
