package sheridan.gcaa.items.gun.guns;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Annihilator extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_45_ACP,7f, 4.5f, 4f, 6f, 0.55f)
                    .setAmmunition(ModItems.AMMO_45ACP.get());

    public Annihilator() {
        super(new GunProperties(3.8f, 0.8f, 3f, 0.8f, 0.15f,
                3.4f, GunProperties.toRPM(800), getTicks(2.55f), getTicks(3.3f), 20,
                1.6f, 0.5f, 0.15f, 0.12f, 12, List.of(Auto.AUTO),
                ModSounds.ANNIHILATOR_FIRE, null, caliber));
    }

    @Override
    protected void gunDetailInfo(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.gunDetailInfo(stack, levelIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip.gun_info.annihilator"));
    }
}