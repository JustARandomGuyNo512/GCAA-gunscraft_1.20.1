package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mag extends Attachment{
    protected Map<IGun, Integer> specialCapacity = new HashMap<>();
    int capacity;
    private int minCapacity = Integer.MAX_VALUE;
    private int maxCapacity = 0;

    public Mag(int capacity, float weight) {
        super(weight);
        this.capacity = Math.max(capacity, 0);
        this.minCapacity = Math.min(capacity, minCapacity);
        this.maxCapacity = Math.max(capacity, maxCapacity);
    }

    protected void addSpecialCapacityFor(IGun gun, int capacity) {
        specialCapacity.put(gun, capacity);
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        super.onAttach(player, stack, gun, data);
        if (specialCapacity.containsKey(gun)) {
            properties.setMagSize(data, specialCapacity.get(gun));
        } else {
            properties.setMagSize(data, capacity);
        }
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        gun.getGunProperties().resetMagSize(data);
        super.onDetach(player, stack, gun, data);
        AmmunitionHandler.clearGun(player, gun, stack);
    }

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> effectsInGunModifyScreen = super.getEffectsInGunModifyScreen();
        String replace;
        if (minCapacity == maxCapacity) {
            replace = Component.translatable("tooltip.gcaa.exp_mag").getString()
                    .replace("$min ~ $max", "" + minCapacity);
        } else {
            replace = Component.translatable("tooltip.gcaa.exp_mag").getString()
                    .replace("$min", "" + minCapacity)
                    .replace("$max", "" + maxCapacity);
        }
        effectsInGunModifyScreen.add(Component.literal(replace));
        return effectsInGunModifyScreen;
    }
}
