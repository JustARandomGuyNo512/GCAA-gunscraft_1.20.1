package sheridan.gcaa.client.render.gui.crosshair;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.guns.IGun;

@OnlyIn(Dist.CLIENT)
public class CrossHairRenderer {
    public static final CrossHairRenderer INSTANCE = new CrossHairRenderer();

    public void render(IGun gun, GuiGraphics guiGraphics, Player player, ItemStack itemStack) {

    }
}
