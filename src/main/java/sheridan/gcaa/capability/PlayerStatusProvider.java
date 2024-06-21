package sheridan.gcaa.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStatusProvider implements ICapabilityProvider, INBTSerializable {
    public PlayerStatus playerStatus;
    public static final Capability<PlayerStatus> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public final LazyOptional<PlayerStatus> longLazyOptional = LazyOptional.of( () -> playerStatus);

    public PlayerStatusProvider() {
        this.playerStatus = new PlayerStatus();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == CAPABILITY) {
            return longLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        playerStatus.saveToNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        playerStatus.readFromNbt((CompoundTag) nbt);
    }

    public static PlayerStatus getStatus(Player player) {
        return player.getCapability(PlayerStatusProvider.CAPABILITY).orElse(PlayerStatus.EMPTY);
    }

    public static void setLastShoot(Player player, long lastShootLeft)  {
        player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
            cap.setLastShoot(lastShootLeft);
        });
    }


    public static void setLastChamberAction(Player player, long lastChamberAction)  {
        player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
            cap.setLastChamberAction(lastChamberAction);
        });
    }

    public static void setReloading(Player player, boolean reloading)  {
        player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent((cap) -> {
            cap.setReloading(reloading);
        });
    }
}
