package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.guns.IGun;

@OnlyIn(Dist.CLIENT)
public interface ICameraShakeHandler {
    void use();
    void clear();
    boolean shake(float particleTicks, PoseStack poseStack, IGun gun, Player player, ItemStack itemStack);
}
