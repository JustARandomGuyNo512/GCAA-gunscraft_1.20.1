package sheridan.gcaa.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.entities.projectiles.Grenade;

import java.util.Objects;
import java.util.function.BiFunction;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES;
    public static final RegistryObject<EntityType<Grenade>> GRENADE;

    static {
        ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GCAA.MODID);
        GRENADE = registerProjectile("grenade", Grenade::new, 3, 8, 0.5f, 0.5f);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerProjectile(String id, BiFunction<EntityType<T>, Level, T> function, int updateInterval, int clientTrackingRange, float sizeX, float sizeY) {
        return ENTITIES.register(id, () -> {
            Objects.requireNonNull(function);
            return EntityType.Builder.of(function::apply, MobCategory.MISC).sized(sizeX, sizeY).updateInterval(updateInterval).clientTrackingRange(clientTrackingRange).fireImmune().build(id);
        });
    }

    public ModEntities() {}
}
