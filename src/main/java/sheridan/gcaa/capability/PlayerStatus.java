package sheridan.gcaa.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerStatus {
    public static final PlayerStatus EMPTY;

    static {
        EMPTY = new PlayerStatus();
        EMPTY.lastShoot = -1L;
        EMPTY.lastChamberAction = -1L;
        EMPTY.localTimeOffset = -1L;
        EMPTY.latency = -1;
        EMPTY.reloading = false;
        EMPTY.dataChanged = false;
    }

    public PlayerStatus() {}

    private long lastShoot;
    private long lastChamberAction;
    private boolean reloading;
    private long localTimeOffset;
    private int latency;
    public boolean dataChanged;

    public void saveToNbt(CompoundTag tag) {}

    public void readFromNbt(CompoundTag tag) {
        lastShoot = 0L;
        lastChamberAction = 0L;
        localTimeOffset = 0L;
        latency = 0;
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

    public void serverSetLatency(ServerPlayer player) {
        this.latency = player.latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public void setLocalTimeOffset(long localTimeOffset) {
        if (localTimeOffset != this.localTimeOffset) {
            this.localTimeOffset = localTimeOffset;
            dataChanged = true;
        }
    }

    public long getLocalTimeOffset() {
        return localTimeOffset;
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

    public int getLatency() {
        return latency;
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
