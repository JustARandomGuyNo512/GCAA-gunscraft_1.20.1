package sheridan.gcaa.client.model.modelPart;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Set;

import static sheridan.gcaa.client.model.modelPart.CubeListBuilder.ALL_VISIBLE;


@OnlyIn(Dist.CLIENT)
public class CubeDefinition {
    private final Vector3f origin;
    private final Vector3f dimensions;
    private final CubeDeformation grow;
    private final boolean mirror;
    private final UVPair texCoord;
    private final UVPair texScale;
    private final Set<Direction> visibleFaces;
    private final Set<ModelPart.UvPolygon> uvPolygons;

    protected CubeDefinition(@Nullable String pComment, float pTexCoordU, float pTexCoordV, float pOriginX, float pOriginY, float pOriginZ, float pDimensionX, float pDimensionY, float pDimensionZ, CubeDeformation pGrow, boolean pMirror, float pTexScaleU, float pTexScaleV, Set<Direction> pVisibleFaces) {
        this.texCoord = new UVPair(pTexCoordU, pTexCoordV);
        this.origin = new Vector3f(pOriginX, pOriginY, pOriginZ);
        this.dimensions = new Vector3f(pDimensionX, pDimensionY, pDimensionZ);
        this.grow = pGrow;
        this.mirror = pMirror;
        this.texScale = new UVPair(pTexScaleU, pTexScaleV);
        this.visibleFaces = pVisibleFaces;
        uvPolygons = null;
    }

    protected CubeDefinition(Set<ModelPart.UvPolygon> uvPolygons,  float pOriginX, float pOriginY, float pOriginZ, float pDimensionX, float pDimensionY, float pDimensionZ, CubeDeformation grow, boolean mirror, float pTexScaleU, float pTexScaleV) {
        this.texCoord = new UVPair(0, 0);
        this.origin = new Vector3f(pOriginX, pOriginY, pOriginZ);
        this.dimensions = new Vector3f(pDimensionX, pDimensionY, pDimensionZ);
        this.grow = grow;
        this.mirror = mirror;
        this.texScale = new UVPair(pTexScaleU, pTexScaleV);
        this.visibleFaces = ALL_VISIBLE;
        this.uvPolygons = uvPolygons;
    }

    public ModelPart.Cube bake(int textureWidth, int textureHeight){
        if (uvPolygons == null) {
            return new ModelPart.Cube((int)this.texCoord.u(), (int)this.texCoord.v(), this.origin.x(), this.origin.y(), this.origin.z(), this.dimensions.x(), this.dimensions.y(), this.dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, this.mirror, (float)textureWidth * this.texScale.u(), (float)textureHeight * this.texScale.v(), this.visibleFaces);
        } else {
            return new ModelPart.Cube(this.origin.x(), this.origin.y(), this.origin.z(), this.dimensions.x(), this.dimensions.y(), this.dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, (float)textureWidth * this.texScale.u(), (float)textureHeight * this.texScale.v(), mirror, uvPolygons);
        }
    }
}