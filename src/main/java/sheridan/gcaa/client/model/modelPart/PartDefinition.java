package sheridan.gcaa.client.model.modelPart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.geom.PartPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class PartDefinition {
    private final List<CubeDefinition> cubes;
    private final PartPose partPose;
    private final Map<String, PartDefinition> children = Maps.newHashMap();

    PartDefinition(List<CubeDefinition> cubes, PartPose partPose) {
        this.cubes = cubes;
        this.partPose = partPose;
    }

    PartDefinition(PartPose partPose) {
        this.cubes = new ArrayList<>();
        this.partPose = partPose;
    }

    public PartDefinition addOrReplaceChild(String name, CubeListBuilder builder, PartPose partPose) {
        PartDefinition partdefinition = new PartDefinition(builder.getCubes(), partPose);
        PartDefinition partdefinition1 = this.children.put(name, partdefinition);
        if (partdefinition1 != null) {
            partdefinition.children.putAll(partdefinition1.children);
        }
        return partdefinition;
    }

    public PartDefinition addOrReplaceChild(String name, PartPose partPose) {
        PartDefinition partdefinition = new PartDefinition(partPose);
        PartDefinition partdefinition1 = this.children.put(name, partdefinition);
        if (partdefinition1 != null) {
            partdefinition.children.putAll(partdefinition1.children);
        }
        return partdefinition;
    }

    public PartDefinition putCubes(CubeListBuilder builder) {
        this.cubes.addAll(builder.getCubes());
        return this;
    }


    public ModelPart bake(int textureWidth, int textureHeight) {
        Object2ObjectArrayMap<String, ModelPart> object2arrayobject = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (p_171593_) -> {
            return p_171593_.getValue().bake(textureWidth, textureHeight);
        }, (p_171595_, p_171596_) -> {
            return p_171595_;
        }, Object2ObjectArrayMap::new));
        List<ModelPart.Cube> list = this.cubes.stream().map((p_171589_) -> {
            return p_171589_.bake(textureWidth, textureHeight);
        }).collect(ImmutableList.toImmutableList());
        ModelPart modelpart = new ModelPart(list, object2arrayobject);
        modelpart.setInitialPose(this.partPose);
        modelpart.loadPose(this.partPose);
        return modelpart;
    }

    public PartDefinition getChild(String childName) {
        return this.children.get(childName);
    }
}