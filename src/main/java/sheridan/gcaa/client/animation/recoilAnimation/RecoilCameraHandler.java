package sheridan.gcaa.client.animation.recoilAnimation;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.guns.IGun;

@OnlyIn(Dist.CLIENT)
public class RecoilCameraHandler {
    public static final RecoilCameraHandler INSTANCE = new RecoilCameraHandler();

    public void onShoot(IGun gun, ItemStack itemStack, Player player) {

    }

    public void update() {
        Minecraft minecraft = Minecraft.getInstance();
        Camera mainCamera = minecraft.gameRenderer.getMainCamera();

    }
}
