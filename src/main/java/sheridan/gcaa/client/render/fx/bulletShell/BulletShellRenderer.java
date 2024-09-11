package sheridan.gcaa.client.render.fx.bulletShell;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Deque;

@OnlyIn(Dist.CLIENT)
public class BulletShellRenderer {
    public static final int MAX_NUM = 20;
    private static final Deque<Task> bulletShells = new ArrayDeque<>(MAX_NUM);

    private static class Task {
        public BulletShellDisplayData displayData;
        public long timeStamp;

        public Task(BulletShellDisplayData displayData, long timeStamp) {
            this.displayData = displayData;
            this.timeStamp = timeStamp;
        }
    }

    public static void push(BulletShellDisplayData bulletShellDisplayData, long timeStamp) {
        if (bulletShells.size() >= MAX_NUM) {
            bulletShells.removeFirst();
        }
        bulletShells.push(new Task(bulletShellDisplayData, timeStamp));
    }

    public static void render() {
        if (bulletShells.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();

        for (Task task : bulletShells) {

        }
    }

    public static void clear() {
        bulletShells.clear();
    }
}
