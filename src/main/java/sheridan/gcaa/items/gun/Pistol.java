package sheridan.gcaa.items.gun;

public class Pistol extends Gun{
    public Pistol(GunProperties gunProperties) {
        super(gunProperties);
    }

    @Override
    public boolean isPistol() {
        return true;
    }

    @Override
    public boolean canUseWithShield() {
        return true;
    }
}
