package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import sheridan.gcaa.common.config.CommonConfig;

import java.util.ArrayDeque;
import java.util.Deque;

public class PosCache {
    private record PositionEntry(Vec3 position, long timestamp) {
        public Vec3 getPosition() {
            return position;
        }
        public long getTimestamp() {
            return timestamp;
        }
    }

    private final Deque<PositionEntry> posQueue = new ArrayDeque<>(10);

    public void clear() {
        posQueue.clear();
    }

    public void update(Player player) {
        int maxDelay = CommonConfig.maxLagCompensationMilliseconds.get();
        long now = System.currentTimeMillis();
        Vec3 currentPos = player.position();
        posQueue.addLast(new PositionEntry(currentPos, now));
        while (!posQueue.isEmpty()) {
            PositionEntry entry = posQueue.peekFirst();
            if (entry.getTimestamp() < now - maxDelay) {
                posQueue.removeFirst();
            } else {
                break;
            }
        }
    }

    public AABB getBoundingBox(int delay, Player player, float inflate) {
        if (delay <= CommonConfig.MIN_LAG_COMPENSATION_MILLISECONDS || delay >= CommonConfig.maxLagCompensationMilliseconds.get()) {
            return player.getBoundingBox().inflate(inflate);
        }
        long targetTime = System.currentTimeMillis() - delay;
        Vec3 interpolatedPos = null;
        for (PositionEntry entry : posQueue) {
            if (entry.getTimestamp() <= targetTime) {
                interpolatedPos = entry.getPosition();
            } else {
                break;
            }
        }
        if (interpolatedPos != null) {
            return makeBoundingBox(player.getBbWidth(), player.getBbHeight(), interpolatedPos.x, interpolatedPos.y, interpolatedPos.z).inflate(inflate);
        }
        return null;
    }

    private AABB makeBoundingBox(float width, float height, double pX, double pY, double pZ) {
        float f = width * 0.5f;
        return new AABB(pX - f, pY, pZ - f, pX + f, pY + height, pZ + f);
    }
}
