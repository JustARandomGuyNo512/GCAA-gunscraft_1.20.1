package sheridan.gcaa.items.gun.guns;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.fireModes.HandAction;
import sheridan.gcaa.items.gun.propertyExtensions.AttachmentReplaceFactorExtension;
import sheridan.gcaa.items.gun.propertyExtensions.AttachmentReplaceFactorExtension.*;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Awp extends HandActionGun {
    private static final Caliber caliber =
            new Caliber(new ResourceLocation(GCAA.MODID, "7.62x51mm"), 22, 16, 10f, 15f);

    public Awp() {
        super(new GunProperties(3f, 0.25f, 3f, 1.8f, 0.1f,
                        5f, GunProperties.toRPM(45), getTicks(2.25f), getTicks(3.35f),
                        10, 3.5f, 1f, 0.1f, 0.1f, 22,
                        List.of(HandAction.HAND_ACTION),
                        ModSounds.AWP_FIRE, ModSounds.AWP_FIRE_SUPPRESSED, caliber)
                        .addExtension(new AttachmentReplaceFactorExtension()
                                .addReplaceFactor(Attachment.MUZZLE, 0, new PropertyEntry(GunProperties.RECOIL_PITCH, 0.2f))),
                new HandActionExtension("bolt_action", getTicks(0.5f), getTicks(1.25f), 8));
    }

    @Override
    public boolean isSniper() {
        return true;
    }

    @Override
    public int getCrosshairType() {
        return -1;
    }

    @Override
    public boolean shootCreateBulletShell() {
        return false;
    }
}
