package flow;

import flow.particle.Particle;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ParticleSimulation {
    private static class ParticleData {
        final Particle p;
        final long spawnTime;

        private ParticleData(Particle p, long spawnTime) {
            this.p = p;
            this.spawnTime = spawnTime;
        }
    }

    private final FlowField flowField;
    private final int numberOfParticles;
    private final long lifeTime;
    private final double friction;
    private final double randomForce;

    private final Supplier<Particle> particleSupplier;
    private List<ParticleData> particles;

    public ParticleSimulation(FlowField flowField, Supplier<Particle> particleSupplier, int numberOfParticles, long lifeTime, double friction, double randomForce) {
        this.flowField = flowField;
        this.numberOfParticles = numberOfParticles;
        this.lifeTime = lifeTime;
        this.friction = friction;
        this.randomForce = randomForce;

        this.particleSupplier = particleSupplier;
        particles = new ArrayList<>(numberOfParticles);

        for(int i = 0; i < numberOfParticles; i++) {
            particles.add(createParticle());
        }
    }

    private ParticleData createParticle() {
        Particle p;
        do {
            p = particleSupplier.get();
        } while(!flowField.isInside(p.getPos()));

        p.setVel(flowField.get(p.getPos()));

        long spawnTime = System.currentTimeMillis();

        return new ParticleData(p, spawnTime);
    }

    public void update() {
        particles = particles.parallelStream()
                .map(this::update)
                .collect(Collectors.toList());
    }

    private ParticleData update(ParticleData pd) {
        Particle p = pd.p;
        long spawnTime = pd.spawnTime;

        if(!flowField.isInside(p.getPos())
                || (lifeTime != -1 && System.currentTimeMillis() - spawnTime >= lifeTime)
                || p.getVel().isZero()) {
            return createParticle();
        }

        Vector random = Vector.randomWithLength(MathUtils.random(randomForce));

        p.addForce(flowField.get(p.getPos()));
        p.addForce(random);
        p.applyFriction(friction);
        p.update();

        return pd;
    }


    public int getNumberOfParticles() {
        return numberOfParticles;
    }

    public Particle getParticle(int index) {
        return particles.get(index).p;
    }

    public long getParticleLifeTime(int index) {
        return System.currentTimeMillis() - particles.get(index).spawnTime;
    }

    public FlowField getFlowField() {
        return flowField;
    }
}
