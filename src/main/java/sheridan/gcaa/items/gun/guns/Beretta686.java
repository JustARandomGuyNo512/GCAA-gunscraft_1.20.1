package sheridan.gcaa.items.gun.guns;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.calibers.CaliberGauge12;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Beretta686 extends Gun {
    private static final CaliberGauge12 caliber =
            (CaliberGauge12) new CaliberGauge12(Caliber.CALIBER_12_GAUGE, 4f, 2.5f, 3.8f, 6.6f, 8)
                    .modifySpread(1.8f)
                    .setAmmunition(ModItems.AMMO_12GAUGE.get())
                    .setPenetration(0.35f);

    public Beretta686() {
        super(new GunProperties(3.9f, 1.6f, 3.8f, 1.2f, 0.25f,
                4.7f, GunProperties.toRPM(1200), getTicks(2.1f), getTicks(2.1f), 2,
                4.5f, 2f, 0.22f, 0.2f, 14,
                List.of(Semi.SEMI, new Volley()), ModSounds.BERETTA_686_FIRE, null, caliber));
    }

    @Override
    public boolean shootCreateBulletShell() {
        return false;
    }

    @Override
    public void clientShoot(ItemStack stack, Player player, IGunFireMode fireMode) {
        if (shouldDoVolley(stack)) {
            float recoilPitch = getGunProperties().recoilPitch;
            getGunProperties().recoilPitch *= 2f;
            super.clientShoot(stack, player, fireMode);
            getGunProperties().recoilPitch = recoilPitch;
        } else {
            super.clientShoot(stack, player, fireMode);
        }
    }

    @Override
    public void shoot(ItemStack stack, Player player, IGunFireMode fireMode, float spread) {
        if (shouldDoVolley(stack)) {
            CaliberGauge12 caliber = (CaliberGauge12) getGunProperties().caliber;
            caliber.projectileNum *= 2;
            float baseSpread = caliber.baseSpread;
            caliber.baseSpread *= 1.5f;
            super.shoot(stack, player, fireMode, spread);
            caliber.projectileNum /= 2;
            caliber.baseSpread = baseSpread;
            setAmmoLeft(stack, 0);
        } else {
            super.shoot(stack, player, fireMode, spread);
        }
    }

    private boolean shouldDoVolley(ItemStack stack) {
        return getFireMode(stack) instanceof Volley && getAmmoLeft(stack) == 2;
    }

    @Override
    protected void handleFireSoundClient(ItemStack stack, Player player) {
        if (shouldDoVolley(stack)) {
            getGunProperties().fireSound = ModSounds.BERETTA_686_FIRE_VOLLEY;
        } else {
            getGunProperties().fireSound = ModSounds.BERETTA_686_FIRE;
        }
        super.handleFireSoundClient(stack, player);
    }

    @Override
    protected void handleFireSoundServer(ItemStack stack, Player player) {
        float soundVol = getGunProperties().fireSoundVol;
        if (shouldDoVolley(stack)) {
            getGunProperties().fireSound = ModSounds.BERETTA_686_FIRE_VOLLEY;
            getGunProperties().fireSoundVol *= 1.5f;
        } else {
            getGunProperties().fireSound = ModSounds.BERETTA_686_FIRE;
        }
        try {
            super.handleFireSoundServer(stack, player);
        } finally {
            getGunProperties().fireSoundVol = soundVol;
        }
    }

    public static class Volley extends Semi {
        @Override
        public String getName() {
            return "volley";
        }

        @Override
        public void shoot(Player player, ItemStack itemStack, IGun gun, float spread) {
            super.shoot(player, itemStack, gun, spread);
        }

        @Override
        public void clientShoot(Player player, ItemStack itemStack, IGun gun) {
            super.clientShoot(player, itemStack, gun);
        }

        @Override
        public Component getTooltipName() {
            return Component.translatable("tooltip.fire_mode.volley");
        }

    }

    @Override
    public int getCrosshairType() {
        return 1;
    }

    @Override
    public GunType getGunType() {
        return GunType.SHOTGUN;
    }
}
