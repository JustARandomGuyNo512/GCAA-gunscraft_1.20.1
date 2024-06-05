package sheridan.gcaa.capability;

import net.minecraft.nbt.CompoundTag;

public class PlayerStatus {
    public static final PlayerStatus EMPTY;

    static {
        EMPTY = new PlayerStatus();
        EMPTY.lastShootLeft = -1L;
        EMPTY.lastShootRight = -1L;
        EMPTY.lastChamberAction = -1L;
        EMPTY.reloading = false;
        EMPTY.dataChanged = false;
    }

    public PlayerStatus() {}

    private long lastShootRight;
    private long lastShootLeft;
    private long lastChamberAction;
    private boolean reloading;
    public boolean dataChanged;

    public void saveToNbt(CompoundTag tag) {}

    public void readFromNbt(CompoundTag tag) {
        lastShootRight = 0L;
        lastShootLeft = 0L;
        lastChamberAction = 0L;
        reloading = false;
        dataChanged = true;
    }

    public long getLastShootRight() {
        return lastShootRight;
    }

    public void setLastShootRight(long lastShootRight) {
        if (lastShootRight != this.lastShootRight) {
            this.lastShootRight = lastShootRight;
            dataChanged = true;
        }
    }

    public long getLastShootLeft() {
        return lastShootLeft;
    }

    public void setLastShootLeft(long lastShootLeft) {
        if (lastShootLeft != this.lastShootLeft) {
            this.lastShootLeft = lastShootLeft;
            dataChanged = true;
        }
    }

    public long getLastChamberAction() {
        return lastChamberAction;
    }

    public void setLastChamberAction(long lastChamberAction) {
       if (lastChamberAction != this.lastChamberAction) {
           this.lastChamberAction = lastChamberAction;
           dataChanged = true;
       }
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        if (reloading != this.reloading) {
            this.reloading = reloading;
            dataChanged = true;
        }
    }

}
