package sheridan.gcaa.client.model.modelPart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class CubeListBuilder {
    public static final Set<Direction> ALL_VISIBLE = EnumSet.allOf(Direction.class);
    private final List<CubeDefinition> cubes = Lists.newArrayList();
    private int xTexOffs;
    private int yTexOffs;
    private boolean mirror;

    public CubeListBuilder texOffs(int xTexOffs, int yTexOffs) {
        this.xTexOffs = xTexOffs;
        this.yTexOffs = yTexOffs;
        return this;
    }

    public CubeListBuilder mirror() {
        return this.mirror(true);
    }

    public CubeListBuilder mirror(boolean mirror) {
        this.mirror = mirror;
        return this;
    }

    public CubeListBuilder addBox(Set<ModelPart.UvPolygon> uvPolygons, float pOriginX, float pOriginY, float pOriginZ, float pDimensionX, float pDimensionY, float pDimensionZ) {
        this.cubes.add(new CubeDefinition(uvPolygons, pOriginX, pOriginY, pOriginZ, pDimensionX, pDimensionY, pDimensionZ, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F));
        return this;
    }

    public CubeListBuilder addBox(Set<ModelPart.UvPolygon> uvPolygons, float pOriginX, float pOriginY, float pOriginZ, float pDimensionX, float pDimensionY, float pDimensionZ, CubeDeformation cubeDeformation) {
        this.cubes.add(new CubeDefinition(uvPolygons, pOriginX, pOriginY, pOriginZ, pDimensionX, pDimensionY, pDimensionZ, cubeDeformation, this.mirror, 1.0F, 1.0F));
        return this;
    }

    public CubeListBuilder addBox(float pOriginX, float pOriginY, float pOriginZ, float pDimensionX, float pDimensionY, float pDimensionZ, Set<Direction> pVisibleFaces) {
        this.cubes.add(new CubeDefinition(null, (float)this.xTexOffs, (float)this.yTexOffs, pOriginX, pOriginY, pOriginZ, pDimensionX, pDimensionY, pDimensionZ, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F, pVisibleFaces));
        return this;
    }

    public CubeListBuilder addBox(float pOriginX, float pOriginY, float pOriginZ, float pDimensionX, float pDimensionY, float pDimensionZ, CubeDeformation pCubeDeformation) {
        this.cubes.add(new CubeDefinition(null, (float)this.xTexOffs, (float)this.yTexOffs, pOriginX, pOriginY, pOriginZ, pDimensionX, pDimensionY, pDimensionZ, pCubeDeformation, this.mirror, 1.0F, 1.0F, ALL_VISIBLE));
        return this;
    }


    public List<CubeDefinition> getCubes() {
        return ImmutableList.copyOf(this.cubes);
    }

    public static CubeListBuilder create() {
        return new CubeListBuilder();
    }
}