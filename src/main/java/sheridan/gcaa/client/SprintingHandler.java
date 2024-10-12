package sheridan.gcaa.client;

import net.minecraft.client.player.LocalPlayer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class SprintingHandler {
    public static final SprintingHandler INSTANCE = new SprintingHandler();
    private LocalPlayer player;
    private float sprintingProgress;
    private float weight;
    private float enterSpeed;
    private float exitSpeed;
    private boolean inSprinting;

    public void update(LocalPlayer player) {
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                weight = gun.getWeight(stack);

            } else {
                //lerp out
            }
            this.player = player;
        } else {
            sprintingProgress = 0;
        }
    }
}
