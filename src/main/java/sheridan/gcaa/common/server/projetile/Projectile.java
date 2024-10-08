package sheridan.gcaa.common.server.projetile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.common.damageTypes.DamageTypes;
import sheridan.gcaa.common.damageTypes.ProjectileDamage;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.ClientPlayParticlePacket;
import sheridan.gcaa.common.config.CommonConfig;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class Projectile {
    private static final int DISABLE_LATENCY = -1;
    private static final Predicate<Entity> GENERIC_TARGETS = (input) -> input instanceof LivingEntity && !input.isSpectator() && input.isAlive();
    private static final float CHUNK_TO_METER = 1.6f;
    private static final float BASE_SPREAD_INDEX = 0.01F;
    private static final Random RANDOM = new Random();
    public static final float dropRate = 0.1f;
    public Vec3 initialPos;
    public Vec3 position;
    public Vec3 velocity;
    public IGun gun;
    public float damage;
    public float effectiveRange;
    public LivingEntity shooter;
    private boolean living;
    private float dis = 0;
    private long birthTime;
    private int latency = DISABLE_LATENCY;

    Projectile() {}

    public void tick(float timeDis) {
        if (living) {
            if (this.shooter != null) {
                if (System.currentTimeMillis() - birthTime > CommonConfig.maxBulletLivingTime.get()) {
                    living = false;
                    return;
                }
                Level level = this.shooter.level();
                Vec3 nextPos = position.add(velocity.scale(timeDis / 0.05f));
                BlockHitResult hitResult = level.clip(new ClipContext(position, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
                if (hitResult.getType() != HitResult.Type.MISS) {
                    Vec3 hitPos = hitResult.getLocation();
                    EntityHitResult entityHitResult = findEntity(level, position, hitPos);
                    if (entityHitResult != null && entityHitResult.getEntity() != this.shooter) {
                        onHitEntity(entityHitResult.getEntity(), level, entityHitResult.getLocation());
                    } else {
                        PacketHandler.simpleChannel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                                hitResult.getLocation().x,
                                hitResult.getLocation().y,
                                hitResult.getLocation().z,
                                48, level.dimension()
                        )), new ClientPlayParticlePacket(hitResult.getBlockPos(), hitResult.getLocation(), hitResult.getDirection(), 10));
                    }
                    living = false;
                } else {
                    EntityHitResult entityHitResult = findEntity(level, position, nextPos);
                    if (entityHitResult != null && entityHitResult.getEntity() != this.shooter) {
                        onHitEntity(entityHitResult.getEntity(), level, entityHitResult.getLocation());
                    }
                }
                dis += (float) position.distanceToSqr(nextPos);
                velocity.add(0, - dropRate, 0);
                if (dis > effectiveRange) {
                    living = false;
                    return;
                }
                position = nextPos;
                if (!level.isLoaded(new BlockPos((int) position.x, (int) position.y, (int) position.z))) {
                    living = false;
                }
            } else {
                living = false;
            }
        }
    }

    protected EntityHitResult findEntity(Level level, Vec3 pStartVec, Vec3 pEndVec) {
        AABB box = this.makeBoundingBox().expandTowards(velocity).inflate(1.0D);
        List<Entity> entities = level.getEntities((Entity) null, box, GENERIC_TARGETS);
        double minDis = Double.MAX_VALUE;
        Entity target = null;
        for(Entity entity : entities) {
            if (entity == shooter) {
                continue;
            }
            AABB aabb = entity instanceof Player player ? PlayerPosCacheHandler.getPlayerAABB(player, latency, 0.3f) :
                    entity.getBoundingBox().inflate(0.3f);
            if (aabb == null) {
                continue;
            }
            Optional<Vec3> optional = aabb.clip(pStartVec, pEndVec);
            if (optional.isPresent()) {
                double dis = pStartVec.distanceToSqr(optional.get());
                if (dis < minDis) {
                    target = entity;
                    minDis = dis;
                }
            }
        }
        return target == null ? null : new EntityHitResult(target);
    }

    private AABB makeBoundingBox() {
        float f = 0.25f * 0.5f;
        float f1 = 0.25f;
        return new AABB(position.x - f, position.y, position.z - f,
                position.x + f, position.y + f1, position.z + f);
    }

    public boolean living() {
        return living;
    }

    public void reset() {
        shooter = null;
        living = false;
        dis = 0;
        latency = DISABLE_LATENCY;
    }

    private void onHitEntity(Entity entity, Level level, Vec3 hitPos) {
        entity.invulnerableTime = 0;
        ProjectileDamage damageSource = (ProjectileDamage) DamageTypes.getDamageSource(level, DamageTypes.GENERIC_PROJECTILE, null, this.shooter);
        damageSource.shooter = this.shooter;
        damageSource.gun = gun;
        float dis = (float) initialPos.distanceToSqr(hitPos);
        float progress = Mth.clamp(dis / effectiveRange, 0, 1);
        entity.hurt(damageSource, damage * (1 - progress * progress) * CommonConfig.globalBulletDamageModify.get().floatValue());
        living = false;
    }

    public void shoot(LivingEntity shooter, float speed, float damage, float spread, float effectiveRange, IGun gun) {
        effectiveRange *= 16;
        this.gun = gun;
        this.shooter = shooter;
        this.damage = damage;
        this.living = true;
        this.effectiveRange = effectiveRange * effectiveRange;
        this.position = new Vec3(this.shooter.getX(), this.shooter.getY()  + shooter.getEyeHeight(shooter.getPose()), this.shooter.getZ());
        this.initialPos = new Vec3(position.x, position.y, position.z);
        speed *= CHUNK_TO_METER;
        spread *= BASE_SPREAD_INDEX * CommonConfig.globalBulletSpeedModify.get();
        Vec3 angle = this.shooter.getLookAngle();
        velocity = angle.normalize().add(
                RANDOM.nextGaussian() * spread,
                RANDOM.nextGaussian() * spread,
                RANDOM.nextGaussian() * spread).scale(speed);
        living = true;
        birthTime = System.currentTimeMillis();
        if (shooter instanceof ServerPlayer player && CommonConfig.enableLagCompensation.get()) {
            latency = player.latency;
            int maxAccept = CommonConfig.maxLagCompensationMilliseconds.get();
            latency = latency > maxAccept ? DISABLE_LATENCY : latency;
        }
    }

}
