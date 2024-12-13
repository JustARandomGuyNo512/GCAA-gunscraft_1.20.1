package sheridan.gcaa.items.gun.calibers;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sheridan.gcaa.common.server.projetile.Projectile;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.utils.FontUtils;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.List;

public class CaliberGauge12 extends Caliber {
    public int projectileNum;
    public float baseSpread = 1.5f;

    public CaliberGauge12(ResourceLocation name, float baseDamage, float minDamage, float effectiveRange, float speed, int projectileNum) {
        super(name, baseDamage, minDamage, effectiveRange, speed);
        this.projectileNum = projectileNum;
    }

    public CaliberGauge12 modifySpread(float baseSpread) {
        this.baseSpread = baseSpread;
        return this;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        super.writeData(jsonObject);
        jsonObject.addProperty("projectileNum", projectileNum);
        jsonObject.addProperty("baseSpread", baseSpread);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        projectileNum = jsonObject.get("projectileNum").getAsInt();
        baseSpread = jsonObject.get("baseSpread").getAsFloat();
    }

    @Override
    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread) {
        Vec3 angle = player.getLookAngle();
        spread *= Projectile.BASE_SPREAD_INDEX;
        angle = angle.normalize().add(
                RenderAndMathUtils.RANDOM.nextGaussian() * spread,
                RenderAndMathUtils.RANDOM.nextGaussian() * spread,
                RenderAndMathUtils.RANDOM.nextGaussian() * spread).scale(speed);
        for (int i = 0; i < projectileNum; i ++) {
            ProjectileHandler.fire(player, angle, penetration, speed, baseDamage, minDamage, baseSpread, effectiveRange, gun, gunStack);
        }
    }

    @Override
    public void handleTooltip(ItemStack stack, IGun gun, Level levelIn, List<Component> tooltip, TooltipFlag flagIn, boolean detail) {
        int color = FontUtils.getColor(baseDamage * projectileNum, 35, 1);
        tooltip.add(FontUtils.dataTip("tooltip.gun_info.damage", baseDamage + " x " + projectileNum, color));
        if (detail) {
            tooltip.add(FontUtils.dataTip("tooltip.gun_info.effective_range", effectiveRange, 10, 1, "gcaa.unit.chunk"));
            tooltip.add(FontUtils.dataTip("tooltip.gun_info.bullet_speed", speed, 12, 1, "gcaa.unit.chunk_pre_second"));
        }
    }
}
