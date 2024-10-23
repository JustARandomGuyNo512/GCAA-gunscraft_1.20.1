package sheridan.gcaa.items.gun.calibers;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;


public class Caliber {
    public static final ResourceLocation CALIBER_762X39MM = new ResourceLocation(GCAA.MODID, "7.62x39mm");
    public static final ResourceLocation CALIBER_556X45MM = new ResourceLocation(GCAA.MODID, "5.56x45mm");
    public static final ResourceLocation CALIBER_9MM = new ResourceLocation(GCAA.MODID, "9mm");
    public static final ResourceLocation CALIBER_762X51MM = new ResourceLocation(GCAA.MODID, "7.62x51mm");
    public static final ResourceLocation CALIBER_357_MAGNUM = new ResourceLocation(GCAA.MODID, ".357_magnum");
    public static final ResourceLocation CALIBER_12_GAUGE = new ResourceLocation(GCAA.MODID, "12_gauge");
    public static final ResourceLocation CALIBER_45_ACP = new ResourceLocation(GCAA.MODID, ".45_acp");

    private final ResourceLocation name;
    public float baseDamage;
    public float minDamage;
    public float effectiveRange;
    public float speed;

    public Caliber(ResourceLocation name, float baseDamage, float minDamage, float effectiveRange, float speed) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.minDamage = minDamage;
        this.effectiveRange = effectiveRange;
        this.speed = speed;
    }

    public final ResourceLocation getName() {
        return name;
    }

    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack, float spread) {
        ProjectileHandler.fire(player, speed, baseDamage, spread, effectiveRange, gun);
    }

    public void handleTooltip(ItemStack stack, IGun gun, Level levelIn, List<Component> tooltip, TooltipFlag flagIn, boolean detail) {
        tooltip.add(FontUtils.dataTip("tooltip.gun_info.damage", baseDamage, 35, 1));
        if (detail) {
            tooltip.add(FontUtils.dataTip("tooltip.gun_info.effective_range", effectiveRange, 10, 1, "gcaa.unit.chunk"));
            tooltip.add(FontUtils.dataTip("tooltip.gun_info.bullet_speed", speed, 12, 1, "gcaa.unit.chunk_pre_second"));
        }
    }
}
