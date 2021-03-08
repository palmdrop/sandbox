package flow.particle;

import util.vector.ReadVector;
import util.vector.Vector;

public abstract class Entity {
    protected final Vector pos;
    protected final Vector vel;
    protected final Vector acc;

    protected double maxForce;
    protected double maxVel;

    private final static int DEFAULT_MAX_VALUE = -1;

    //Init
    public Entity() {
        this(new Vector(), new Vector(), new Vector(), DEFAULT_MAX_VALUE, DEFAULT_MAX_VALUE);
    }

    public Entity(final ReadVector pos) {
        this(pos, new Vector(), new Vector(), DEFAULT_MAX_VALUE, DEFAULT_MAX_VALUE);
    }

    public Entity(final ReadVector pos, final ReadVector vel) {
        this(pos, vel, new Vector(), DEFAULT_MAX_VALUE, DEFAULT_MAX_VALUE);
    }

    public Entity(final ReadVector pos, final ReadVector vel, final ReadVector acc) {
        this(pos, vel, acc, DEFAULT_MAX_VALUE, DEFAULT_MAX_VALUE);
    }

    public Entity(final ReadVector pos, final ReadVector vel, final ReadVector acc, double maxForce) {
        this(pos, vel, acc, maxForce, DEFAULT_MAX_VALUE);
    }

    public Entity(final ReadVector pos, final ReadVector vel, final ReadVector acc, double maxForce, double maxVel) {
        this.pos = new Vector(pos);
        this.vel = new Vector(vel);
        this.acc = new Vector(acc);
        this.maxForce = maxForce;
        this.maxVel = maxVel;
    }

    //Functionality
    public void update() { update(1); }
    public void update(double timeStep) {
        updateA(timeStep);
        updateB(timeStep);
    }

    public abstract void updateA(double timeStep);
    public abstract void updateB(double timeStep);

    public abstract void addForce(final ReadVector force);

    public void move(final ReadVector v) {
        pos.add(v);
    }
    public void offsetVelocity(final ReadVector v) { vel.add(v); }

    //Getters and setters
    public ReadVector getPos() { return pos; }
    public ReadVector getVel() { return vel; }
    public ReadVector getAcc() { return acc; }

    public double getX() { return getPos().getX(); }
    public double getY() { return getPos().getY(); }
    public double getZ() { return getPos().getZ(); }

    public double getMaxForce() { return maxForce; }
    public double getMaxVel()   { return maxVel; }

    public void setPos(final ReadVector pos) { this.pos.set(pos); }
    public void setPos(double x, double y) {
        pos.set(0, x);
        pos.set(1, y);
    }
    public void setVel(final ReadVector vel) { this.vel.set(vel); }
    public void setVel(double x, double y) {
        vel.set(0, x);
        vel.set(1, y);
    }
    public void setAcc(final ReadVector acc) { this.acc.set(acc); }
    public void setAcc(double x, double y) {
        acc.set(0, x);
        acc.set(1, y);
    }

    public void setX(double x) { this.pos.setX(x); }
    public void setY(double y) { this.pos.setY(y); }
    public void setZ(double z) { this.pos.setZ(z); }

    public void setMaxForce(double maxForce) { this.maxForce = maxForce; }
    public void setMaxVel(double maxVel)     { this.maxVel = maxVel; }
}
