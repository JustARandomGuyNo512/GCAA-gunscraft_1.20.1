package sheridan.gcaa;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.proxies.*;
import sheridan.gcaa.industrial.AmmunitionRecipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.replaceableParts.*;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.*;

import java.util.*;
import java.util.stream.Collectors;

import static sheridan.gcaa.items.attachments.Attachment.MUZZLE;
import static sheridan.gcaa.items.attachments.Attachment.STOCK;
import static sheridan.gcaa.items.attachments.Attachment.GRIP;
import static sheridan.gcaa.items.attachments.Attachment.MAG;
import static sheridan.gcaa.items.attachments.Attachment.HANDGUARD;
import static sheridan.gcaa.items.attachments.Attachment.SCOPE;

public class Commons {
    public static long SERVER_START_TIME = System.currentTimeMillis();

    public static void onCommonSetUp(final FMLCommonSetupEvent event) {

        AttachmentsRegister.registerAttachmentSlot(ModItems.AKM.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ak_suppressor", "gcaa:ak_compensator")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ak_extend_mag", "gcaa:drum_ak")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot("rail_set", Set.of("gcaa:ak_rail_bracket", "gcaa:okp7_a")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:ak_improved_handguard")).setReplaceableGunPart(new WeightPart(1)))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube", "gcaa:ak_tactical_stock")).setReplaceableGunPart(new RecoilControlPart(1, 0.1f, 0.1f)))
                .addChild(new AttachmentSlot("dust_cover", Set.of("gcaa:ak_improved_dust_cover")).setReplaceableGunPart(new WeightPart(0.3f))),
                 AkmAttachmentProxy::new
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M4A1.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ar_suppressor", "gcaa:ar_compensator")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ar_extend_mag", "gcaa:mag_sure_fire_60")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of(
                        "gcaa:ar_railed_handguard",
                        "gcaa:ar_light_handguard_short",
                        "gcaa:ar_light_handguard"
                )).setReplaceableGunPart(new RecoilControlPart(0.8f, 0.05f, 0.05f)))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ctr_stock", "gcaa:ubr_stock")).setReplaceableGunPart(new WeightPart(1)))
                .addChild(new AttachmentSlot("gas_block", Set.of("gcaa:ar_gas_block")))
                .addChild(new AttachmentSlot(SCOPE, Set.of(
                        "gcaa:red_dot",
                        "gcaa:holographic",
                        "gcaa:acog",
                        "gcaa:elcan",
                        "gcaa:okp7_b"
                )).setReplaceableGunPart(new WeightPart(0.5f))),
                M4a1AttachmentProxy::new
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.G19.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:pistol_suppressor", "gcaa:osprey_suppressor")))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:micro_red_dot")))
                .addChild(new AttachmentSlot(GRIP, Set.of("gcaa:micro_laser_sight", "gcaa:micro_flashlight", "gcaa:glock_mount")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:glock_extend_mag")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.PYTHON_357.get(), AttachmentSlot.EMPTY);

        AttachmentsRegister.registerAttachmentSlot(ModItems.AWP.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:sniper_suppressor")).setReplaceableGunPart(new RecoilLowerPart(0, 0.15f, 0.15f)))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:scope_x10", "gcaa:acog", "gcaa:okp7_b","gcaa:elcan")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:sniper_extend_mag")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M870.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:shotgun_suppressor")))
                .addChild(new AttachmentSlot(STOCK, Set.of()))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog", "gcaa:elcan", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:shotgun_extend_bay")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of()))
        );

        Set<String> mk47HandguardSlot = new HashSet<>(List.of(
                "gcaa:laser_sight",
                "gcaa:horizontal_laser_sight",
                "gcaa:rail_panel",
                "gcaa:rail_panel_short",
                "gcaa:flashlight"));

        Set<String> hkg28HandguardFrontSlot = new HashSet<>(List.of(
                "gcaa:laser_sight",
                "gcaa:horizontal_laser_sight",
                "gcaa:flashlight",
                "gcaa:rail_panel_short"
        ));

        Set<String> mk47HandguardScopeSlot = Set.of(
                "gcaa:red_dot",
                "gcaa:holographic",
                "gcaa:acog",
                "gcaa:horizontal_laser_sight",
                "gcaa:rail_panel",
                "gcaa:okp7_b",
                "gcaa:elcan",
                "gcaa:rail_panel_short"
        );

        Set<String> mk47HandguardGrip = Set.of(
                "gcaa:laser_sight",
                "gcaa:horizontal_laser_sight",
                "gcaa:flashlight",
                "gcaa:rail_panel",
                "gcaa:rail_panel_short",
                "gcaa:vertical_grip",
                "gcaa:gp_25",
                "gcaa:m203",
                "gcaa:slant_grip"
        );

        Set<String> hkg28HandguardScope = Set.of(
                "gcaa:red_dot",
                "gcaa:holographic",
                "gcaa:horizontal_laser_sight",
                "gcaa:scope_x10",
                "gcaa:acog",
                "gcaa:okp7_b",
                "gcaa:elcan",
                "gcaa:rail_panel",
                "gcaa:rail_panel_short"
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M249.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog","gcaa:elcan", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ar_suppressor", "gcaa:ar_compensator")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")).setReplaceableGunPart(new RecoilControlPart(1.2f, 0.1f, 0.08f)))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:m249_railed_handguard")).setReplaceableGunPart(new WeightPart(0.5f)).asSlotProvider())
                .addChild(new AttachmentSlot("handguard_grip", mk47HandguardGrip.stream().filter(s -> !s.equals("gcaa:gp_25") && !s.equals("gcaa:m203")).collect(Collectors.toSet())).lower().lock())
                .addChild(new AttachmentSlot("handguard_left", mk47HandguardSlot).lower().lock())
                .addChild(new AttachmentSlot("handguard_right", mk47HandguardSlot).lower().lock())
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.VECTOR_45.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog","gcaa:elcan", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:osprey_smg_suppressor"," gcaa:smg_suppressor", "gcaa:smg_compensator")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")).setReplaceableGunPart(new RecoilControlPart(0.7f, 0.05f, 0.06f)))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:vector_45_extend_mag", "gcaa:exp_mag_45_straight", "gcaa:drum_45_straight")))
                .addChild(new AttachmentSlot(GRIP, Set.of("gcaa:vertical_grip", "gcaa:rail_panel_short", "gcaa:laser_sight", "gcaa:flashlight", "gcaa:slant_grip")))
                .addChild(new AttachmentSlot("left",  Set.of("gcaa:laser_sight", "gcaa:flashlight", "gcaa:micro_flashlight")))
                .addChild(new AttachmentSlot("right", Set.of("gcaa:laser_sight", "gcaa:rail_panel_short", "gcaa:flashlight")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.XM1014.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog","gcaa:elcan", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:shotgun_suppressor")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.MK47.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog","gcaa:elcan", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:ar_light_handguard")).setReplaceableGunPart(new Mk47Handguard()).asSlotProvider())
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ak_extend_mag", "gcaa:drum_ak")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ctr_stock", "gcaa:ubr_stock")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ak_compensator", "gcaa:ak_suppressor")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot("hand_guard_scope", mk47HandguardScopeSlot).upper())
                .addChild(new AttachmentSlot("hand_guard_left", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("hand_guard_left_rear", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("hand_guard_right", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("hand_guard_right_rear", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("hand_guard_lower", hkg28HandguardFrontSlot).lower())
                .addChild(new AttachmentSlot("hand_guard_grip", mk47HandguardGrip).lower()),
                (slot) -> new GrenadeExclusiveProxy(slot, "hand_guard_lower", "hand_guard_grip")
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.HK_G28.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:scope_x10","gcaa:elcan", "gcaa:acog", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:sniper_suppressor", "gcaa:dmr_compensator")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ctr_stock", "gcaa:ubr_stock")))
                .addChild(new AttachmentSlot("handguard_scope", hkg28HandguardScope).upper())
                .addChild(new AttachmentSlot("handguard_left", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("handguard_right", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("handguard_front", hkg28HandguardFrontSlot).lower())
                .addChild(new AttachmentSlot("handguard_front_left", hkg28HandguardFrontSlot).lower())
                .addChild(new AttachmentSlot("handguard_front_right", hkg28HandguardFrontSlot).lower())
                .addChild(new AttachmentSlot(GRIP, mk47HandguardGrip).lower())
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:exp_mag7_62x51", "gcaa:drum_762x51"))),
                (slot) -> new GrenadeExclusiveProxy(slot, "grip", "handguard_front")
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.BERETTA_686.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot("rail_clamp", Set.of("gcaa:rail_clamp"))));

        AttachmentsRegister.registerAttachmentSlot(ModItems.AK12.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ak12_suppressor")).setReplaceableGunPart(new RecoilLowerPart(0, 0.15f, 0.15f)))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:exp_mag5_45x39", "gcaa:drum_545x39")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube", "gcaa:ak_tactical_stock")).setReplaceableGunPart(new RecoilControlPart(1, 0.12f, 0.12f)))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic","gcaa:elcan", "gcaa:acog", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot("handguard_grip", Set.of(
                        "gcaa:vertical_grip", "gcaa:gp_25", "gcaa:laser_sight", "gcaa:flashlight", "gcaa:slant_grip"
                )).lower())
                .addChild(new AttachmentSlot("handguard_left", Set.of(
                        "gcaa:laser_sight", "gcaa:flashlight"
                )).lower())
                .addChild(new AttachmentSlot("handguard_right", Set.of(
                        "gcaa:laser_sight", "gcaa:flashlight"
                )).lower())
                .addChild(new AttachmentSlot("handguard_scope", mk47HandguardScopeSlot).upper())
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.ANNIHILATOR.get(), AttachmentSlot.EMPTY);

        AttachmentsRegister.registerAttachmentSlot(ModItems.FN_BALLISTA.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:sniper_suppressor")).setReplaceableGunPart(new RecoilLowerPart(0, 0.1f, 0.1f)))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:scope_x10","gcaa:elcan", "gcaa:acog", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:sniper_extend_mag")))
                .addChild(new AttachmentSlot("rail_scope", hkg28HandguardScope).upper())
                .addChild(new AttachmentSlot("rail_lower", hkg28HandguardFrontSlot).lower())
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.MP5.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:osprey_smg_suppressor","gcaa:smg_suppressor", "gcaa:smg_compensator")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:exp_mag9x19", "gcaa:drum_9x19")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:mp5_rail_handguard")).setReplaceableGunPart(new WeightPart(0.6f)).asSlotProvider())
                .addChild(new AttachmentSlot("handguard_left", mk47HandguardSlot).lower().lock())
                .addChild(new AttachmentSlot("handguard_right", mk47HandguardSlot).lower().lock())
                .addChild(new AttachmentSlot("handguard_grip", Set.of(
                        "gcaa:laser_sight",
                        "gcaa:horizontal_laser_sight",
                        "gcaa:flashlight",
                        "gcaa:rail_panel",
                        "gcaa:rail_panel_short",
                        "gcaa:vertical_grip",
                        "gcaa:slant_grip"
                )).lower().lock())
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")).setReplaceableGunPart(new RecoilControlPart(1, 0.12f, 0.12f)))
                .addChild(new AttachmentSlot("rail_clamp", Set.of("gcaa:rail_clamp")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M60E4.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:sniper_suppressor", "gcaa:dmr_compensator")))
                .addChild(new AttachmentSlot(GRIP, Set.of("gcaa:laser_sight",
                        "gcaa:horizontal_laser_sight",
                        "gcaa:flashlight",
                        "gcaa:rail_panel",
                        "gcaa:rail_panel_short",
                        "gcaa:vertical_grip",
                        "gcaa:slant_grip")))
                .addChild(new AttachmentSlot("handguard_left", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("handguard_right", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:elcan", "gcaa:acog", "gcaa:okp7_b")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.FN57.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:pistol_suppressor", "gcaa:osprey_suppressor")))
                .addChild(new AttachmentSlot(GRIP, Set.of("gcaa:micro_laser_sight", "gcaa:micro_flashlight", "gcaa:glock_mount")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.MCX_SPEAR.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:dmr_compensator")).setReplaceableGunPart(new MCXSpearMuzzle()))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot("handguard_grip", Set.of(
                        "gcaa:laser_sight",
                        "gcaa:horizontal_laser_sight",
                        "gcaa:flashlight",
                        "gcaa:rail_panel",
                        "gcaa:rail_panel_short",
                        "gcaa:vertical_grip",
                        "gcaa:gp_25",
                        "gcaa:m203",
                        "gcaa:slant_grip"
                )))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog","gcaa:elcan", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot("handguard_scope", mk47HandguardScopeSlot).upper())
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ctr_stock", "gcaa:ubr_stock")).setReplaceableGunPart(new RecoilControlPart(1.1f, 0.13f, 0.1f)))
                .addChild(new AttachmentSlot("handguard_left", Set.of("gcaa:laser_sight", "gcaa:flashlight","gcaa:horizontal_laser_sight", "gcaa:rail_panel_short")).lower())
                .addChild(new AttachmentSlot("handguard_right", Set.of("gcaa:laser_sight", "gcaa:flashlight","gcaa:horizontal_laser_sight", "gcaa:rail_panel_short")).lower())
                .addChild(new AttachmentSlot("handguard_front", Set.of("gcaa:laser_sight", "gcaa:flashlight","gcaa:horizontal_laser_sight", "gcaa:rail_panel_short")).lower())
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:mcx_spear_exp_mag"))),
                (slot) -> new GrenadeExclusiveProxy(slot, "handguard_grip", "handguard_front")
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.AUG_A3.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ar_suppressor", "gcaa:ar_compensator")))
                .addChild(new AttachmentSlot(GRIP, Set.of(
                        "gcaa:vertical_grip", "gcaa:gp_25", "gcaa:laser_sight", "gcaa:flashlight", "gcaa:slant_grip"
                )).lower())
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog","gcaa:elcan", "gcaa:okp7_b")))
                .addChild(new AttachmentSlot("left", Set.of("gcaa:laser_sight", "gcaa:flashlight","gcaa:horizontal_laser_sight")).lower())
                .addChild(new AttachmentSlot("right", Set.of("gcaa:laser_sight", "gcaa:flashlight")).lower())
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ar_extend_mag")))
        );

        registerVendingMachineProducts();
        registerAmmunitionRecipes();
        checkVendingMachineProductsRegistry();
    }

    public static void checkVendingMachineProductsRegistry() {
        if (GCAA.ALLOW_DEBUG) {
            System.out.println("Esperance: Checking vending machine products registry...");
            List<Ammunition> allAmmo = Ammunition.getAll();
            Set<IProduct> ammunition = ProductsRegister.getProducts(ProductsRegister.AMMUNITION);
            for (Ammunition ammo : allAmmo) {
                if (!ammunition.contains(IProduct.of(ammo))) {
                    GCAA.LOGGER.error("Vending machine does not contain product for ammunition: " + ammo);
                }
            }
            Set<IProduct> attachments = ProductsRegister.getProducts(ProductsRegister.ATTACHMENT);
            List<Attachment> all = Attachment.getAllInstances();
            for (Attachment attachment : all) {
                if (!attachments.contains(IProduct.of(attachment))) {
                    GCAA.LOGGER.error("Vending machine does not contain product for attachment: " + attachment);
                }
            }
            Set<IProduct> guns = ProductsRegister.getProducts(ProductsRegister.GUN);
            List<Gun> allInstances = Gun.getAllInstances();
            for (Gun gun : allInstances) {
                if (!guns.contains(IProduct.of(gun))) {
                    GCAA.LOGGER.error("Vending machine does not contain product for gun: " + gun);
                }
            }
        }
    }

    public static void registerAmmunitionRecipes() {
        RecipeRegister.registerAmmunition(List.of(
                ModItems.AMMO_9X19MM.get(),
                ModItems.AMMO_5_7X28MM.get(),
                ModItems.AMMO_45ACP.get(),
                ModItems.AMMO_5_56X45MM.get(),
                ModItems.AMMO_7_62X39MM.get(),
                ModItems.AMMO_7_62X51MM.get(),
                ModItems.AMMO_6_8X51MM.get(),
                ModItems.AMMO_12GAUGE.get(),
                ModItems.AMMO_357MAGNUM.get(),
                ModItems.AMMO_VOG_25.get(),
                ModItems.AMMO_M433.get(),
                ModItems.AMMO_5_45X39MM.get(),
                ModItems.AMMO_338_LAPUA_MAGNUM.get()
        ), List.of (
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get()),
                        List.of(15, 10, 25), new AmmunitionRecipe(ModItems.AMMO_9X19MM.get(), 20 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, Items.IRON_INGOT),
                        List.of(12, 12, 15), new AmmunitionRecipe(ModItems.AMMO_5_7X28MM.get(), 25 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get()),
                        List.of(20, 10, 30), new AmmunitionRecipe(ModItems.AMMO_45ACP.get(), 30 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get()),
                        List.of(25, 18, 15), new AmmunitionRecipe(ModItems.AMMO_5_56X45MM.get(), 40 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get(), Items.IRON_INGOT),
                        List.of(10, 22, 18, 3), new AmmunitionRecipe(ModItems.AMMO_7_62X39MM.get(), 42 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get()),
                        List.of(30, 28, 25),  new AmmunitionRecipe(ModItems.AMMO_7_62X51MM.get(), 45 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get(), Items.IRON_INGOT),
                        List.of(25, 28, 20, 5),  new AmmunitionRecipe(ModItems.AMMO_6_8X51MM.get(), 50 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get(), ModItems.PLASTIC.get()),
                        List.of(6, 16, 40, 10), new AmmunitionRecipe(ModItems.AMMO_12GAUGE.get(), 30 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get()),
                        List.of(20, 12, 15),new AmmunitionRecipe(ModItems.AMMO_357MAGNUM.get(), 28 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, Items.TNT),
                        List.of(30, 10, 5), new AmmunitionRecipe(ModItems.AMMO_VOG_25.get(), 50 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, Items.TNT),
                        List.of(25, 15, 7), new AmmunitionRecipe(ModItems.AMMO_M433.get(), 40 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get()),
                        List.of(20, 16, 14), new AmmunitionRecipe(ModItems.AMMO_5_45X39MM.get(), 40 * 1000)),
                addIngredients(List.of(ModItems.THIN_COPPER_PLATE.get(), Items.GUNPOWDER, ModItems.LEAD_NUGGET.get()),
                        List.of(35, 30, 30),  new AmmunitionRecipe(ModItems.AMMO_338_LAPUA_MAGNUM.get(), 60 * 1000))
        ));
    }

    public static void registerVendingMachineProducts() {
        ProductsRegister.registerProducts(ProductsRegister.EXCHANGE,
                new CommonProduct(Items.GOLD_INGOT, 100),
                new CommonProduct(Items.GOLD_BLOCK, 900),
                new CommonProduct(Items.DIAMOND, 200),
                new CommonProduct(Items.DIAMOND_BLOCK, 1800),
                new CommonProduct(Items.IRON_INGOT, 20),
                new CommonProduct(Items.IRON_BLOCK, 180),
                new CommonProduct(Items.COPPER_INGOT, 30),
                new CommonProduct(Items.COPPER_BLOCK, 270),
                new CommonProduct(ModItems.ANNIHILATOR.get(), 500));

        ProductsRegister.registerProducts(ProductsRegister.GUN,
                new GunProduct(ModItems.G19.get(), 800),
                new GunProduct(ModItems.FN57.get(), 1000),
                new GunProduct(ModItems.PYTHON_357.get(), 700),
                new GunProduct(ModItems.VECTOR_45.get(), 1300),
                new GunProduct(ModItems.MP5.get(), 1100),
                new GunProduct(ModItems.AKM.get(), 2700),
                new GunProduct(ModItems.M4A1.get(), 2900),
                new GunProduct(ModItems.AK12.get(), 3000),
                new GunProduct(ModItems.MK47.get(), 3050),
                new GunProduct(ModItems.MCX_SPEAR.get(), 4200),
                new GunProduct(ModItems.AUG_A3.get(), 2700),
                new GunProduct(ModItems.AWP.get(), 4000),
                new GunProduct(ModItems.FN_BALLISTA.get(), 6000),
                new GunProduct(ModItems.HK_G28.get(), 4000),
                new GunProduct(ModItems.M870.get(), 2000),
                new GunProduct(ModItems.XM1014.get(), 2400),
                new GunProduct(ModItems.BERETTA_686.get(), 1800),
                new GunProduct(ModItems.M249.get(), 4400),
                new GunProduct(ModItems.M60E4.get(), 5500));

        ProductsRegister.registerProducts(ProductsRegister.AMMUNITION,
                new AmmunitionProduct(ModItems.AMMO_9X19MM.get(), 100),
                new AmmunitionProduct(ModItems.AMMO_5_7X28MM.get(), 200),
                new AmmunitionProduct(ModItems.AMMO_357MAGNUM.get(), 150),
                new AmmunitionProduct(ModItems.AMMO_45ACP.get(), 180),
                new AmmunitionProduct(ModItems.AMMO_7_62X39MM.get(), 300),
                new AmmunitionProduct(ModItems.AMMO_5_56X45MM.get(), 280),
                new AmmunitionProduct(ModItems.AMMO_5_45X39MM.get(), 240),
                new AmmunitionProduct(ModItems.AMMO_7_62X51MM.get(), 400),
                new AmmunitionProduct(ModItems.AMMO_6_8X51MM.get(), 350),
                new AmmunitionProduct(ModItems.AMMO_338_LAPUA_MAGNUM.get(), 600),
                new AmmunitionProduct(ModItems.AMMO_12GAUGE.get(), 200),
                new GrenadeProduct(ModItems.AMMO_VOG_25.get(), 140),
                new GrenadeProduct(ModItems.AMMO_M433.get(), 180));

        ProductsRegister.registerProducts(ProductsRegister.ATTACHMENT,
                new AttachmentProduct(ModItems.PISTOL_SUPPRESSOR.get(), 50),
                new AttachmentProduct(ModItems.SMG_SUPPRESSOR.get(), 80),
                new AttachmentProduct(ModItems.AK_SUPPRESSOR.get(), 110),
                new AttachmentProduct(ModItems.AR_SUPPRESSOR.get(), 130),
                new AttachmentProduct(ModItems.AK12_SUPPRESSOR.get(), 120),
                new AttachmentProduct(ModItems.SHOTGUN_SUPPRESSOR.get(), 150),
                new AttachmentProduct(ModItems.SNIPER_SUPPRESSOR.get(), 210),
                new AttachmentProduct(ModItems.OSPREY_SUPPRESSOR.get(), 150),
                new AttachmentProduct(ModItems.OSPREY_SMG_SUPPRESSOR.get(), 180),
                new AttachmentProduct(ModItems.AK_COMPENSATOR.get(), 180),
                new AttachmentProduct(ModItems.AR_COMPENSATOR.get(), 220),
                new AttachmentProduct(ModItems.SMG_COMPENSATOR.get(), 160),
                new AttachmentProduct(ModItems.DMR_COMPENSATOR.get(), 200),
                new AttachmentProduct(ModItems.AK_IMPROVED_HANDGUARD.get(), 270),
                new AttachmentProduct(ModItems.AK_RAIL_BRACKET.get(), 100),
                new AttachmentProduct(ModItems.RAIL_CLAMP.get(), 100),
                new AttachmentProduct(ModItems.GLOCK_MOUNT.get(), 110),
                new AttachmentProduct(ModItems.AK_IMPROVED_DUST_COVER.get(), 100),
                new AttachmentProduct(ModItems.MICRO_RED_DOT.get(), 75),
                new AttachmentProduct(ModItems.RED_DOT.get(), 102),
                new AttachmentProduct(ModItems.HOLOGRAPHIC.get(), 100),
                new AttachmentProduct(ModItems.OKP_7_A.get(), 70),
                new AttachmentProduct(ModItems.OKP_7_B.get(), 120),
                new AttachmentProduct(ModItems.ACOG.get(), 220),
                new AttachmentProduct(ModItems.ELCAN.get(), 300),
                new AttachmentProduct(ModItems.SCOPE_X10.get(), 400),
                new AttachmentProduct(ModItems.VERTICAL_GRIP.get(), 80),
                new AttachmentProduct(ModItems.SLANT_GRIP.get(), 100),
                new GrenadeLauncherProduct(ModItems.GP_25.get(), 240),
                new GrenadeLauncherProduct(ModItems.M203.get(), 300),
                new AttachmentProduct(ModItems.AR_GAS_BLOCK.get(), 30),
                new AttachmentProduct(ModItems.AR_STOCK_TUBE.get(), 60),
                new AttachmentProduct(ModItems.AR_RAILED_HANDGUARD.get(), 180),
                new AttachmentProduct(ModItems.AR_LIGHT_HANDGUARD_SHORT.get(), 210),
                new AttachmentProduct(ModItems.AR_LIGHT_HANDGUARD.get(), 300),
                new AttachmentProduct(ModItems.MP5_RAIL_HANDGUARD.get(), 150),
                new AttachmentProduct(ModItems.M249_RAILED_HANDGUARD.get(), 100),
                new AttachmentProduct(ModItems.EXP_MAG_45_STRAIGHT.get(), 160),
                new AttachmentProduct(ModItems.DRUM_45_STRAIGHT.get(), 280),
                new AttachmentProduct(ModItems.EXP_MAG9X19.get(), 150),
                new AttachmentProduct(ModItems.DRUM_9X19_ARC.get(), 300),
                new AttachmentProduct(ModItems.AR_EXTEND_MAG.get(), 150),
                new AttachmentProduct(ModItems.MAG_SURE_FIRE_60.get(), 450),
                new AttachmentProduct(ModItems.AK_EXTEND_MAG.get(), 170),
                new AttachmentProduct(ModItems.DRUM_AK.get(), 400),
                new AttachmentProduct(ModItems.EXP_MAG5_45X39.get(), 160),
                new AttachmentProduct(ModItems.DRUM_5_45X39.get(), 380),
                new AttachmentProduct(ModItems.EXP_MAG7_62X51.get(), 200),
                new AttachmentProduct(ModItems.MCX_SPEAR_EXP_MAG.get(), 100),
                new AttachmentProduct(ModItems.DRUM_7_62X51.get(), 500),
                new AttachmentProduct(ModItems.GLOCK_EXTEND_MAG.get(), 70),
                new AttachmentProduct(ModItems.VECTOR_45_EXTEND_MAG.get(), 130),
                new AttachmentProduct(ModItems.SNIPER_EXTEND_MAG.get(), 50),
                new AttachmentProduct(ModItems.SHOTGUN_EXTEND_BAY.get(), 70),
                new AttachmentProduct(ModItems.CTR_STOCK.get(), 170),
                new AttachmentProduct(ModItems.UBR_STOCK.get(), 150),
                new AttachmentProduct(ModItems.AK_TACTICAL_STOCK.get(), 200),
                new AttachmentProduct(ModItems.MICRO_LASER_SIGHT.get(), 30),
                new AttachmentProduct(ModItems.LASER_SIGHT.get(), 50),
                new AttachmentProduct(ModItems.MICRO_FLASHLIGHT.get(), 50),
                new AttachmentProduct(ModItems.FLASHLIGHT.get(), 70),
                new AttachmentProduct(ModItems.HORIZONTAL_LASER_SIGHT.get(), 60),
                new AttachmentProduct(ModItems.RAIL_PANEL.get(), 15),
                new AttachmentProduct(ModItems.RAL_PANEL_SHORT.get(), 10));

        CompoundTag potionTag = new CompoundTag();
        potionTag.putString("Potion", "minecraft:strong_regeneration");

        ProductsRegister.registerProducts(ProductsRegister.OTHER,
                new CommonProduct(Items.IRON_INGOT, 20),
                new CommonProduct(Items.GOLD_INGOT, 100),
                new CommonProduct(Items.DIAMOND, 200),
                new CommonProduct(ModItems.VENDING_MACHINE.get(), 200),
                new CommonProduct(ModItems.AMMUNITION_PROCESSOR.get(), 160),
                new CommonProduct(Items.COOKED_BEEF, 20),
                new CommonProduct(Items.CARROT, 5),
                new CommonProduct(Items.APPLE, 5),
                new CommonProduct(Items.GOLDEN_APPLE, 805),
                new CommonProduct(ModItems.BULLET_CRAFTING.get(), 200),
                new NBTAttachedProduct(Items.POTION, 100, potionTag));
    }

    private static AmmunitionRecipe addIngredients(List<Item> listItem, List<Integer> listAmount, AmmunitionRecipe recipe) {
        int i = 0;
        for (Item item: listItem) {
            int amount = i < listAmount.size() ? listAmount.get(i) : 1;
            Map<Item, Integer> ingredients = recipe.getIngredients();
            ingredients.put(item, amount);
            if (i == 16) {
                break;
            }
            i++;
        }
        return recipe;
    }
}
