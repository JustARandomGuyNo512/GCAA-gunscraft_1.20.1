package sheridan.gcaa.common.damageTypes;

import net.minecraft.core.Holder;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.gun.IGun;

public class ProjectileDamage  extends DamageSource {

    public Entity shooter;
    public IGun gun;


    public ProjectileDamage(Holder<DamageType> p_270818_, @Nullable Entity p_270162_, @Nullable Entity p_270115_) {
        super(p_270818_, p_270162_, p_270115_);
    }

}