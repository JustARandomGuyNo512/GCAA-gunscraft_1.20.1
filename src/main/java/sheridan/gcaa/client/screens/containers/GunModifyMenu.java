package sheridan.gcaa.client.screens.containers;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.gun.IGun;

public class GunModifyMenu extends AbstractContainerMenu {
    public Inventory playerInventory;
    public SimpleContainer displaySuitableAttachments;
    public SimpleContainer ammoSelector;

    public static class DisplaySlot extends Slot {
        public boolean active = false;

        public DisplaySlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean isActive() {
            return active;
        }
    }

    protected GunModifyMenu(@Nullable MenuType<?> menuType, int pContainerId, Inventory playerInventory) {
        super(menuType, pContainerId);
        this.playerInventory = playerInventory;
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, (i + 1) * 9 + j, 108 + j * 18, 163 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 108 + i * 18, 221));
        }

        displaySuitableAttachments = new SimpleContainer(20);

        for (int i = 0; i < 4; i ++) {
            for (int j = 0; j < 5; j++) {
                this.addSlot(new DisplaySlot(displaySuitableAttachments, i * 5 + j, 282 + j * 18, 165 + i * 18));
            }
        }

        ammoSelector = new SimpleContainer(1);
        this.addSlot(new Slot(ammoSelector, 0, 252, 142));
    }
    public GunModifyMenu(int i, Inventory inventory) {
        this(ModContainers.ATTACHMENTS.get(), i, inventory);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int id) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem().getItem() instanceof IGun;
    }
}

