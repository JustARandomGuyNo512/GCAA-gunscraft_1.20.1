package sheridan.gcaa.items.gun;

public class ProjectileData {

    public float baseDamage;
    public float minDamage;
    public float effectiveRange;
    public float speed;


    public ProjectileData(float baseDamage, float minDamage, float effectiveRange, float speed) {
        this.baseDamage = baseDamage;
        this.minDamage = minDamage;
        this.effectiveRange = effectiveRange * 16f;
        this.speed = speed;
    }
}
