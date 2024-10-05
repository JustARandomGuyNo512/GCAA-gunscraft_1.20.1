package sheridan.gcaa.common.damageTypes;

import net.minecraft.core.Holder;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.gun.IGun;

public class ProjectileDamage extends DamageSource {
    public Entity shooter;
    public IGun gun;

    public ProjectileDamage(Holder<DamageType> pType, @Nullable Entity pDirectEntity, @Nullable Entity pCausingEntity) {
        super(pType, pDirectEntity, pCausingEntity);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        String id = "death.attack." + this.type().msgId();
        String msg = Component.translatable(id).getString();
        String shooterName = this.shooter == null ? "???" : this.shooter.getDisplayName().getString();
        String vicName = pLivingEntity.getDisplayName().getString();
        String gunName = this.gun == null ? "???" : Component.translatable(gun.getGun().getDescriptionId()).getString();
        msg = msg.replace("$shooter", shooterName).replace("$gun", gunName).replace("$victim", vicName);
        return Component.literal(msg);
    }

}