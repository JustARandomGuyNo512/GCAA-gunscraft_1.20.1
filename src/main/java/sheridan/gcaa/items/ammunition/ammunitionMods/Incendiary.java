package sheridan.gcaa.items.ammunition.ammunitionMods;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.common.damageTypes.DamageTypes;
import sheridan.gcaa.common.damageTypes.ProjectileDamage;
import sheridan.gcaa.common.server.projetile.Projectile;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.items.ammunition.AmmunitionMod;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.awt.*;

/**
 * 燃烧弹-普通子弹改造
 */
public class Incendiary extends AmmunitionMod {
    private final float fireDamageRate = 0.1f;

    public Incendiary() {
        super(new ResourceLocation(GCAA.MODID, "incendiary"), 2, ICONS_0, new Vector4i(48, 0, 128, 128),
                "gcaa.ammunition_mod.incendiary", new Color(0xee2816).getRGB());
    }

    @Override
    public Component getSpecialDescription() {
        String str = Component.translatable("gcaa.ammunition_mod.incendiary_special").getString().replace("$rate", FontUtils.toPercentageStr(fireDamageRate));
        return Component.empty().append(Component.literal(str));
    }

    @Override
    public void onHitBlockServer(Projectile projectile, BlockHitResult hitResult, BlockState blockState) {
        BlockState blockStateInner = projectile.shooter.level().getBlockState(hitResult.getBlockPos());
        if (blockStateInner.getBlock() instanceof TntBlock tntBlock) {
            tntBlock.onCaughtFire(blockStateInner, projectile.shooter.level(), hitResult.getBlockPos(), hitResult.getDirection(), projectile.shooter);
            projectile.shooter.level().removeBlock(hitResult.getBlockPos(), false);
        }
    }

    @Override
    public void onHitEntity(Projectile projectile, Entity entity, boolean isHeadSHot, IGun gun, ProjectileHandler.AmmunitionDataCache cache) {
        float baseDamage = projectile.damage / cache.baseDamageRate();
        if (!entity.fireImmune()) {
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
            if (entity.getRemainingFireTicks() == 0) {
                entity.setSecondsOnFire(6);
            }
        }
        entity.hurt(projectile.shooter.level().damageSources().inFire(), baseDamage * getFireDamageRate());
    }

    public float getFireDamageRate() {
        return fireDamageRate;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onHitBlockClient(BlockPos pos, Vector3f hitVec, Direction direction, Vector3f normalVec, Player player) {
        if (player != null) {
            for (int i = 0; i < 5; i++) {
                Vector3f particleVec = normalVec.mul(
                        (float) (Math.random() - 0.5f),
                        (float) (Math.random() - 0.5f),
                        (float) (Math.random() - 0.5f)).mul(0.25f);
                player.level().addParticle(ParticleTypes.SMOKE, hitVec.x, hitVec.y, hitVec.z, particleVec.x, particleVec.y + 0.2f, particleVec.z);
                player.level().addParticle(ParticleTypes.FLAME, hitVec.x, hitVec.y, hitVec.z, particleVec.x, particleVec.y + 0.1f, particleVec.z);
            }
        }
    }

    @Override
    public boolean syncClientHooks() {
        return true;
    }
}
