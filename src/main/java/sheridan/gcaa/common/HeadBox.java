package sheridan.gcaa.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.common.config.CommonConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record HeadBox(float damageModify, float size, float yPos) {

    private static final Map<EntityType<?>, HeadBox> headBoxMap = new HashMap<>();

    public static final HeadShotResult MISS = new HeadShotResult(1.0f, false);

    public static void register(EntityType<?> type, HeadBox headBox) {
        if (!headBoxMap.containsKey(type)) {
            headBoxMap.put(type, headBox);
        }
    }

    public static HeadShotResult getHeadShotResult(Entity entity, @Nullable AABB entityAABB, Vec3 startPos, Vec3 endPos) {
        HeadBox headBox = headBoxMap.get(entity.getType());
        if (headBox != null) {
            AABB headAABB = entityAABB == null ? headBox.createAABB(entity) : headBox.createAABB(entityAABB);
            Optional<Vec3> hit = headAABB.clip(startPos, endPos);
            if (hit.isPresent()) {
                return new HeadShotResult(headBox.damageModify, true);
            }
        }
        return MISS;
    }

    public static boolean contains(EntityType<?> entityType) {
        return headBoxMap.containsKey(entityType);
    }

    public static HeadBox getBox(EntityType<?> entityType) {
        return contains(entityType) ? headBoxMap.get(entityType) : null;
    }

    public AABB createAABB(Entity entity) {
        return createAABB(entity.getBoundingBox().inflate(0, 0.3, 0));
    }

    public AABB createAABB(AABB entityAABB) {
        double sx = entityAABB.getXsize();
        double sy = entityAABB.getYsize();
        double len = sx * this.size;
        Vec3 center = entityAABB.getCenter();
        return new AABB(
                center.x - len / 2,
                entityAABB.minY + yPos * sy - len / 2,
                center.z - len / 2,
                center.x + len / 2,
                entityAABB.minY + yPos * sy + len / 2,
                center.z + len / 2
        );
    }

    public HeadBox(float damageModify, float size, float yPos) {
        this.size = size;
        this.yPos = yPos;
        this.damageModify = Math.max(damageModify, 1.0f);
    }

    public static HeadBox parseAndInit(String str) {
        try {
            String[] strings = str.split("=");
            if (strings.length != 2) {
                return null;
            }
            ResourceLocation resourceLocation = new ResourceLocation(strings[0]);
            if (ForgeRegistries.ENTITY_TYPES.containsKey(resourceLocation)) {
                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
                if (entityType == null) {
                    return null;
                }
                String[] params = strings[1].split(",");
                if (params.length == 3) {
                    float damageModify = Float.parseFloat(params[0]);
                    damageModify = Math.abs(damageModify);
                    damageModify = Math.max(damageModify, CommonConfig.MIN_HEADSHOT_DAMAGE_FACTOR);
                    damageModify = Math.min(damageModify, CommonConfig.MAX_HEADSHOT_DAMAGE_FACTOR);

                    float size = Float.parseFloat(params[1]);
                    float yPos = Float.parseFloat(params[2]);

                    HeadBox headBox = new HeadBox(damageModify, size, yPos);
                    headBoxMap.put(entityType, headBox);
                    return headBox;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public String toString() {
        return "HeadBox{" +
                "size=" + size +
                ", yPos=" + yPos +
                ", damageModify=" + damageModify +
                '}';
    }

    public record HeadShotResult(float damageModify, boolean isHeadShot) {

        public float getDamageModify() {
            return isHeadShot ? damageModify : 1.0f;
        }

        public boolean getIsHeadShot() {
            return isHeadShot;
        }
    }

}
