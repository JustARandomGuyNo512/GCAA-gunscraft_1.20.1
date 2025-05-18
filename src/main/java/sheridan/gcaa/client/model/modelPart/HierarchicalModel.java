package sheridan.gcaa.client.model.modelPart;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public abstract class HierarchicalModel<E extends Entity> extends EntityModel<E> {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public HierarchicalModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public HierarchicalModel(Function<ResourceLocation, RenderType> pRenderType) {
        super(pRenderType);
    }

    public final void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {}

    @Override
    public void setupAnim(E pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {}

    public abstract IAnimatedModelPart root();

    public Optional<IAnimatedModelPart> getAnyDescendantWithName(String pName) {
        return pName.equals("root") ? Optional.of(this.root()) : this.root().getAllParts().filter((part) -> {
            return part.hasChild(pName);
        }).findFirst().map((part) -> {
            return part.getChild(pName);
        });
    }
}