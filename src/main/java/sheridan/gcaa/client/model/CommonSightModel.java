package sheridan.gcaa.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.items.gun.IGun;

public abstract class CommonSightModel extends SightModel {
    protected final ModelPart root;

    public CommonSightModel(ModelPart root)  {
        this.root = root;
        this.root.meshingAll();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return root;
    }

    Vector3f t1 = new Vector3f();
    Vector3f t2 = new Vector3f();
    @Override
    public float handleMinZTranslation(PoseStack poseStack) {
        ModelPart minZDis = getMinZDis();
        if (minZDis != null) {
            float zStart = poseStack.last().pose().getTranslation(t1).z;
            minZDis.translateAndRotate(poseStack);
            float dis = poseStack.last().pose().getTranslation(t2).z - zStart;
            return Math.max(poseStack.last().pose().getTranslation(t2).z, dis);
        }
        return Float.NaN;
    }

    protected abstract ModelPart getMinZDis();
}
