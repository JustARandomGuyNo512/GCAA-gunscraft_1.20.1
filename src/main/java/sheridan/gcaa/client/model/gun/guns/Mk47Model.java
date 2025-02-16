package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public class Mk47Model extends CommonRifleModel {
    private ModelPart handguard, rail_left, rail_left_rear, rail_right,
            rail_right_rear, rail_lower, rail_lower_rear;

    public Mk47Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.png"));
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        handguard = main.getChild("handguard");
        rail_left = handguard.getChild("rail_left");
        rail_left_rear = handguard.getChild("rail_left_rear");
        rail_right = handguard.getChild("rail_right");
        rail_right_rear = handguard.getChild("rail_right_rear");
        rail_lower = handguard.getChild("rail_lower");
        rail_lower_rear = handguard.getChild("rail_lower_rear");
        bindSlotReplacement(Attachment.MUZZLE, "muzzle");
        bindSlotReplacement(Attachment.GRIP, "grip");
        bindSlotReplacement(Attachment.STOCK, "stock");
        bindSlotReplacement(GunRenderContext.SCOPE_ALL, "IS");
        bindSlotReplacement(GunRenderContext.SCOPE_ALL, "IS_front");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        handleRailsVisible(context);
        handguard.visible = context.notHasHandguard();
        if (handguard.visible) {
            context.render(handguard, context.solidNoCullMipMap(texture));
            handguard.visible = false;
        }
        super.renderGunModel(context);
    }

    private void handleRailsVisible(GunRenderContext context) {
        rail_left.visible = context.has("hand_guard_left");
        rail_left_rear.visible = context.has("hand_guard_left_rear");
        rail_right.visible = context.has("hand_guard_right");
        rail_right_rear.visible = context.has("hand_guard_right_rear");
        rail_lower.visible = context.has("hand_guard_lower");
        rail_lower_rear.visible = context.has("hand_guard_grip");
    }
}
