package sheridan.gcaa;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.proxies.AkmAttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.proxies.Mk47AttachmentSlotProxy;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.attachments.replaceableParts.Mk47Handguard;
import sheridan.gcaa.items.attachments.replaceableParts.RecoilControlPart;
import sheridan.gcaa.items.attachments.replaceableParts.RecoilLowerPart;
import sheridan.gcaa.items.attachments.replaceableParts.WeightPart;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ak_extend_mag")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot("rail_set", Set.of("gcaa:ak_rail_bracket")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:ak_improved_handguard")).setReplaceableGunPart(new WeightPart(1)))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")).setReplaceableGunPart(new RecoilControlPart(1, 0.1f, 0.1f)))
                .addChild(new AttachmentSlot("dust_cover", Set.of("gcaa:ak_improved_dust_cover", "gcaa:ak_tactical_dust_cover"))),
                 AkmAttachmentSlotProxy::new
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M4A1.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ar_suppressor", "gcaa:ar_compensator")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ar_extend_mag")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:ar_railed_handguard")).setReplaceableGunPart(new RecoilControlPart(0.8f, 0.05f, 0.05f)))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ctr_stock")).setReplaceableGunPart(new WeightPart(1)))
                .addChild(new AttachmentSlot("gas_block", Set.of("gcaa:ar_gas_block")))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")).setReplaceableGunPart(new WeightPart(0.5f)))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.G19.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:pistol_suppressor", "gcaa:osprey_suppressor")))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:micro_red_dot")))
                .addChild(new AttachmentSlot(GRIP, Set.of("gcaa:micro_laser_sight")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:glock_extend_mag")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.PYTHON_357.get(), AttachmentSlot.EMPTY);

        AttachmentsRegister.registerAttachmentSlot(ModItems.AWP.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:sniper_suppressor")).setReplaceableGunPart(new RecoilLowerPart(0, 0.15f, 0.15f)))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:scope_x10", "gcaa:acog")))
                .addChild(new AttachmentSlot(MAG, "mag", Set.of("gcaa:sniper_extend_mag")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M870.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:shotgun_suppressor")))
                .addChild(new AttachmentSlot(STOCK, Set.of()))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:shotgun_extend_bay")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of()))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M249.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ar_suppressor", "gcaa:ar_compensator")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")).setReplaceableGunPart(new RecoilControlPart(1.2f, 0.1f, 0.08f)))
                .addChild(new AttachmentSlot(MAG, Set.of()).setReplaceableGunPart(new WeightPart(3f)))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of()))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.VECTOR_45.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:osprey_suppressor"," gcaa:pistol_suppressor", "gcaa:smg_compensator")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")).setReplaceableGunPart(new RecoilControlPart(0.7f, 0.05f, 0.06f)))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:vector_45_extend_mag")))
                .addChild(new AttachmentSlot(GRIP, Set.of("gcaa:vertical_grip", "gcaa:rail_panel_short")))
                .addChild(new AttachmentSlot("left", "s_left", Set.of("gcaa:laser_sight")))
                .addChild(new AttachmentSlot("right", "s_right", Set.of("gcaa:laser_sight", "gcaa:rail_panel_short")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.XM1014.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:shotgun_suppressor")))
        );

        Set<String> mk47HandguardSlot = new HashSet<>(List.of(
                "gcaa:laser_sight",
                "gcaa:horizontal_laser_sight",
                "gcaa:rail_panel",
                "gcaa:rail_panel_short"));
        AttachmentsRegister.registerAttachmentSlot(ModItems.MK47.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of()).setReplaceableGunPart(new Mk47Handguard()))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ak_extend_mag")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ctr_stock")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ak_compensator", "gcaa:ak_suppressor")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot("handguard_scope", Set.of(
                        "gcaa:red_dot",
                        "gcaa:holographic",
                        "gcaa:acog",
                        "gcaa:horizontal_laser_sight",
                        "gcaa:rail_panel",
                        "gcaa:rail_panel_short")).upper())
                .addChild(new AttachmentSlot("handguard_left", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("handguard_left_rear", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("handguard_right", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("handguard_right_rear", mk47HandguardSlot).lower())
                .addChild(new AttachmentSlot("handguard_lower", Set.of(
                        "gcaa:laser_sight",
                        "gcaa:horizontal_laser_sight",
                        "gcaa:rail_panel_short")).lower())
                .addChild(new AttachmentSlot("handguard_grip", Set.of(
                        "gcaa:laser_sight",
                        "gcaa:horizontal_laser_sight",
                        "gcaa:rail_panel",
                        "gcaa:rail_panel_short",
                        "gcaa:gp_25")).lower()),
                Mk47AttachmentSlotProxy::new
        );
    }
}
