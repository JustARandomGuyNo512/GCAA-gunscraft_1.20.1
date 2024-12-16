package sheridan.gcaa.client.render;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@OnlyIn(Dist.CLIENT)
public class InertialBobbingHandler {
    public static final float OFFSET_SCALE = 0.2f;
    public static final float MAX_OFFSET = 0.5f;
    public static final float MIN_OFFSET = -0.3f;
    private Timer timer;
    private ReentrantLock lock;
    private float lastPlayerYSpeed;
    private float YA;
    private float yVelocity;
    private float xVelocity;
    private float yOffset;
    private float xOffset;
    private float lastPlayerXSpeed;
    private float XA;
    private AtomicBoolean work;
    private static InertialBobbingHandler INSTANCE = new InertialBobbingHandler();
    static {
        INSTANCE.init();
    }

    public static void initInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InertialBobbingHandler();
            INSTANCE.init();
        }
    }

    private void init() {
        work = new AtomicBoolean(false);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    update();
                } catch (Exception ignore) {}
            }
        }, 0, 10L);
        lock = new ReentrantLock();
    }

    private void stop() {
        timer.cancel();
    }

    public static void clear() {
        INSTANCE.stop();
        INSTANCE = null;
    }

    public static InertialBobbingHandler getInstance() {
        return INSTANCE;
    }

    public static float getYOffset() {
        if (INSTANCE == null) {
            return 0;
        }
        float offset = INSTANCE.yOffset * OFFSET_SCALE;
        if (offset < 0) {
            offset *= 0.8f;
        }
        return Mth.clamp(offset, MIN_OFFSET, MAX_OFFSET);
    }

    public static float getXOffset() {
        if (INSTANCE == null) {
            return 0;
        }
        float offset = INSTANCE.xOffset * OFFSET_SCALE;
        return Mth.clamp(offset, MIN_OFFSET, MAX_OFFSET);
    }

    public void handle(LocalPlayer localPlayer) {
        float ySpeed = (float) (localPlayer.getY() - localPlayer.yOld);
        float a = ySpeed - lastPlayerYSpeed;
        float xSpeed = getSidewaysSpeed(localPlayer);
        float xa = xSpeed - lastPlayerXSpeed;
        try {
            lock.lock();
            this.XA = xa;
            if (a < 0) {
                a *= 0.5f;
            }
            this.YA = a;
            if (a != 0 || xa != 0) {
                work.set(true);
            }
        } catch (Exception ignored) {
        } finally {
            lock.unlock();
        }
        lastPlayerYSpeed = ySpeed;
        lastPlayerXSpeed = xSpeed;
    }

    private float getSidewaysSpeed(LocalPlayer localPlayer) {
        Vec3 motion = localPlayer.getDeltaMovement();
        float yaw = localPlayer.getYRot();
        float sideX = (float) Math.cos(Math.toRadians(yaw));
        float sideZ = (float) Math.sin(Math.toRadians(yaw));
        return (float) (motion.x * sideX + motion.z * sideZ);
    }

    private void update() {
        if (work.get()) {
            try {
                lock.lock();
                xVelocity += XA * 0.6f;
                yVelocity += YA * 0.7f;
                yOffset += yVelocity * 0.15f;
                xOffset += xVelocity * 0.12f;
                yVelocity -= yOffset * 0.12f;
                xVelocity -= xOffset * 0.12f;
                yVelocity *= 0.7f;
                xVelocity *= 0.85f;
                if ((Math.abs(yVelocity) < 1e-5 && Math.abs(yOffset) < 1e-5 && Math.abs(YA) < 1e-5) &&
                    (Math.abs(xVelocity) < 1e-5 && Math.abs(xOffset) < 1e-5 && Math.abs(XA) < 1e-5))  {
                    yVelocity = 0;
                    yOffset = 0;
                    YA = 0;
                    xVelocity = 0;
                    xOffset = 0;
                    XA = 0;
                    work.set(false);
                }
            } catch (Exception ignored) {
            } finally {
                lock.unlock();
            }
        }
    }
}
