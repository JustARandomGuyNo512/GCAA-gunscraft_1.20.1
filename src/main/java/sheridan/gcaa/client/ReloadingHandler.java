package sheridan.gcaa.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReloadingHandler {
    public static final ReloadingHandler INSTANCE = new ReloadingHandler();

    private IReloadingTask reloadingTask;

    public boolean reloading() {
        return reloadingTask != null;
    }
}
