package sheridan.gcaa.items.attachments.functional;

import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

public class M203 extends GrenadeLauncher{
    public M203() {
        super(ModItems.AMMO_M433.get(),
                new InertialRecoilData(0, 0, 1f, 0.08f, 1.3f,  0.06f, 0.5f, 0.25f),
                5, 3f, ModSounds.M203_FIRE, RenderAndMathUtils.secondsToTicks(3.2f), 4.1f, 0.8f, 4f, 6);
    }
}
