package sheridan.gcaa.items.attachments.functional;

import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

public class GP25 extends GrenadeLauncher{
    public GP25() {
        super(ModItems.AMMO_VOG_25.get(),
                new InertialRecoilData(0, 0, 0.9f, 0.08f, 1f,  0.06f, 0.3f, 0),
                4, 2.5f, ModSounds.GP_25_FIRE, RenderAndMathUtils.secondsToTicks(2.1f),
                2.5f, 1f, 3f, 8, 2.5f);
    }

}
