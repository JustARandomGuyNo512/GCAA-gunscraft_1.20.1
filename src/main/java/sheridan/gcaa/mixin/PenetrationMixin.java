package sheridan.gcaa.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sheridan.gcaa.common.damageTypes.ProjectileDamage;

@Mixin(LivingEntity.class)
public class PenetrationMixin {

    @Inject(method="getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtArmor(Lnet/minecraft/world/damagesource/DamageSource;F)V"), cancellable = true)
    private void getDamageAfterArmorAbsorb(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        if (pDamageSource instanceof ProjectileDamage projectileDamage) {
            float armor = ((LivingEntity)(Object)this).getArmorValue();
            float toughness = (float) ((LivingEntity)(Object)this).getAttributeValue(Attributes.ARMOR_TOUGHNESS);
            float damage = CombatRules.getDamageAfterAbsorb(pDamageAmount, armor, toughness);
            float damageAbsorbed = pDamageAmount - damage;
            if (damageAbsorbed == 0) {
                return;
            }
            float penetration = projectileDamage.penetration;
            float dis = penetration - 1;
            if (dis == 0) {
                return;
            }
            float finalDamage;
            float absorbRate = damageAbsorbed / pDamageAmount;
            if (dis < 0) {
                finalDamage = (float) Mth.lerp(Mth.clamp(Math.pow(dis, 2) * (absorbRate * 1.5f), 0, 1), damage, 0.01f);
                if (penetration < 0.2f && absorbRate > 0.12f) {
                    finalDamage *= Mth.lerp(penetration / 0.2f, 0.75f, 0.1f);
                }
            } else {
                float pRate = (float) (1 - Math.exp(- dis * 1.5f * (1 - absorbRate * absorbRate)));
                finalDamage = Mth.lerp(pRate , damage, pDamageAmount);
            }
            cir.cancel();
            cir.setReturnValue(finalDamage);
        }
    }
}
