package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectileEntityHitResult extends EntityHitResult {
    public AABB boxHit;
    public ProjectileEntityHitResult(Entity pEntity, AABB pBoxHit)  {
        super(pEntity);
        this.boxHit = pBoxHit;
    }
}
