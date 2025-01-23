package sheridan.gcaa.common.server.projetile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.common.HeadBox;
import sheridan.gcaa.common.damageTypes.DamageTypes;
import sheridan.gcaa.common.damageTypes.ProjectileDamage;
import sheridan.gcaa.entities.projectiles.Grenade;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.ClientHitBlockPacket;
import sheridan.gcaa.common.config.CommonConfig;
import sheridan.gcaa.network.packets.s2c.HeadShotFeedBackPacket;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class Projectile {
    private static int ACTIVE_NUM = 0;
    private static int TOTAL_NUM = 0;
    private static final int DISABLE_LATENCY = -1;
    private static final Predicate<Entity> GENERIC_TARGETS = (input) -> input instanceof Grenade ||
            (input instanceof LivingEntity && !input.isSpectator() && input.isAlive()) || input instanceof EndCrystal;
    public static final float CHUNK_TO_METER = 1.6f;
    public static final float BASE_SPREAD_INDEX = 0.0125F;
    public static final Random RANDOM = new Random();
    public static final float dropRate = 0.1f;
    public Vec3 initialPos;
    public Vec3 position;
    public Vec3 velocity;
    public IGun gun;
    public float damage;
    public float minDamage;
    public float effectiveRange;
    public LivingEntity shooter;
    private boolean living;
    private float dis = 0;
    private long birthTime;
    private float penetration;
    private int latency = DISABLE_LATENCY;
    private String modsUUID;
    private List<IAmmunitionMod> mods;
    private ProjectileHandler.AmmunitionDataCache cache;

    Projectile() {
        TOTAL_NUM ++;
    }

    public static int getActiveNum() {
        return ACTIVE_NUM;
    }

    public static int getTotalNum() {
        return TOTAL_NUM;
    }

    public void tick(float timeDis) {
        if (living) {
            if (this.shooter != null) {
                if (System.currentTimeMillis() - birthTime > CommonConfig.maxBulletLivingTime.get()) {
                    living = false;
                    return;
                }
                Level level = this.shooter.level();
                Vec3 nextPos = position.add(velocity.scale(timeDis / 0.05f));
                boolean reachedBoundary = !level.isLoaded(new BlockPos((int) position.x, (int) position.y, (int) position.z));
                BlockHitResult hitResult = level.clip(new ClipContext(position, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
                if (hitResult.getType() != HitResult.Type.MISS) {
                    Vec3 hitPos = hitResult.getLocation();
                    BlockState blockState = this.shooter.level().getBlockState(hitResult.getBlockPos());
                    boolean through = CommonConfig.bulletCrossLeafBlock.get() && blockState.getBlock() instanceof LeavesBlock;
                    Vec3 endPos = through ? nextPos : hitPos;
                    ProjectileEntityHitResult entityHitResult = findEntity(level, position, endPos);
                    if (entityHitResult != null && entityHitResult.getEntity() != this.shooter) {
                        onHitEntity(entityHitResult.getEntity(), level, entityHitResult.getLocation(), position, endPos, entityHitResult.boxHit);
                        living = false;
                        return;
                    } else {
                        onHitBlock(hitResult);
                    }
                    if (!through) {
                        living = false;
                        return;
                    }
                } else {
                    ProjectileEntityHitResult entityHitResult = findEntity(level, position, nextPos);
                    if (entityHitResult != null && entityHitResult.getEntity() != this.shooter) {
                        onHitEntity(entityHitResult.getEntity(), level, entityHitResult.getLocation(), position, nextPos, entityHitResult.boxHit);
                    }
                }
                velocity.add(0, - dropRate, 0);
                position = nextPos;
                dis = (float) position.distanceToSqr(initialPos);
                if (dis > effectiveRange) {
                    living = false;
                    return;
                }
                if (reachedBoundary) {
                    living = false;
                }
            } else {
                living = false;
            }
        }
    }

    protected ProjectileEntityHitResult findEntity(Level level, Vec3 pStartVec, Vec3 pEndVec) {
        AABB box = this.makeBoundingBox().expandTowards(velocity).inflate(1.0D);
        List<Entity> entities = level.getEntities((Entity) null, box, GENERIC_TARGETS);
        double minDis = Double.MAX_VALUE;
        Entity target = null;
        AABB boxHit = null;
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
                    if (entity instanceof Player) {
                        boxHit = aabb;
                    }
                }
            } else if (dis == 0) {
                Vec3 hitPos = intersect(pStartVec, pEndVec, aabb);
                if (hitPos != null && pStartVec.distanceToSqr(pEndVec) >= pStartVec.distanceToSqr(hitPos)) {
                    double d1 = pStartVec.distanceToSqr(hitPos);
                    if (d1 < minDis) {
                        target = entity;
                        minDis = d1;
                    }
                }
            }
        }
        return target == null ? null : new ProjectileEntityHitResult(target, boxHit);
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
        ACTIVE_NUM --;
        modsUUID = null;
        mods = null;
        cache = null;
    }

    private void onHitBlock(BlockHitResult blockHitResult) {
        BlockState blockState = shooter.level().getBlockState(blockHitResult.getBlockPos());
        Block block = blockState.getBlock();
        if (block instanceof BellBlock bell && this.shooter instanceof Player) {
            bell.attemptToRing(null, this.shooter.level(), blockHitResult.getBlockPos(), blockHitResult.getDirection());
        }
        if (CommonConfig.bulletBreakGlass.get() && (
                block instanceof AbstractGlassBlock ||
                block instanceof StainedGlassPaneBlock ||
                "minecraft:glass_pane".equals(BuiltInRegistries.BLOCK.getKey(block).toString()))) {
            this.shooter.level().destroyBlock(blockHitResult.getBlockPos(), false);
        }
        if (this.mods != null) {
            for (IAmmunitionMod mod : mods) {
                try {
                    mod.onHitBlockServer(this, blockHitResult, blockState);
                } catch (Exception ignored) {}
            }
        }
        PacketHandler.simpleChannel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                blockHitResult.getLocation().x,
                blockHitResult.getLocation().y,
                blockHitResult.getLocation().z,
                48, shooter.level().dimension()
        )), new ClientHitBlockPacket(
                blockHitResult.getBlockPos(),
                blockHitResult.getLocation(),
                blockHitResult.getDirection(),
                cache.getClientModsIndexList()
        ));
    }

    private void onHitEntity(Entity entity, Level level, Vec3 hitPos, Vec3 from, Vec3 to, AABB boxHit) {
        if (entity instanceof Grenade grenade && grenade.shooter != shooter) {
            grenade.explode();
            living = false;
            return;
        }
        entity.invulnerableTime = 0;
        ProjectileDamage damageSource =
                (ProjectileDamage) DamageTypes.getDamageSource(level, DamageTypes.GENERIC_PROJECTILE, this.shooter, this.shooter);
        damageSource.shooter = this.shooter;
        damageSource.gun = gun;
        damageSource.penetration = this.penetration;
        float dis = (float) initialPos.distanceToSqr(hitPos);
        float progress = Mth.clamp(dis / effectiveRange, 0, 1);
        boolean isHeadShot = false;
        if (CommonConfig.enableHeadShot.get()) {
            HeadBox.HeadShotResult headShotResult = HeadBox.getHeadShotResult(entity, boxHit, from, to);
            if (headShotResult.getIsHeadShot()) {
                damage *= headShotResult.getDamageModify();
                isHeadShot = true;
            }
        }
        if (this.shooter instanceof Player && !shooter.level().isClientSide) {
            PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) this.shooter), new HeadShotFeedBackPacket(isHeadShot));
        }
        entity.hurt(damageSource,
                Mth.lerp((1 - progress * progress), minDamage, damage) * CommonConfig.globalBulletDamageModify.get().floatValue());
        if (this.mods != null) {
            for (IAmmunitionMod mod : mods) {
                try {
                    mod.onHitEntity(this, entity, isHeadShot, gun, cache);
                } catch (Exception ignored) {}
            }
        }
        living = false;
        if (Math.random() <= 0.025) {
            if (entity instanceof Chicken) {
                level.playSound(entity, new BlockPos((int) hitPos.x, (int) hitPos.y, (int) hitPos.z), ModSounds.NI_GAN_MA.get(), SoundSource.PLAYERS, 1, 1);
            }
        }
    }

    public void shoot(LivingEntity shooter, float penetration, float speed, float damage, float minDamage, float spread, float effectiveRange, IGun gun, String modsUUID) {
        shoot(shooter, shooter.getLookAngle(), penetration, speed, damage, minDamage, spread, effectiveRange, gun, modsUUID);
    }

    public void shoot(LivingEntity shooter, Vec3 angle, float penetration, float speed, float damage, float minDamage, float spread, float effectiveRange, IGun gun, String modsUUID) {
        effectiveRange *= 16;
        this.gun = gun;
        this.shooter = shooter;
        this.damage = (float) (damage * (0.95f + Math.random() * 0.1f));
        this.minDamage = (float) (minDamage * (0.9f + Math.random() * 0.2f));
        this.living = true;
        this.penetration = penetration;
        this.effectiveRange = effectiveRange * effectiveRange;
        this.position = new Vec3(this.shooter.getX(), this.shooter.getY()  + shooter.getEyeHeight(shooter.getPose()), this.shooter.getZ());
        this.initialPos = new Vec3(position.x, position.y, position.z);
        speed *= CHUNK_TO_METER * CommonConfig.globalBulletSpeedModify.get();
        spread *= BASE_SPREAD_INDEX;
        velocity = angle.normalize().add(
                RANDOM.nextGaussian() * spread,
                RANDOM.nextGaussian() * spread,
                RANDOM.nextGaussian() * spread).scale(speed);
        birthTime = System.currentTimeMillis();
        if (shooter instanceof ServerPlayer player && CommonConfig.enableLagCompensation.get()) {
            latency = player.latency;
            int maxAccept = CommonConfig.maxLagCompensationMilliseconds.get();
            latency = latency > maxAccept ? DISABLE_LATENCY : latency;
        }
        this.modsUUID = modsUUID;
        ProjectileHandler.AmmunitionDataCache cache = ProjectileHandler.getAmmunitionDataFromCache(this.modsUUID);
        if (cache != null && cache != ProjectileHandler.EMPTY_MODS) {
            this.damage *= cache.baseDamageRate();
            this.minDamage *= cache.minDamageRate();
            this.velocity = velocity.scale(cache.speedRate());
            this.effectiveRange *= cache.effectiveRangeRate();
            this.penetration *= cache.penetrationRate();
            if (!cache.mods().isEmpty()) {
                mods = cache.mods();
                for (IAmmunitionMod mod : mods) {
                    mod.onShootInServer(this, gun);
                }
            }
            this.cache = cache;
        } else {
            this.cache = ProjectileHandler.EMPTY_MODS;
        }
        ACTIVE_NUM ++;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        TOTAL_NUM --;
        modsUUID = null;
    }

    public static Vec3 intersect(Vec3 startPos, Vec3 endPos, AABB box) {
        double dx = endPos.x - startPos.x;
        double dy = endPos.y - startPos.y;
        double dz = endPos.z - startPos.z;

        double txMin = (box.minX - startPos.x) / dx;
        double txMax = (box.maxX - startPos.x) / dx;

        double tyMin = (box.minY - startPos.y) / dy;
        double tyMax = (box.maxY - startPos.y) / dy;

        double tzMin = (box.minZ - startPos.z) / dz;
        double tzMax = (box.maxZ - startPos.z) / dz;

        double tMin = Math.max(
                Math.max(
                        Math.min(txMin, txMax), Math.min(tyMin, tyMax)
                ),
                Math.min(tzMin, tzMax)
        );

        double tMax = Math.min(Math.min(Math.max(txMin, txMax), Math.max(tyMin, tyMax)), Math.max(tzMin, tzMax));

        if (tMin > tMax || tMax < 0) {
            return null;
        }

        double x = startPos.x + tMin * dx;
        double y = startPos.y + tMin * dy;
        double z = startPos.z + tMin * dz;

        if (x >= box.minX && x <= box.maxX &&
                y >= box.minY && y <= box.maxY &&
                z >= box.minZ && z <= box.maxZ) {
            return new Vec3(x, y, z);
        } else {
            return null;
        }
    }
}
