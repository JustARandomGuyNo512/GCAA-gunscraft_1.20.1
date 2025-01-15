package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.common.server.projetile.Projectile;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.awt.*;

/**
 * 高爆弹-普通子弹改装
 */
public class Explosive extends AmmunitionMod {
    private final float explosiveDamageRate = 0.5f;

    public Explosive() {
        super(new ResourceLocation(GCAA.MODID, "explosive"), 3, ICONS_0, new Vector4i(16, 0, 128, 128),
                "gcaa.ammunition_mod.explosive", new Color(0xe5944e).getRGB(), 120);
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {
        dataRateTag.putFloat(Ammunition.BASE_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.BASE_DAMAGE_RATE) + 0.1f);
        dataRateTag.putFloat(Ammunition.MIN_DAMAGE_RATE, dataRateTag.getFloat(Ammunition.MIN_DAMAGE_RATE) + 0.1f);
        dataRateTag.putFloat(Ammunition.PENETRATION_RATE, dataRateTag.getFloat(Ammunition.PENETRATION_RATE) - 0.3f);
    }

    @Override
    public Component getSpecialDescription() {
        String str = Component.translatable("gcaa.ammunition_mod.explosive_special").getString().replace("$rate", FontUtils.toPercentageStr(explosiveDamageRate));
        return Component.empty().append(Component.literal(str));
    }

    public float getExplosiveDamageRate() {
        return explosiveDamageRate;
    }

    @Override
    public void onHitEntity(Projectile projectile, Entity entity, boolean isHeadSHot, IGun gun, ProjectileHandler.AmmunitionDataCache cache) {
        if (entity.ignoreExplosion()) {
            return;
        }
        entity.invulnerableTime = 0;
        float baseDamage = projectile.damage / cache.baseDamageRate();
        DamageSource explosion = projectile.shooter.level().damageSources().explosion(projectile.shooter, projectile.shooter);
        entity.hurt(explosion, baseDamage * getExplosiveDamageRate());
    }

    @Override
    public void onHitBlockServer(Projectile projectile, BlockHitResult hitResult, BlockState blockState) {
        BlockState blockStateInner = projectile.shooter.level().getBlockState(hitResult.getBlockPos());
        if (blockStateInner.getBlock() instanceof TntBlock tntBlock) {
            tntBlock.onCaughtFire(blockStateInner, projectile.shooter.level(), hitResult.getBlockPos(), hitResult.getDirection(), projectile.shooter);
            projectile.shooter.level().removeBlock(hitResult.getBlockPos(), false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onHitBlockClient(BlockPos pos, Vector3f hitVec, Direction direction, Vector3f normalVec, Player player) {
        if (player != null) {
            player.level().addParticle(ParticleTypes.EXPLOSION, hitVec.x, hitVec.y, hitVec.z, 0, 0, 0);
        }
    }

    @Override
    public boolean syncClientHooks() {
        return true;
    }
}
