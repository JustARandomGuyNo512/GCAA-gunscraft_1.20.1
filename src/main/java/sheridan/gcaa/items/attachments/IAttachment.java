package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.antlr.v4.misc.Utils;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

import java.util.function.Function;

public interface IAttachment {
    AttachResult PASSED = new AttachResult(true, () -> "passed");
    AttachResult REJECTED = new AttachResult(false, () -> "rejected");

    AttachResult canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);

    void onAttach(ItemStack stack, IGun gun, CompoundTag data);

    AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);

    void onDetach(ItemStack stack, IGun gun, CompoundTag data);

    Attachment get();

    interface MessageGetter {
        String getMessage();
    }

    class AttachResult {
        private final boolean passed;
        private final MessageGetter messageGetter;

        public AttachResult(boolean passed) {
            this.passed = passed;
            this.messageGetter = () -> "no details...";
        }

        public AttachResult(boolean passed, MessageGetter messageGetter) {
            this.passed = passed;
            this.messageGetter = messageGetter;
        }

        public boolean isPassed() {
            return passed;
        }

        public String getMessage() {
            return messageGetter.getMessage();
        }
    }
}
