package sheridan.gcaa.client;

import net.minecraft.client.player.LocalPlayer;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class SprintingHandler {
    public static final SprintingHandler INSTANCE = new SprintingHandler();
    private float sprintingProgress;
    private float lastSprintingProgress;
    private float exitSpeed;
    private boolean inSprinting;
    private int sprintingCoolDown;

    public void tick(LocalPlayer player) {
        if (player != null) {
            if (sprintingCoolDown != 0) {
                sprintingCoolDown = Math.max(0, sprintingCoolDown - 1);
                inSprinting = false;
                lastSprintingProgress = sprintingProgress;
                sprintingProgress = Math.max(0, sprintingProgress - exitSpeed);
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                inSprinting = shouldEnterSprinting(player, stack, gun);
                float weight = 1f - RenderAndMathUtils.sCurve(gun.getWeight(stack) / GunProperties.MAX_WEIGHT + 0.05f);
                exitSpeed = 0.09f + weight * 0.1f * gun.getAgility(stack);
                float enterSpeed = exitSpeed * 1.25f;
                if (inSprinting) {
                    lastSprintingProgress = sprintingProgress;
                    sprintingProgress = Math.min(1, sprintingProgress + enterSpeed);
                } else {
                    lastSprintingProgress = sprintingProgress;
                    sprintingProgress = Math.max(0, sprintingProgress - exitSpeed);
                }
            } else {
                exitSprinting(20);
            }
        } else {
            sprintingProgress = 0;
            inSprinting = false;
        }
    }

    public boolean shouldEnterSprinting(LocalPlayer player, ItemStack heldItem, IGun gun) {
        if (!player.isSprinting()) {
            return false;
        }
        if (ReloadingHandler.isReloading() || HandActionHandler.INSTANCE.hasTask() || Clients.mainHandStatus.ads) {
            exitSprinting(10);
        }
        return sprintingCoolDown == 0 && !player.getAbilities().flying && !player.isCrouching() &&
                !ReloadingHandler.isReloading() && !HandActionHandler.INSTANCE.hasTask() && !Clients.mainHandStatus.ads;
    }

    public void exitSprinting(int coolDown)  {
        inSprinting = false;
        sprintingCoolDown = coolDown;
    }

    public float getSprintingProgress() {
        return sprintingProgress;
    }

    public float getLerpedSprintingProgress(float particleTick) {
        float lerped = Mth.lerp(particleTick, lastSprintingProgress, sprintingProgress);
        return RenderAndMathUtils.sCurve(lerped);
    }

    public void clear(int coolDown) {
        inSprinting = false;
        sprintingProgress = 0;
        lastSprintingProgress = 0;
        sprintingCoolDown = coolDown;
    }
}
