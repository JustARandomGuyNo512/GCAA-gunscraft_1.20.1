package sheridan.gcaa.entities.projectiles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.entities.projectiles.damageTypes.DamageTypes;
import sheridan.gcaa.entities.projectiles.damageTypes.ProjectileDamage;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.ClientPlayParticlePacket;

import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class Projectile extends Entity implements IProjectile {
    private static final Random RANDOM = new Random();
    public static final float CHUNK_TO_METER = 0.8f;
    public static final float BASE_SPREAD_INDEX = 0.0087F;
    public float baseDamage;
    public float minDamage;
    public float effectiveRange;
    public LivingEntity shooter;
    public float speed;
    public float spread;
    private float progress;
    private static final Predicate<Entity> PROJECTILE_TARGETS = (input) -> input != null && !input.isSpectator() && !(input instanceof ItemEntity) && !(input instanceof ExperienceOrb);
    public static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(Projectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> EFFECTIVE_RANGE = SynchedEntityData.defineId(Projectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Vector3f> INITIAL_POS = SynchedEntityData.defineId(Projectile.class, EntityDataSerializers.VECTOR3);

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TYPE, 1);
        this.entityData.define(EFFECTIVE_RANGE, 16 * 1f);
        this.entityData.define(INITIAL_POS, new Vector3f(0,0,0));
    }

    public Projectile(EntityType<? extends Projectile> entityTypeIn, Level levelIn) {
        super(entityTypeIn, levelIn);
    }

    public static void spawn(Player player, IGun gun) {

    }

    public Projectile(EntityType<? extends Projectile> entityTypeIn, Level levelIn, LivingEntity shooter, Vec3 lookAngle, float speed, float spread, float damage, float minDamage, int type, float effectiveRange, int force, IGun gun) {
        this(entityTypeIn, levelIn);
        this.shooter = shooter;
        Vec3 shooterPos = new Vec3(shooter.getX(), shooter.getY() + shooter.getEyeHeight(shooter.getPose()), shooter.getZ());
        this.setPos(shooterPos.x, shooterPos.y, shooterPos.z);
        this.firstTick = true;
        this.entityData.set(TYPE, type);
        this.entityData.set(INITIAL_POS, new Vector3f(
                (float) this.getX(),
                (float) this.getY(),
                (float) this.getZ()
        ));
        this.entityData.set(EFFECTIVE_RANGE, effectiveRange);
        shoot(shooter, speed, spread, damage, minDamage, effectiveRange, lookAngle);
    }

    @Override
    public void shoot(LivingEntity shooter, float speed, float spread, float baseDamage, float miniDamage, float effectiveRange, Vec3 angle) {
        this.baseDamage = baseDamage;
        this.minDamage = miniDamage;
        this.effectiveRange = effectiveRange;
        this.speed = speed * CHUNK_TO_METER;
        this.spread = spread * BASE_SPREAD_INDEX;
        Vec3 velocity = (angle).normalize().add(
                RANDOM.nextGaussian() * this.spread,
                RANDOM.nextGaussian() * this.spread,
                RANDOM.nextGaussian() * this.spread).scale(speed);
        this.setDeltaMovement(velocity);
//        double horizontalDistance = velocity.horizontalDistance();
//        this.setYRot( - (float)(Mth.atan2(velocity.x, velocity.z) * (double)(180F / (float)Math.PI)));
//        this.setXRot( - (float)(Mth.atan2(velocity.y, horizontalDistance) * (double)(180F / (float)Math.PI)));
//        this.yRotO = this.getYRot();
//        this.xRotO = this.getXRot();
    }

    @Override
    public void tick() {
        if (!checkAddUpdate()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        super.tick();
        if (!this.level().isClientSide) {
            Vec3 prevPos = this.position();
//            if (this.shooter != null) {
//                System.out.println(this.shooter.position() + "   " + prevPos);
//            }
            Vec3 nextPos = prevPos.add(this.getDeltaMovement());
            HitResult result = getHitResultOnMoveVector(this, PROJECTILE_TARGETS);
            if (result.getType() != HitResult.Type.MISS) {
                if (result.getType() == HitResult.Type.ENTITY) {
                    if (hitEntity((EntityHitResult) result, prevPos, nextPos)) {
                        this.remove(RemovalReason.DISCARDED);
                    }
                }
                if (result.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) result;
                    if (hitBlock(blockHitResult)) {
                        this.remove(RemovalReason.DISCARDED);
                    }
                }
            }
            this.setPos(nextPos.x, nextPos.y, nextPos.z);
            firstTick = false;
        } else {
            //this.type = this.entityData.get(TYPE);
        }

    }

    public boolean hitEntity(EntityHitResult result, Vec3 prevPos, Vec3 nextPos) {
        Entity entity = result.getEntity();
        if (entity == this.shooter || entity instanceof Projectile) {
            return false;
        }
        entity.invulnerableTime = 0;
        ProjectileDamage damageSource = (ProjectileDamage) DamageTypes.getDamageSource(this.level(), DamageTypes.GENERIC_PROJECTILE, this, this.shooter);
        damageSource.shooter = this.shooter;
        //damageSource.gun = gun;
        float prevDamage = baseDamage - (baseDamage - minDamage) * progress;
//        if (CommonConfig.getEnableHeadShot()) {
//            HeadBox.HeadShotResult headShotResult = HeadBox.getHeadShot(entity, prevPos, nextPos);
//            if (headShotResult.getIsHeadShot()) {
//                prevDamage *= headShotResult.getDamageModify();
//                if (!this.level().isClientSide && this.shooter != null && this.shooter instanceof Player) {
//                    PacketHandler.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) this.shooter), new HeadShotFeedBackPacket());
//                }
//            }
//        }
        return entity.hurt(damageSource, prevDamage);
    }

    public HitResult getHitResultOnMoveVector(Entity entity, Predicate<Entity> predicate) {
        Vec3 vec3 = entity.getDeltaMovement();
        Level level = entity.level();
        Vec3 vec31 = entity.position();
        return getHitResult(vec31, entity, predicate, vec3, level);
    }

    private HitResult getHitResult(Vec3 p_278237_, Entity p_278320_, Predicate<Entity> predicate, Vec3 p_278342_, Level level) {
        Vec3 vec3 = p_278237_.add(p_278342_);
        HitResult hitresult = level.clip(new ClipContext(p_278237_, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_278320_));
        if (hitresult.getType() != HitResult.Type.MISS) {
            vec3 = hitresult.getLocation();
        }

        HitResult entityHitResult = getEntityHitResult(level, p_278320_, p_278237_, vec3, p_278320_.getBoundingBox().expandTowards(p_278342_).inflate(1.0D), predicate);
        if (entityHitResult != null) {
            hitresult = entityHitResult;
        }

        return hitresult;
    }

    public EntityHitResult getEntityHitResult(Level level, Entity entity, Vec3 vec3, Vec3 endVec, AABB entityAABB, Predicate<Entity> predicate) {
        double d0 = Double.MAX_VALUE;
        Entity result = null;
        for(Entity e : level.getEntities(entity, entityAABB, predicate)) {
            if (e != this.shooter && e.getId() != this.shooter.getId() && !(e instanceof Projectile)) {
                AABB aabb = e.getBoundingBox().inflate(0.3f);
                Optional<Vec3> optional = aabb.clip(vec3, endVec);
                if (optional.isPresent()) {
                    double d1 = vec3.distanceToSqr(optional.get());
                    if (d1 < d0) {
                        result = e;
                        d0 = d1;
                    }
                } else if (firstTick) {
                    firstTick = false;
                    Vec3 hitPos = intersect(vec3, endVec, e.getBoundingBox());
                    if (hitPos != null && vec3.distanceToSqr(endVec) >= vec3.distanceToSqr(hitPos)) {
                        double d1 = vec3.distanceToSqr(hitPos);
                        if (d1 < d0) {
                            result = e;
                            d0 = d1;
                        }
                    }
                }
            }
        }

        return result == null ? null : new EntityHitResult(result);
    }

    public static Vec3 intersect(Vec3 startPos, Vec3 endPos, AABB box) {
        double dx = endPos.x - startPos.x;
        double dy = endPos.y - startPos.y;
        double dz = endPos.z - startPos.z;

        double tx_min = (box.minX - startPos.x) / dx;
        double tx_max = (box.maxX - startPos.x) / dx;

        double ty_min = (box.minY - startPos.y) / dy;
        double ty_max = (box.maxY - startPos.y) / dy;

        double tz_min = (box.minZ - startPos.z) / dz;
        double tz_max = (box.maxZ - startPos.z) / dz;

        double t_min = Math.max(
                Math.max(
                        Math.min(tx_min, tx_max), Math.min(ty_min, ty_max)
                ),
                Math.min(tz_min, tz_max)
        );

        double t_max = Math.min(Math.min(Math.max(tx_min, tx_max), Math.max(ty_min, ty_max)), Math.max(tz_min, tz_max));

        if (t_min > t_max || t_max < 0) {
            return null;
        }

        double x = startPos.x + t_min * dx;
        double y = startPos.y + t_min * dy;
        double z = startPos.z + t_min * dz;

        if (x >= box.minX && x <= box.maxX &&
                y >= box.minY && y <= box.maxY &&
                z >= box.minZ && z <= box.maxZ) {
            return new Vec3(x, y, z);
        } else {
            return null;
        }
    }

    public boolean hitBlock(BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        BlockState state = this.level().getBlockState(result.getBlockPos());
        boolean notThrough = !(state.getBlock() instanceof LeavesBlock) && !(state.getBlock() instanceof AirBlock);
        boolean flag = checkForHitSpecialBlock(pos, state, result);
        if (notThrough && flag) {
            PacketHandler.simpleChannel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                    result.getLocation().x,
                    result.getLocation().y,
                    result.getLocation().z,
                    48,
                    this.level().dimension()
            )), new ClientPlayParticlePacket(pos, result.getLocation(), result.getDirection(), 20));
        }
        return notThrough;
    }

    private boolean checkForHitSpecialBlock(BlockPos pos, BlockState state, BlockHitResult result) {
        if (state.getBlock() instanceof BellBlock bell && this.shooter instanceof Player) {
            return !bell.attemptToRing(this, this.level(), pos, result.getDirection());
        }
        if (state.getBlock() instanceof TargetBlock && this.shooter instanceof Player) {
            //if (CommonConfig.getShotActiveRedstone()) {
                handleTargetBlockSignal(this.level(), state, result, this);
            //}
        }
        if (state.getBlock() instanceof AbstractGlassBlock) {
            //if (CommonConfig.getShotBreakGlass()) {
                this.level().destroyBlock(pos, false);
                return false;
            //}
        }
        return true;
    }

    private void handleTargetBlockSignal(Level level, BlockState blockState, BlockHitResult result, Entity entity) {
        int index = getRedStoneStrength(result, result.getLocation());
        int j = entity instanceof AbstractArrow ? 20 : 8;
        if (!level.getBlockTicks().hasScheduledTick(result.getBlockPos(), blockState.getBlock())) {
            setOutputPower(level, blockState, index, result.getBlockPos(), j);
        }
        if (shooter instanceof ServerPlayer serverplayer) {
            serverplayer.awardStat(Stats.TARGET_HIT);
            CriteriaTriggers.TARGET_BLOCK_HIT.trigger(serverplayer, this, result.getLocation(), index);
        }
    }

    private static final IntegerProperty OUTPUT_POWER = BlockStateProperties.POWER;
    private static void setOutputPower(LevelAccessor p_57386_, BlockState p_57387_, int p_57388_, BlockPos p_57389_, int p_57390_) {
        p_57386_.setBlock(p_57389_, p_57387_.setValue(OUTPUT_POWER, p_57388_), 3);
        p_57386_.scheduleTick(p_57389_, p_57387_.getBlock(), p_57390_);
    }

    private int getRedStoneStrength(BlockHitResult p_57409_, Vec3 p_57410_) {
        Direction direction = p_57409_.getDirection();
        double d0 = Math.abs(Mth.frac(p_57410_.x) - 0.5D);
        double d1 = Math.abs(Mth.frac(p_57410_.y) - 0.5D);
        double d2 = Math.abs(Mth.frac(p_57410_.z) - 0.5D);
        Direction.Axis direction$axis = direction.getAxis();
        double d3;
        if (direction$axis == Direction.Axis.Y) {
            d3 = Math.max(d0, d2);
        } else if (direction$axis == Direction.Axis.Z) {
            d3 = Math.max(d0, d1);
        } else {
            d3 = Math.max(d1, d2);
        }
        return Math.max(1, Mth.ceil(15.0D * Mth.clamp((0.5D - d3) / 0.5D, 0.0D, 1.0D)));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {}



    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(
                this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.getXRot(),
                this.getYRot(), this.getType(), 0, this.getDeltaMovement(), this.getYHeadRot()
        );
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.blocksBuilding = true;
        double d0 = packet.getX();
        double d1 = packet.getY();
        double d2 = packet.getZ();
        this.setPos(d0, d1, d2);
    }

    private boolean checkAddUpdate() {
        if (this.tickCount >= 100) {
            return false;
        }
        return this.level().isClientSide || this.shooter != null;
//        Vec3 pos = this.position();
//        Vector3f initialPos = this.entityData.get(INITIAL_POS);
//        float effectiveRange = this.entityData.get(EFFECTIVE_RANGE);
//        float disX = (float) (pos.x - initialPos.x);
//        float disY = (float) (pos.y - initialPos.y);
//        float disZ = (float) (pos.z - initialPos.z);
//        progress = (disX * disX + disY * disY + disZ * disZ) / (effectiveRange * effectiveRange);
//        return progress < 1.0f;
    }

    @Override
    public Projectile get() {
        return this;
    }
}
