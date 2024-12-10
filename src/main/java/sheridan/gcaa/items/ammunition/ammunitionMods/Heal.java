package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.IronGolem;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.common.server.projetile.Projectile;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.util.UUID;

public class Heal extends AmmunitionMod {
    private static final float HEAL_RATE = 0.6f;
    private static final int HEAL_TIME = 3 * 20;

    public Heal() {
        super(new ResourceLocation(GCAA.MODID, "heal"), 10, ICONS_0, new Vector4i(64, 0, 128, 128), "gcaa.ammunition_mod.heal", 0x00ff00, 100);
    }

    @Override
    public int getCostFor(IAmmunition ammunition) {
        return ammunition.getMaxModCapacity();
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {
        dataRateTag.putFloat(Ammunition.BASE_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.BASE_DAMAGE_RATE) - 100f);
        dataRateTag.putFloat(Ammunition.MIN_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.MIN_DAMAGE_RATE) - 100f);
        dataRateTag.putFloat(Ammunition.PENETRATION_RATE, dataRateTag.getFloat(Ammunition.PENETRATION_RATE) - 100f);
    }

    @Override
    public Component getSpecialDescription() {
        String str = Component.translatable("gcaa.ammunition_mod.heal_special").getString();
        String res = str.replace("$rate", FontUtils.toPercentageStr(HEAL_RATE))
                .replace("$time", HEAL_TIME / 20 + "")
                .replace("$effect", Component.translatable(MobEffects.REGENERATION.getDescriptionId()).getString());
        return Component.literal(res);
    }

    @Override
    public void onHitEntity(Projectile projectile, Entity entity, boolean isHeadSHot, IGun gun, ProjectileHandler.AmmunitionDataCache cache) {
        if (entity instanceof LivingEntity livingEntity) {
            float baseDamage = projectile.damage / cache.baseDamageRate();
            livingEntity.setHealth(livingEntity.getHealth() + baseDamage * HEAL_RATE);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, HEAL_TIME, 1), projectile.shooter);
            if (livingEntity instanceof NeutralMob neutralMob) {
                neutralMob.forgetCurrentTargetAndRefreshUniversalAnger();
                neutralMob.setRemainingPersistentAngerTime(0);
            }
        }
    }
}
