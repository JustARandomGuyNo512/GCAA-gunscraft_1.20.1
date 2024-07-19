package sheridan.gcaa.client.screens.containers;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.gun.IGun;

public class AttachmentMenu extends AbstractContainerMenu {
    public Inventory playerInventory;
    public SimpleContainer suitableAttachmentsShow;

    public static class JustLookSlot extends Slot {

        public JustLookSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }
    }

    protected AttachmentMenu(@Nullable MenuType<?> menuType, int pContainerId, Inventory playerInventory) {
        super(menuType, pContainerId);
        this.playerInventory = playerInventory;
//        for (int i = 0; i < 3; i++) {
//            for(int j = 0; j < 9; ++j) {
//                this.addSlot(new Slot(playerInventory, (i + 1) * 9 + j, 84 + j * 18, 133 + i * 18));
//            }
//        }
//        for (int i = 0; i < 9; i++) {
//            this.addSlot(new Slot(playerInventory, i, 84 + i * 18, 191));
//        }
//
//        suitableAttachmentsShow = new SimpleContainer(12);
//
//        for (int i = 0; i < 3; i ++) {
//            for (int j = 0; j < 4; j++) {
//                this.addSlot(new JustLookSlot(suitableAttachmentsShow, i * 4 + j, 253 + j * 18, 140 + i * 18));
//            }
//        }

    }
    public AttachmentMenu(int i, Inventory inventory) {
        this(ModContainers.ATTACHMENTS.get(), i, inventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int id) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem().getItem() instanceof IGun;
    }
}

