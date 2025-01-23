package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;
import java.util.Stack;

public interface IAttachment {

    AttachResult canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);

    default void childTryAttach(ItemStack stack, IGun gun, IAttachment child, AttachmentSlot childSlot, final Stack<AttachmentSlot> path, AttachResult prevResult) {}

    void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data);

    AttachResult canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);

    default void childTryDetach(ItemStack stack, IGun gun, IAttachment child, AttachmentSlot childSlot, final Stack<AttachmentSlot> path, AttachResult prevResult) {}

    void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data);

    List<Component> getEffectsInGunModifyScreen();

    Attachment get();

    interface MessageGetter {
        String getMessage();
    }

    static AttachResult passed() {
        return new AttachResult(true, () -> "passed");
    }

    static AttachResult rejected() {
        return new AttachResult(false, () -> "rejected");
    }

    class AttachResult {
        private boolean passed;
        private MessageGetter messageGetter;

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

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public void setMessageGetter(MessageGetter messageGetter) {
            this.messageGetter = messageGetter;
        }

        @Override
        public String toString() {
            return "AttachResult{" +
                    "passed: " + passed +
                    ", message: " + messageGetter.getMessage() +
                    '}';
        }
    }
}
