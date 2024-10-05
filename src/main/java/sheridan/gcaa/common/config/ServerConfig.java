package sheridan.gcaa.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final int DEFAULT_LAG_COMPENSATION_MILLISECONDS = 100;
    public static final int MAX_LAG_COMPENSATION_MILLISECONDS = 300;
    public static final int MIN_LAG_COMPENSATION_MILLISECONDS = 50;
    public static final int MIN_BULLET_POOL_INITIAL_SIZE = 32;
    public static final int MAX_BULLET_POOL_INITIAL_SIZE = 4096;
    private final static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.BooleanValue enableLagCompensation;
    public static ForgeConfigSpec.IntValue maxLagCompensationMilliseconds;
    public static ForgeConfigSpec.IntValue initialBulletPoolSize;

    static {
        BUILDER.comment("Enable lag compensation\n开启延迟补偿\n");
        enableLagCompensation = BUILDER.define("enable_lag_compensation", false);
        BUILDER.comment("Max lag compensation milliseconds\n延迟补偿接受的最大延迟毫秒数\n");
        maxLagCompensationMilliseconds = BUILDER.defineInRange("max_lag_compensation_milliseconds",
                DEFAULT_LAG_COMPENSATION_MILLISECONDS,
                MIN_LAG_COMPENSATION_MILLISECONDS,
                MAX_LAG_COMPENSATION_MILLISECONDS);
        BUILDER.comment("GCAA uses an object pool to handle bullet objects, which may exist in large quantities. This value determines how many bullet objects are initialized when the pool is created. \n" +
                "An appropriate value can prevent the performance loss caused by subsequent memory allocations and pool expansions (each bullet object is approximately 184 bytes).\n" +
                "If you're running on a server, 1024 would be a good choice, but if you're playing solo, it's recommended to set it to 256.\n" +
                "GCAA采用对象池来处理子弹这种可能会大量存在的对象，这个值决定了对象池创建时对初始化多少子弹对象。\n" +
                "合适的值能避免后续的内存分配以及池扩容带来的性能损耗（每个子弹对象大小大约是184 字节）。\n" +
                "如果您是在服务器上运行，那么1024会是个不错的值，如果您只是独自游玩，建议设置为256。\n");
        initialBulletPoolSize = BUILDER.defineInRange("initial_bullet_pool_size", 1024, MIN_BULLET_POOL_INITIAL_SIZE, MAX_BULLET_POOL_INITIAL_SIZE);
        SPEC = BUILDER.build();
    }
}
