package sheridan.gcaa.capability;

import net.minecraft.nbt.CompoundTag;

public class PlayerStatus {
    public static final PlayerStatus EMPTY;

    static {
        EMPTY = new PlayerStatus();
        EMPTY.lastShoot = -1L;
        EMPTY.lastChamberAction = -1L;
        EMPTY.reloading = false;
        EMPTY.dataChanged = false;
    }

    public PlayerStatus() {}

    private long lastShoot;
    private long lastChamberAction;
    private boolean reloading;
    public boolean dataChanged;

    public void saveToNbt(CompoundTag tag) {}

    public void readFromNbt(CompoundTag tag) {
        lastShoot = 0L;
        lastChamberAction = 0L;
        reloading = false;
        dataChanged = true;
    }

    public long getLastShoot() {
        return lastShoot;
    }

    public void setLastShoot(long lastShoot) {
        if (lastShoot != this.lastShoot) {
            this.lastShoot = lastShoot;
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
