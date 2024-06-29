package sheridan.gcaa.entities.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Bullet extends Entity implements IBullet{
    public static final float CHUNK_TO_METER = 16f;
    public float baseDamage;
    public float miniDamage;
    public float effectiveRange;
    public LivingEntity shooter;
    public float speed;
    public float spread;

    public Bullet(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {}


    @Override
    public void shoot(LivingEntity shooter, float speed, float spread, float baseDamage, float miniDamage, float effectiveRange) {
        this.shooter = shooter;
        this.baseDamage = baseDamage;
        this.miniDamage = miniDamage;
        this.effectiveRange = effectiveRange;
        this.speed = speed * CHUNK_TO_METER;
        this.spread = spread;
    }

    @Override
    public Bullet get() {
        return this;
    }
}
