package sheridan.gcaa;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentRegister;
import sheridan.gcaa.items.ModItems;

import java.util.Set;

public class Commons {
    public static long SERVER_START_TIME = System.currentTimeMillis();

    public static void onCommonSetUp(final FMLCommonSetupEvent event) {
        AttachmentRegister.registerAttachmentSlot(ModItems.AKM.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot("muzzle", Set.of("")))
                .addChild(new AttachmentSlot("mag", Set.of("")))
                .addChild(new AttachmentSlot("grip", Set.of("")))
                .addChild(new AttachmentSlot("rail_set", Set.of("")))
                .addChild(new AttachmentSlot("handguard", Set.of("")))
                .addChild(new AttachmentSlot("stock", Set.of("")))
        );

        AttachmentRegister.registerAttachmentSlot(ModItems.G19.get(), AttachmentSlot.root()
                .addChild(new AttachmentSlot("muzzle", Set.of("")))
                .addChild(new AttachmentSlot("scope", Set.of("")))
                .addChild(new AttachmentSlot("grip", Set.of("")))
                .addChild(new AttachmentSlot("mag", Set.of("")))
        );
    }
}
