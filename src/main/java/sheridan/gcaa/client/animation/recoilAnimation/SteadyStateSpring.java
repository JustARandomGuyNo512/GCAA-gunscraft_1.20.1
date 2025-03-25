package sheridan.gcaa.client.animation.recoilAnimation;

public class SteadyStateSpring extends MassDampingSpring {
    private double maxRecordedEnergy = 0.0;
    private double progress = 0.0, lastProgress = 0.0;
    private static final double ENERGY_THRESHOLD = 1e-6;
    private static double shootSteady = 1;

    public SteadyStateSpring(String mass, String stiffness, String dampingForward, String dampingBackward) {
        super(mass, stiffness, dampingForward, dampingBackward);
    }

    @Override
    public void applyImpulse(double force) {
        super.applyImpulse(force);
        double currentEnergy = computeTotalEnergy();
        maxRecordedEnergy = Math.max(maxRecordedEnergy, currentEnergy);
        shootSteady = Math.min(1, progress + Math.abs(lastProgress - progress) * 0.5);
        System.out.println(getSteady());
        lastProgress = progress;
    }

    @Override
    public void update() {
        super.update();
        double currentEnergy = computeTotalEnergy();
        if (maxRecordedEnergy > 0) {
            progress = 1.0 - Math.min(1.0, currentEnergy / maxRecordedEnergy);
        } else {
            progress = 1.0;
            shootSteady = 1;
        }
        if (currentEnergy < ENERGY_THRESHOLD) {
            progress = 1.0;
            maxRecordedEnergy = 0;
            shootSteady = 1;
        }
    }

    @Override
    public String genJavaNewCode() {
        return super.genJavaNewCode().replace("MassDampingSpring", "SteadyStateSpring");
    }

    @Override
    public Object copy() {
        return new SteadyStateSpring(mass.strVal(), stiffness.strVal(), dampingForward.strVal(), dampingBackward.strVal()).setName(name);
    }

    private double computeTotalEnergy() {
        double kineticEnergy = 0.5 * mass.val() * velocity * velocity;
        double potentialEnergy = 0.5 * stiffness.val() * position * position;
        return kineticEnergy + potentialEnergy;
    }

    public double getSteady() {
        return shootSteady;
    }
}