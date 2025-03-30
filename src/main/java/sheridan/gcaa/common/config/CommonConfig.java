package sheridan.gcaa.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import sheridan.gcaa.common.HeadBox;

import java.util.List;

public class CommonConfig {
    public static final float MAX_HEADSHOT_DAMAGE_FACTOR = 10.0f;
    public static final float MIN_HEADSHOT_DAMAGE_FACTOR = 1.0f;
    public static final int DEFAULT_LAG_COMPENSATION_MILLISECONDS = 100;
    public static final int MAX_LAG_COMPENSATION_MILLISECONDS = 300;
    public static final int MIN_LAG_COMPENSATION_MILLISECONDS = 50;
    public static final int MIN_BULLET_POOL_INITIAL_SIZE = 32;
    public static final int MAX_BULLET_POOL_INITIAL_SIZE = 8192;
    public static final int MIN_MAX_BULLET_LIVING_TIME = 1000;
    public static final int MAX_MAX_BULLET_LIVING_TIME = 16000;
    public static final float MIN_GLOBAL_BULLET_SPEED_MODIFY = 0.25f;
    public static final float MAX_GLOBAL_BULLET_SPEED_MODIFY = 5f;
    public static final float MIN_GLOBAL_BULLET_DAMAGE_MODIFY = 0.1f;
    public static final float MAX_GLOBAL_BULLET_DAMAGE_MODIFY = 100f;
    private final static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.BooleanValue enableLagCompensation;
    public static ForgeConfigSpec.IntValue maxLagCompensationMilliseconds;
    public static ForgeConfigSpec.IntValue initialBulletPoolSize;
    public static ForgeConfigSpec.IntValue maxBulletLivingTime;
    public static ForgeConfigSpec.BooleanValue enableKnockBackToEntity;
    public static ForgeConfigSpec.BooleanValue enableKnockBackToPlayer;
    public static ForgeConfigSpec.DoubleValue globalBulletSpeedModify;
    public static ForgeConfigSpec.DoubleValue globalBulletDamageModify;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> headshotModify;
    public static ForgeConfigSpec.BooleanValue enableHeadShot;
    public static ForgeConfigSpec.BooleanValue bulletBreakGlass;
    public static ForgeConfigSpec.BooleanValue bulletCrossLeafBlock;
    public static ForgeConfigSpec.BooleanValue creativeModeUseAmmo;

    private static boolean verifyHeadShotModify(Object obj) {
        if (obj instanceof String str) {
            if ("".equals(str) || str.length() <= 0) {
                return false;
            }
            HeadBox headBox = HeadBox.parseAndInit(str);
            return headBox != null;
        }
        return false;
    }

    static {
        BUILDER.comment("""
                Enable lag compensation
                开启延迟补偿""");
        enableLagCompensation = BUILDER.define("enable_lag_compensation", false);
        BUILDER.comment("""
                
                Max lag compensation milliseconds
                延迟补偿接受的最大延迟毫秒数""");
        maxLagCompensationMilliseconds = BUILDER.defineInRange("max_lag_compensation_milliseconds",
                DEFAULT_LAG_COMPENSATION_MILLISECONDS,
                MIN_LAG_COMPENSATION_MILLISECONDS,
                MAX_LAG_COMPENSATION_MILLISECONDS);
        BUILDER.comment("""
                
                GCAA uses an object pool to handle bullet objects, which may exist in large quantities. This value determines how many bullet objects are initialized when the pool is created
                An appropriate value can prevent the performance loss caused by subsequent memory allocations and pool expansions (each bullet object is approximately 184 bytes)
                If you're running on a server, 1024 would be a good choice, but if you're playing solo, it's recommended to set it to 256
                GCAA采用对象池来处理子弹这种可能会大量存在的对象，这个值决定了对象池创建时对初始化多少子弹对象
                合适的值能避免后续的内存分配以及池扩容带来的性能损耗（每个子弹对象大小大约是188 字节）
                如果您是在服务器上运行，那么256会是个不错的值，如果您只是独自游玩，建议设置为32""");
        initialBulletPoolSize = BUILDER.defineInRange("initial_bullet_pool_size", 32, MIN_BULLET_POOL_INITIAL_SIZE, MAX_BULLET_POOL_INITIAL_SIZE);
        BUILDER.comment("""
                
                The bullet has a maximum survival time of milliseconds(1000 milliseconds = 1 second), beyond which it will be recovered
                子弹最大生存毫秒数时间（1000毫秒=1秒），超过这个时间子弹会被回收""");
        maxBulletLivingTime = BUILDER.defineInRange("max_bullet_living_time", 5000, MIN_MAX_BULLET_LIVING_TIME, MAX_MAX_BULLET_LIVING_TIME);
        BUILDER.comment("""
                
                Enable bullet knock back to all entities except players
                开启子弹对于除玩家外的所有实体击退""");
        enableKnockBackToEntity = BUILDER.define("enable_knock_back_to_entity", true);
        BUILDER.comment("""
                
                Enable bullet knock back to player
                开启子弹对于玩家击退""");
        enableKnockBackToPlayer = BUILDER.define("enable_knock_back_to_player", true);
        BUILDER.comment("""
                
                Global bullet speed modify, this only effects on bullets, like grenades, rockets, etc are not affected.
                全局子弹速度乘数，仅影响子弹速度，榴弹或者火箭弹不会受影响""");
        globalBulletSpeedModify = BUILDER.defineInRange("global_bullet_speed_modify", 1, MIN_GLOBAL_BULLET_SPEED_MODIFY, MAX_GLOBAL_BULLET_SPEED_MODIFY);
        BUILDER.comment("""
                
                Global bullet damage modify, this only effects on bullets, like grenades, rockets, etc are not affected.
                全局子弹伤害乘数，仅影响子弹速度，榴弹或者火箭弹不会受影响""");
        globalBulletDamageModify = BUILDER.defineInRange("global_bullet_damage_modify", 1, MIN_GLOBAL_BULLET_DAMAGE_MODIFY, MAX_GLOBAL_BULLET_DAMAGE_MODIFY);
        BUILDER.comment("""
                
                Enable head shot.
                启用爆头判定""");
        enableHeadShot = BUILDER.define("enable_head_shot", true);
        String str0 = "( " + MIN_HEADSHOT_DAMAGE_FACTOR + " ~ " + MAX_HEADSHOT_DAMAGE_FACTOR + " )";
        BUILDER.comment("""
                
                Set the headshot parameters. The following are the parameters of the registered minecraft original creatures.
                You just need to add a custom project that mimics the existing project format.
                First parameter: headshot damage multiplier""" + str0 + """
                \nSecond parameter: the size of the head bounding box (which is a cube) relative to the original mob bounding box.
                Third parameter: the height of the center of the head bounding box relative to the original mob bounding box (0 for the bottom of the foot, 1 for the top of the head).
                配置headshot参数，以下是已经注册好的minecraft原版生物的参数
                你只需要仿照已有的项目格式添加自定义项目就行了
                第一个参数：headshot伤害倍数""" + str0 + """
                \n第二个参数：头部碰撞箱（这是一个正方体）相对于原生物碰撞箱的大小
                第三个参数：头部碰撞箱中心相对于相对于原生物碰撞箱的高度（脚底为0，头顶为1）""");
        headshotModify = BUILDER.defineListAllowEmpty("headshot_modify", List.of(
                "minecraft:zombie=2.0,0.75,0.91",
                "minecraft:husk=2.0,0.8,0.95",
                "minecraft:zombified_piglin=2.0,0.85,0.91",
                "minecraft:piglin_brute=2.0,0.85,0.91",
                "minecraft:piglin=2.0,0.85,0.91",
                "minecraft:enderman=2.0,0.85,0.88",
                "minecraft:drowned=2.0,0.8,0.91",
                "minecraft:zombie_villager=2.0,0.9,0.91",
                "minecraft:pillager=2.0,0.9,0.92",
                "minecraft:evoker=2.0,0.9,0.85",
                "minecraft:allay=2.0,0.88,0.82",
                "minecraft:villager=1.5,0.9,0.85",
                "minecraft:wandering_trader=1.5,0.9,0.88",
                "minecraft:stray=1.5,0.8,0.85",
                "minecraft:vindicator=2.0,0.9,0.86",
                "minecraft:witch=2.0,0.9,0.85",
                "minecraft:blaze=2.0,0.75,0.91",
                "minecraft:skeleton=2.0,0.75,0.87",
                "minecraft:player=1.5,0.7,0.88",
                "minecraft:iron_golem=1.5,0.5,0.85",
                "minecraft:snow_golem=1.5,0.9,0.85",
                "minecraft:vex=2.0,0.9,0.65",
                "minecraft:warden=2.0,1.0,0.88",
                "minecraft:creeper=2.0,0.8,0.84"
        ), CommonConfig::verifyHeadShotModify);
        BUILDER.comment("""
                
                Bullet breaks glass block or glass pane.
                子弹击碎玻璃块或玻璃板""");
        bulletBreakGlass = BUILDER.define("bullet_break_glass", true);
        BUILDER.comment("""
                
                Bullets can go through leaves.
                子弹穿透树叶方块""");
        bulletCrossLeafBlock = BUILDER.define("bullet_cross_leaf_block", true);
        BUILDER.comment("""
                
                Creative mode use ammo.
                创造模式消耗子弹""");
        creativeModeUseAmmo = BUILDER.define("creative_mode_use_ammo", true);
        SPEC = BUILDER.build();
    }
}
