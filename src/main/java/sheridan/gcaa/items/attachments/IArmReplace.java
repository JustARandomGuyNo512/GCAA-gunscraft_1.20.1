package sheridan.gcaa.items.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IArmReplace {
    boolean replaceArmRender(boolean mainHand);
    int orderForArmRender(boolean mainHand);
    @OnlyIn(Dist.CLIENT)
    default float getPitchRecoilControlIncRate() {return 0;}
    @OnlyIn(Dist.CLIENT)
    default float getYawRecoilControlIncRate() {return 0;}
    @OnlyIn(Dist.CLIENT)
    default float getAgilityIncRate() {return 0;}
}
