package sheridan.gcaa.client.model.modelPart;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.PartPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MeshDefinition {
    private final PartDefinition root = new PartDefinition(ImmutableList.of(), PartPose.ZERO);
    public PartDefinition getRoot() {
        return this.root;
    }
}