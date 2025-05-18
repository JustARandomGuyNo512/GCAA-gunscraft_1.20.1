package sheridan.gcaa.entities.projectiles;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.common.config.CommonConfig;
import sheridan.gcaa.common.damageTypes.DamageTypes;
import sheridan.gcaa.common.damageTypes.ProjectileDamage;

import java.util.function.Predicate;

public class Grenade extends Entity{
    Predicate<Entity> GENERIC_TARGETS = (input) -> input instanceof LivingEntity && !input.isSpectator() && input.isAlive();
    public LivingEntity shooter;
    int bounced = 0;
    float explodeRadius;
    int safeTicks = 0;

    public Grenade(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy, float explodeRadius) {
        Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize().add(
                this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy),
                this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy),
                this.random.triangle(0.0D, 0.0172275D * (double)pInaccuracy)).scale(pVelocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.explodeRadius = explodeRadius;
    }

    public void shootFromRotation(LivingEntity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy, float explodeRadius, int safeTicks) {
        float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, pVelocity, pInaccuracy, explodeRadius);
        this.shooter = pShooter;
        Vec3 shooterPos = new Vec3(shooter.getX(), shooter.getY() + shooter.getEyeHeight(shooter.getPose()), shooter.getZ());
        this.setPos(shooterPos.x, shooterPos.y, shooterPos.z);
        this.safeTicks = safeTicks;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 220) {
            explode();
        }
        Vec3 deltaMovement = this.getDeltaMovement();
        if (this.level().isClientSide && this.tickCount >= safeTicks) {
            SimpleParticleType type = this.isInWater() ? ParticleTypes.BUBBLE : ParticleTypes.CRIT;
            for(int i = 0; i < 4; ++i) {
                this.level().addParticle(type,
                        this.getX() + deltaMovement.x * (double)i / 4.0D,
                        this.getY() + deltaMovement.y * (double)i / 4.0D,
                        this.getZ() + deltaMovement.z * (double)i / 4.0D,
                        -deltaMovement.x, -deltaMovement.y + 0.2D, -deltaMovement.z);
            }
        }

        Vec3 prevPos = this.position();
        Vec3 nextPos = prevPos.add(deltaMovement);
        BlockHitResult hitResult = this.level().clip(new ClipContext(prevPos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            if (!this.level().isClientSide) {
                Vec3 hitPos = hitResult.getLocation();
                EntityHitResult entityhitresult = this.findHitEntity(prevPos, hitPos);
                if (entityhitresult != null && entityhitresult.getEntity() != this.shooter) {
                    onHitEntity(entityhitresult.getEntity());
                    return;
                }
            }
            if (this.tickCount <= safeTicks || bounced <= 3) {
                if( this.tickCount > safeTicks && getHitDeg(deltaMovement, hitResult.getDirection()) < 65) {
                    explode();
                    return;
                }
                boolean bounce = true;
                if (bounced == 0 && CommonConfig.bulletBreakGlass.get()) {
                    BlockState blockState = this.level().getBlockState(hitResult.getBlockPos());
                    Block block = blockState.getBlock();
                    if (block instanceof AbstractGlassBlock || block instanceof StainedGlassPaneBlock || "minecraft:glass_pane".equals(BuiltInRegistries.BLOCK.getKey(block).toString())) {
                        this.level().destroyBlock(hitResult.getBlockPos(), false);
                        this.setDeltaMovement(deltaMovement.scale(0.3f));
                        bounce = false;
                        bounced ++;
                    }
                }
                if (bounce) {
                    switch (hitResult.getDirection()) {
                        case UP, DOWN -> deltaMovement = new Vec3(deltaMovement.x, -deltaMovement.y, deltaMovement.z).scale(0.6f);
                        case NORTH, SOUTH -> deltaMovement = new Vec3(deltaMovement.x, deltaMovement.y, -deltaMovement.z).scale(0.6f);
                        case WEST, EAST -> deltaMovement = new Vec3(-deltaMovement.x, deltaMovement.y, deltaMovement.z).scale(0.6f);
                    }
                    bounced ++;
                }
                this.level().playSound(this, hitResult.getBlockPos(), SoundEvents.IRON_GOLEM_HURT, SoundSource.BLOCKS, 1, 1);
                nextPos = hitResult.getLocation();
                if (this.level().getBlockState(hitResult.getBlockPos()).getBlock() instanceof BellBlock bell && this.shooter instanceof Player) {
                    bell.attemptToRing(this, this.level(), hitResult.getBlockPos(), hitResult.getDirection());
                }
            } else {
                explode();
            }
        } else {
            if (!this.level().isClientSide) {
                EntityHitResult entityhitresult = this.findHitEntity(prevPos, nextPos);
                if (entityhitresult != null && entityhitresult.getEntity() != this.shooter) {
                    onHitEntity(entityhitresult.getEntity());
                    return;
                }
            }
        }

        this.setPos(nextPos.x, nextPos.y, nextPos.z);
        float f = this.isInWater() ? 0.88f : 0.99f;
        this.setDeltaMovement(deltaMovement.add(0, -0.04f, 0).scale(f));
        if (bounced >= 3 && this.getDeltaMovement().length() <= 0.1f) {
            explode();
            return;
        }
        double horizontalDistance = deltaMovement.horizontalDistance();
        this.setYRot((float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(deltaMovement.y, horizontalDistance) * (double)(180F / (float)Math.PI)));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dis) {
        return dis <= 32768;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.tickCount = pCompound.getInt("tick");
        this.bounced = pCompound.getShort("bounced");
        this.explodeRadius = pCompound.getFloat("radius");
    }

    public void explode() {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY() + 0.0625f, this.getZ(), explodeRadius, false, Level.ExplosionInteraction.NONE);
            this.discard();
        }
    }

    private void onHitEntity(Entity entity) {
        entity.invulnerableTime = 0;
        float length = (float) this.getDeltaMovement().length();
        if (length > 0.5f) {
            ProjectileDamage damageSource = (ProjectileDamage) DamageTypes.getDamageSource(this.level(), DamageTypes.GENERIC_PROJECTILE, this, this.shooter);
            damageSource.shooter = this.shooter;
            entity.hurt(damageSource, length * 2f);
        }
        explode();
    }

    private float getHitDeg(Vec3 velocity, Direction direction) {
        switch (direction) {
            case UP, DOWN -> {
                float length = (float) velocity.length();
                float cosTheta = (float) (Math.abs(velocity.y) / length);
                return  (float) Math.toDegrees(Math.acos(cosTheta));
            }
            case NORTH, SOUTH -> {
                float length = (float) velocity.length();
                float cosTheta = (float) (Math.abs(velocity.z) / length);
                return  (float) Math.toDegrees(Math.acos(cosTheta));
            }
            case WEST, EAST -> {
                float length = (float) velocity.length();
                float cosTheta = (float) (Math.abs(velocity.x) / length);
                return  (float) Math.toDegrees(Math.acos(cosTheta));
            }
        }
        return 0;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("tick", tickCount);
        pCompound.putShort("bounced", (short) bounced);
        pCompound.putFloat("radius", explodeRadius);
    }

    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), GENERIC_TARGETS);
    }

    @Override
    protected void defineSynchedData() {}

    protected static float lerpRotation(float pCurrentRotation, float pTargetRotation) {
        while(pTargetRotation - pCurrentRotation < -180.0F) {
            pCurrentRotation -= 360.0F;
        }
        while(pTargetRotation - pCurrentRotation >= 180.0F) {
            pCurrentRotation += 360.0F;
        }
        return Mth.lerp(0.2F, pCurrentRotation, pTargetRotation);
    }

    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(
                this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.getXRot(),
                this.getYRot(), this.getType(), 0, this.getDeltaMovement(), this.getYHeadRot()
        );
    }

    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.blocksBuilding = true;
        double d0 = packet.getX();
        double d1 = packet.getY();
        double d2 = packet.getZ();
        this.setPos(d0, d1, d2);
        this.noCulling = true;
    }
}
