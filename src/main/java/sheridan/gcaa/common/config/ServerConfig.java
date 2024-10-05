package sheridan.gcaa.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final int DEFAULT_LAG_COMPENSATION_MILLISECONDS = 100;
    public static final int MAX_LAG_COMPENSATION_MILLISECONDS = 300;
    public static final int MIN_LAG_COMPENSATION_MILLISECONDS = 50;
    public static final int MIN_BULLET_POOL_INITIAL_SIZE = 32;
    public static final int MAX_BULLET_POOL_INITIAL_SIZE = 4096;
    public static final int MIN_MAX_BULLET_LIVING_TIME = 1000;
    public static final int MAX_MAX_BULLET_LIVING_TIME = 10000;
    private final static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.BooleanValue enableLagCompensation;
    public static ForgeConfigSpec.IntValue maxLagCompensationMilliseconds;
    public static ForgeConfigSpec.IntValue initialBulletPoolSize;
    public static ForgeConfigSpec.IntValue maxBulletLivingTime;

    static {
        BUILDER.comment("""
                Enable lag compensation
                开启延迟补偿
                """);
        enableLagCompensation = BUILDER.define("enable_lag_compensation", false);
        BUILDER.comment("""
                Max lag compensation milliseconds
                延迟补偿接受的最大延迟毫秒数
                """);
        maxLagCompensationMilliseconds = BUILDER.defineInRange("max_lag_compensation_milliseconds",
                DEFAULT_LAG_COMPENSATION_MILLISECONDS,
                MIN_LAG_COMPENSATION_MILLISECONDS,
                MAX_LAG_COMPENSATION_MILLISECONDS);
        BUILDER.comment("""
                GCAA uses an object pool to handle bullet objects, which may exist in large quantities. This value determines how many bullet objects are initialized when the pool is created.\s
                An appropriate value can prevent the performance loss caused by subsequent memory allocations and pool expansions (each bullet object is approximately 184 bytes).
                If you're running on a server, 1024 would be a good choice, but if you're playing solo, it's recommended to set it to 256.
                GCAA采用对象池来处理子弹这种可能会大量存在的对象，这个值决定了对象池创建时对初始化多少子弹对象。
                合适的值能避免后续的内存分配以及池扩容带来的性能损耗（每个子弹对象大小大约是188 字节）。
                如果您是在服务器上运行，那么1024会是个不错的值，如果您只是独自游玩，建议设置为256。
                """);
        initialBulletPoolSize = BUILDER.defineInRange("initial_bullet_pool_size", 1024, MIN_BULLET_POOL_INITIAL_SIZE, MAX_BULLET_POOL_INITIAL_SIZE);
        BUILDER.comment("""
                The bullet has a maximum survival time of milliseconds, beyond which it will be recovered.
                子弹最大生存毫秒数时间，超过这个时间子弹会被回收
                """);
        maxBulletLivingTime = BUILDER.defineInRange("max_bullet_living_time", 5000, MIN_MAX_BULLET_LIVING_TIME, MAX_MAX_BULLET_LIVING_TIME);
        SPEC = BUILDER.build();
    }
}
