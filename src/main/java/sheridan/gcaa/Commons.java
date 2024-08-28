package sheridan.gcaa;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.ModItems;

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
                .addChild(new AttachmentSlot(MAG, Set.of()))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot("rail_set", Set.of("gcaa:ak_rail_bracket")))
                .addChild(new AttachmentSlot(HANDGUARD, Set.of("gcaa:ak_improved_handguard")))
                .addChild(new AttachmentSlot(STOCK, Set.of()))
                .addChild(new AttachmentSlot("dust_cover", Set.of("gcaa:ak_improved_dust_cover")))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.G19.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of("gcaa:pistol_suppressor")))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:micro_red_dot")))
                .addChild(new AttachmentSlot(GRIP, Set.of()))
                .addChild(new AttachmentSlot(MAG, Set.of()))
        );

        AttachmentsRegister.registerAttachmentSlot(ModItems.AWP.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot(MUZZLE, Set.of()))
                .addChild(new AttachmentSlot(SCOPE, Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:scope_x10")))
                .addChild(new AttachmentSlot(MAG, "mag", Set.of()))
        );
    }
}
