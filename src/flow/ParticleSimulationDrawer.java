package flow;

import flow.particle.Particle;
import processing.core.PGraphics;
import render.AbstractDrawer;
import util.geometry.Rectangle;

public class ParticleSimulationDrawer extends AbstractDrawer {
    public interface ParticleDrawer {
        void draw(PGraphics canvas, Particle particle, long particleLifeTime);
    }

    private final ParticleSimulation particleSimulation;
    private final ParticleDrawer particleDrawer;

    public ParticleSimulationDrawer(ParticleSimulation particleSimulation, ParticleDrawer particleDrawer, Rectangle bounds) {
        super(bounds);
        this.particleSimulation = particleSimulation;
        this.particleDrawer = particleDrawer;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        for(int i = 0; i < particleSimulation.getNumberOfParticles(); i++) {
            Particle p = particleSimulation.getParticle(i);
            long lifeTime = particleSimulation.getParticleLifeTime(i);

            particleDrawer.draw(canvas, p, lifeTime);
        }

        return canvas;
    }
}
