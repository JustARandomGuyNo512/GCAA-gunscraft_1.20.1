package sheridan.gcaa.client.render;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@OnlyIn(Dist.CLIENT)
public class JumpBobbingHandler {
    public static final float OFFSET_SCALE = 0.2f;
    public static final float MAX_OFFSET = 0.5f;
    public static final float MIN_OFFSET = -0.3f;
    private Timer timer;
    private ReentrantLock lock;
    private float lastPlayerSpeed;
    private float a;
    private float velocity;
    private float offset;
    private AtomicBoolean work;
    private static JumpBobbingHandler INSTANCE = new JumpBobbingHandler();
    static {
        INSTANCE.init();
    }

    public static void initInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JumpBobbingHandler();
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

    public static JumpBobbingHandler getInstance() {
        return INSTANCE;
    }

    public static float getOffset() {
        if (INSTANCE == null) {
            return 0;
        }
        float offset = INSTANCE.offset * OFFSET_SCALE;
        if (offset < 0) {
            offset *= 0.8f;
        }
        return Mth.clamp(offset, MIN_OFFSET, MAX_OFFSET);
    }

    public void handle(LocalPlayer localPlayer) {
        float speed = (float) (localPlayer.getY() - localPlayer.yOld);
        float a = speed - lastPlayerSpeed;
        try {
            lock.lock();
            if (a < 0) {
                a *= 0.5f;
            }
            this.a = a;
            if (a != 0) {
                work.set(true);
            }
        } catch (Exception ignored) {
        } finally {
            lock.unlock();
        }
        lastPlayerSpeed = speed;
    }

    private void update() {
        if (work.get()) {
            try {
                lock.lock();
                velocity += a * 0.7f;
                offset += velocity * 0.15f;
                velocity -= offset * 0.12f;
                velocity *= 0.7f;
                if (Math.abs(velocity) < 1e-6 && Math.abs(offset) < 1e-6 && Math.abs(a) < 1e-6)  {
                    velocity = 0;
                    offset = 0;
                    a = 0;
                    work.set(false);
                }
            } catch (Exception ignored) {
            } finally {
                lock.unlock();
            }
        }
    }
}
