package sheridan.gcaa.entities.projectiles;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.entities.projectiles.damageTypes.DamageTypes;
import sheridan.gcaa.entities.projectiles.damageTypes.ProjectileDamage;

import java.util.Random;
import java.util.function.Predicate;

public class Grenade extends Entity implements IProjectile{
    private LivingEntity shooter;
    int bounced = 0;

    public Grenade(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
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
    }

    public void shootFromRotation(LivingEntity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, pVelocity, pInaccuracy);
        this.shooter = pShooter;
        Vec3 shooterPos = new Vec3(shooter.getX(), shooter.getY() + shooter.getEyeHeight(shooter.getPose()), shooter.getZ());
        this.setPos(shooterPos.x, shooterPos.y, shooterPos.z);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 220) {
            explode();
        }
        Vec3 deltaMovement = this.getDeltaMovement();
        if (this.level().isClientSide && this.tickCount >= 8) {
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
            if (this.tickCount <= 8 || bounced <= 3) {
                if( this.tickCount > 8 && getHitDeg(deltaMovement, hitResult.getDirection()) < 65) {
                    explode();
                    return;
                }
                switch (hitResult.getDirection()) {
                    case UP, DOWN -> deltaMovement = new Vec3(deltaMovement.x, -deltaMovement.y, deltaMovement.z).scale(0.6f);
                    case NORTH, SOUTH -> deltaMovement = new Vec3(deltaMovement.x, deltaMovement.y, -deltaMovement.z).scale(0.6f);
                    case WEST, EAST -> deltaMovement = new Vec3(-deltaMovement.x, deltaMovement.y, deltaMovement.z).scale(0.6f);
                }
                this.level().playSound(this, hitResult.getBlockPos(), SoundEvents.IRON_GOLEM_HURT, SoundSource.BLOCKS, 1, 1);
                bounced ++;
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
        setXRot((float)(Mth.atan2(deltaMovement.y, deltaMovement.horizontalDistance()) * (double)(180F / (float)Math.PI)));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dis) {
        return dis <= 32768;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.tickCount = pCompound.getInt("tick");
    }

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.0f, false, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    private void onHitEntity(Entity entity) {
        entity.invulnerableTime = 0;
        ProjectileDamage damageSource = (ProjectileDamage) DamageTypes.getDamageSource(this.level(), DamageTypes.GENERIC_PROJECTILE, this, this.shooter);
        damageSource.shooter = this.shooter;
        entity.hurt(damageSource, Math.max(2, 5 - bounced));
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

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.blocksBuilding = true;
        double d0 = packet.getX();
        double d1 = packet.getY();
        double d2 = packet.getZ();
        this.setPos(d0, d1, d2);
        this.noCulling = true;
    }
}