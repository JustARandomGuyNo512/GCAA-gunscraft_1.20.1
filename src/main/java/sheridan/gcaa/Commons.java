package sheridan.gcaa;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.proxies.AkmAttachmentSlotProxy;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.attachments.replaceableParts.TestPart;

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
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:ak_improved_handguard")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")))
                .addChild(new AttachmentSlot("dust_cover", Set.of("gcaa:ak_improved_dust_cover", "gcaa:ak_tactical_dust_cover"))),
                 AkmAttachmentSlotProxy::new
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.M4A1.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:ar_suppressor", "gcaa:ar_compensator")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:ar_extend_mag")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:ar_railed_handguard")).setReplaceableGunPart(new TestPart()))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ctr_stock")))
                .addChild(new AttachmentSlot("gas_block", Set.of("gcaa:ar_gas_block")))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.G19.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:pistol_suppressor", "gcaa:osprey_suppressor")))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:micro_red_dot")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:glock_extend_mag")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.PYTHON_357.get(), AttachmentSlot.EMPTY);

        AttachmentsRegister.registerAttachmentSlot(ModItems.AWP.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:sniper_suppressor")))
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
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")))
                .addChild(new AttachmentSlot(MAG, Set.of()))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of()))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.VECTOR_45.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:osprey_suppressor"," gcaa:pistol_suppressor", "gcaa:smg_compensator")))
                .addChild(new AttachmentSlot(STOCK, Set.of("gcaa:ar_stock_tube")))
                .addChild(new AttachmentSlot(MAG, Set.of("gcaa:vector_45_extend_mag")))
                .addChild(new AttachmentSlot(GRIP, Set.of("gcaa:vertical_grip")))
                .addChild(new AttachmentSlot("left", "s_left", Set.of()))
                .addChild(new AttachmentSlot("right", "s_right", Set.of()))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.XM1014.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")))
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:shotgun_suppressor")))
        );
    }
}
