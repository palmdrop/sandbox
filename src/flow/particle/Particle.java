package flow.particle;

import util.vector.ReadVector;
import util.vector.Vector;

public class Particle extends Entity {
    private static final int DEFAULT_RADIUS = -1;
    private static final int DEFAULT_MASS = -1;

    private double radius;
    private double mass;

    private double elasticity;

    //Init
    public Particle() {
        this(new Vector());
    }

    public Particle(final ReadVector pos) {
        this(pos, new Vector());
    }

    public Particle(final ReadVector pos, final Vector vel) {
        super(pos, vel);
        init(DEFAULT_RADIUS, DEFAULT_MASS);
    }

    public Particle(final ReadVector pos, final ReadVector vel, double radius) {
        super(pos, vel);
        init(radius);
    }

    public Particle(final ReadVector pos, final ReadVector vel, double radius, double mass) {
        super(pos, vel);
        init(radius, mass);
    }

    private void init(double radius) {
        init(radius, radius * radius * Math.PI);
    }

    private void init(double radius, double mass) {
        this.radius = radius;
        this.mass = mass;
        elasticity = 1;
    }

    //Functionality
    public static double collision(final Particle a, final Particle b, final Vector outAtoB) { //returns the distance squared if collision, otherwise -1
        if(a.equals(b)) return -1;

        //Preliminary calculations
        outAtoB.set(Vector.sub(b.getPos(), a.getPos()));
        double distSq = outAtoB.lengthSq();
        double collisionDist = a.getRadius() + b.getRadius();

        //Check for collision
        if(distSq <= collisionDist * collisionDist) return distSq;

        return -1;
    }

    public static void elasticCollision(final Particle a, final Particle b) {
        elasticCollision(a, b, null);
    }

    public static void elasticCollision(final Particle a, final Particle b, final ReadVector gravity) { //TODO: dependent on the force on the flow.particle??????????????
        final Vector AtoB = new Vector();
        double distSq = collision(a, b, AtoB);
        if(distSq == -1) return;

        //Calculate distance and normal
        double dist = Math.sqrt(distSq);
        final Vector normal = AtoB.div(dist);

        elasticCollision(a, b, normal, dist);
    }

    public static void elasticCollision(final Particle a, final Particle b, final ReadVector normal, double dist) {
        if(dist > a.getRadius() + b.getRadius()) return;

        //Displace (handle overlap)
        double overlap = (a.getRadius() + b.getRadius() - dist);

        final Vector displacement = Vector.mult(normal, overlap/2);
        a.move(Vector.mult(displacement, -1));
        b.move(displacement);

        //Apply physics
        double elasticity = (a.getElasticity() + b.getElasticity()) / 2; //TODO: how to take different elasticity into account??

        final Vector vn = Vector.mult(normal, Vector.dot(Vector.sub(a.getVel(), b.getVel()), normal));
        double massFactor = (1.0 + elasticity) / (a.getMass() + b.getMass());

        a.offsetVelocity(Vector.mult(vn, -b.getMass() * massFactor));
        b.offsetVelocity(Vector.mult(vn,  a.getMass() * massFactor));
    }

    public void applyBorders(double width, double height) {
        applyBorders(0, width, 0, height);
    }

    public void applyBorders(double minX, double maxX, double minY, double maxY) {
        if(getX() <= getRadius() + minX) {
            setX(getRadius() + minX);

            setVel(new Vector(-getVel().getX() * elasticity, getVel().getY()));
        } else if(getX() >= maxX - getRadius() - 1) {
            setX(maxX - getRadius() - 1);
            setVel(new Vector(-getVel().getX() * elasticity, getVel().getY()));
        }

        if(getY() <= getRadius() + minY) {
            setY(getRadius() + minY);
            setVel(new Vector(getVel().getX(), -getVel().getY() * elasticity));
        } else if(getY() >= maxY - getRadius() - 1) {
            setY(maxY - getRadius() - 1);
            setVel(new Vector(getVel().getX(), -getVel().getY() * elasticity));
        }
    }

    public void wrapAround(double width, double height) {
        double x = getX();
        double y = getY();

        if(x < 0) x += width;
        else if(x >= width) x -= width;

        if(y < 0) y += height;
        else if(y >= height) y -= height;

        setPos(x, y);
    }


    //Running
    @Override
    public void updateA(double timeStep) {
        vel.add(Vector.mult(acc, timeStep/2));
        if(maxVel != -1) vel.limit(maxVel);
        pos.add(Vector.mult(vel, timeStep));

        acc.mult(0);
    }

    @Override
    public void updateB(double timeStep) {
        vel.add(Vector.mult(acc, timeStep/2));
    }

    @Override
    public void addForce(final ReadVector force) {
        if(maxForce != -1) acc.add(new Vector(force).limit(maxForce));
        else               acc.add(force);
    }

    public void applyFriction(double friction) {
        vel.mult(1.0 - friction);
    }

    //Getters and setters
    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }
    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getElasticity() { return elasticity; }
    public void setElasticity(double elasticity) { this.elasticity = elasticity; }
}
