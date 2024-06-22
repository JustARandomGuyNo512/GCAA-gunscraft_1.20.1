package sheridan.gcaa.items.guns;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.BaseItem;
import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.GunFirePacket;

import java.util.List;

public class Gun extends BaseItem implements IGun {
    private final GunProperties gunProperties;

    public Gun(GunProperties gunProperties) {
        super(new Properties().stacksTo(1));
        this.gunProperties = gunProperties;
    }

    @Override
    public GunProperties getGunProperties() {
        return gunProperties;
    }

    @Override
    public Gun getGun() {
        return this;
    }

    @Override
    public int getAmmoLeft(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean tryShoot(ItemStack stack, Player player) {
        return getFireMode(stack).canFire(player, stack, this);
    }

    @Override
    public void preShoot(ItemStack stack, Player player) {
        IGunFireMode fireMode = getFireMode(stack);
        if (player.level().isClientSide) {
            fireMode.preShoot(player, stack, this);
            PacketHandler.simpleChannel.sendToServer(new GunFirePacket());
        }
    }

    @Override
    public void shoot(ItemStack stack, Player player) {

    }

    @Override
    public IGunFireMode getFireMode(ItemStack stack) {
        return null;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    protected CompoundTag checkAndGet(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            this.onCraftedBy(stack, null, null);
            nbt = stack.getTag();
        }
        return nbt;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        CompoundTag nbt = pStack.getTag();
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        if (!nbt.contains("inner_version")) {
            nbt.putInt("inner_version", getVersion());
        }

        pStack.setTag(nbt);
    }

    protected int getVersion() {
        return GCAA.INNER_VERSION;
    }
}

