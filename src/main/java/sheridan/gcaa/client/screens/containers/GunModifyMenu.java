package sheridan.gcaa.client.screens.containers;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
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
    private ContainerLevelAccess access;

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

    public GunModifyMenu(@Nullable MenuType<?> menuType, int pContainerId, Inventory playerInventory, Player player) {
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
        this.addSlot(new AmmunitionModifyMenu.AmmunitionSlot(ammoSelector, 0, 252, 142));
        if (player == null) {
            this.access = ContainerLevelAccess.NULL;
        } else {
            this.access = ContainerLevelAccess.create(player.level(), player.getOnPos());
        }
    }
    public GunModifyMenu(int i, Inventory inventory) {
        this(ModContainers.ATTACHMENTS.get(), i, inventory, null);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int id) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem().getItem() instanceof IGun;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39371_, p_39372_) -> {
            this.clearContainer(pPlayer, this.ammoSelector);
        });
    }
}

