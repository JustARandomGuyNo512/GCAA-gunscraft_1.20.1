package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.common.config.CommonConfig;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class ProjectileHandler {
    private static boolean init = false;
    private static ProjectilePool POOL = null;
    private static List<Projectile> ACTIVE_PROJECTILES = null;
    private static long lastUpdate = 0;
    private static final WeakHashMap<String, List<IAmmunitionMod>> AMMUNITION_MODS_CACHE = new WeakHashMap<>();

    public static void clearAmmunitionModsCache() {
        AMMUNITION_MODS_CACHE.clear();
    }

    /*
    * This method can only be called by server thread!!!
    * */
    public static void fire(LivingEntity shooter, float speed, float damage, float spread, float effectiveRange, IGun gun, ItemStack gunStack) {
        Projectile bullet = POOL.getOrCreate(shooter, speed, damage, spread, effectiveRange, gun);
        ACTIVE_PROJECTILES.add(bullet);
    }

    public static void fire(LivingEntity shooter, Vec3 angle, float speed, float damage, float spread, float effectiveRange, IGun gun, ItemStack gunStack) {
        Projectile bullet = POOL.getOrCreate(shooter, angle, speed, damage, spread, effectiveRange, gun);
        ACTIVE_PROJECTILES.add(bullet);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        init = true;
        POOL= new ProjectilePool(CommonConfig.initialBulletPoolSize.get());
        ACTIVE_PROJECTILES = new ArrayList<>(CommonConfig.initialBulletPoolSize.get());
    }

    @SubscribeEvent
    public static void updateProjectiles(TickEvent.ServerTickEvent event) {
        if (!init) {
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            if (lastUpdate != 0) {
                update(RenderAndMathUtils.secondsFromNow(lastUpdate));
            }
            lastUpdate = System.currentTimeMillis();
        }
    }

    private static void update(float timeDis) {
        for (int i = ACTIVE_PROJECTILES.size() - 1; i >= 0; i--) {
            Projectile projectile = ACTIVE_PROJECTILES.get(i);
            projectile.tick(timeDis);
            if (!projectile.living()) {
                int lastIndex = ACTIVE_PROJECTILES.size() - 1;
                if (i != lastIndex) {
                    ACTIVE_PROJECTILES.set(i, ACTIVE_PROJECTILES.get(lastIndex));
                }
                ACTIVE_PROJECTILES.remove(lastIndex);
                POOL.returnProjectile(projectile);
            }
        }
    }

}
