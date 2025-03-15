package sheridan.gcaa.client.animation.recoilAnimation;

public class SteadyStateSpring extends MassDampingSpring {
    private double maxRecordedEnergy = 0.0;
    private double progress = 0.0;
    private static final double ENERGY_THRESHOLD = 1e-6;
    private double lastStableProgress = 0.0;

    public SteadyStateSpring(String mass, String stiffness, String dampingForward, String dampingBackward) {
        super(mass, stiffness, dampingForward, dampingBackward);
    }

    @Override
    public void applyImpulse(double force) {
        super.applyImpulse(force);
        double currentEnergy = computeTotalEnergy();
        maxRecordedEnergy = Math.max(maxRecordedEnergy, currentEnergy);
        //System.out.println(progress + " " + (lastStableProgress - progress));
        lastStableProgress = progress;
    }

    @Override
    public void update() {
        super.update();
        updateSable();
    }

    private void updateSable() {
        double currentEnergy = computeTotalEnergy();

        if (maxRecordedEnergy > 0) {
            progress = 1.0 - Math.min(1.0, currentEnergy / (maxRecordedEnergy));
        } else {
            progress = 1.0;
            lastStableProgress = 0;
        }
        if (currentEnergy < ENERGY_THRESHOLD) {
            progress = 1.0;
            maxRecordedEnergy = 0;
            lastStableProgress = 0;
        }
    }

    private double computeTotalEnergy() {
        double kineticEnergy = 0.5 * mass.val() * velocity * velocity;
        double potentialEnergy = 0.5 * stiffness.val() * position * position;
        return kineticEnergy + potentialEnergy;
    }

    public double getProgress() {
        return progress;
    }
}