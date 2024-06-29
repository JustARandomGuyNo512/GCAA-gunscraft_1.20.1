package sheridan.gcaa.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.entities.projectiles.Bullet;

import java.util.Objects;
import java.util.function.BiFunction;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES;
    public static final RegistryObject<EntityType<Bullet>> BULLET;


    static {
        ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GCAA.MODID);
        BULLET = registerProjectile("bullet", Bullet::new);

    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerProjectile(String id, BiFunction<EntityType<T>, Level, T> function) {
        return ENTITIES.register(id, () -> {
            Objects.requireNonNull(function);
            return EntityType.Builder.of(function::apply, MobCategory.MISC).sized(0.25F, 0.25F).updateInterval(1).clientTrackingRange(4).fireImmune().build(id);
        });
    }

    public ModEntities() {}

}
