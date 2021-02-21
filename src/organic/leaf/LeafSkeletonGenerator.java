package organic.leaf;

import organic.Component;
import organic.structure.branch.Branch;
import organic.structure.branch.Branches;
import organic.structure.branch.SimpleBranch;

public class LeafSkeletonGenerator {
    private final double baseAngle;
    private final double baseIncrement;

    private final double branchLength;
    private final int baseSegments;
    private final int stepsPerBranch;
    private final int baseStepsPerBranch;

    private final double branchAngle;
    private final double branchAngleIncrement;
    private final double branchFalloff;

    public LeafSkeletonGenerator(double baseAngle, double baseIncrement, double branchLength, int baseSegments, int baseStepsPerBranch, int stepsPerBranch, double branchFalloff, double branchAngle, double branchAngleIncrement) {
        this.baseAngle = baseAngle;
        this.baseIncrement = baseIncrement;
        this.branchLength = branchLength;
        this.baseSegments = baseSegments;
        this.baseStepsPerBranch = baseStepsPerBranch;
        this.branchAngle = branchAngle;
        this.branchAngleIncrement = branchAngleIncrement;
        this.stepsPerBranch = stepsPerBranch;
        this.branchFalloff = branchFalloff;
    }


    private double currentAngle = -Math.PI/2;
    public Branch<Component> generate() {
        Branch<Component> root = new SimpleBranch<>(currentAngle, branchLength);

        generateBase(root, branchLength, baseSegments);
        generateBranches(root, branchLength, 0);

        return root;
    }


    private void generateBranches(Branch<Component> current, double branchLength, int iterator) {
        boolean isLast = false;
        if(current.children().isEmpty()) {
            isLast = true;
        }

        double angle = current.getAngle();
        if(iterator % baseStepsPerBranch == 0) {
            double angleDiff = currentAngle - angle;
            currentAngle = angle;

            Branch<Component> left = new SimpleBranch<>(-branchAngle + angleDiff * 100, branchLength);
            Branch<Component> right = new SimpleBranch<>(branchAngle - angleDiff * 100, branchLength);

            int branchSteps = (int)(stepsPerBranch * Math.pow(branchFalloff, iterator));
            growBranch(left, branchAngleIncrement, branchLength, branchSteps);
            growBranch(right, -branchAngleIncrement, branchLength, branchSteps);

            current.children().add(left);
            current.children().add(right);
        }
        currentAngle = angle;

        if(!isLast) {
            Branch<Component> next = current.children().get(0);
            generateBranches(next, branchLength, iterator + 1);
        }

    }

    private void growBranch(Branch<Component> branchRoot, double angle, double length, int steps) {
        Branches.curve(branchRoot, angle, 0.0, steps, length);
    }

    private Branch<Component> generateBase(Branch<Component> root, double branchLength, int baseSegments) {
        return Branches.curve(root, baseAngle, baseIncrement, baseSegments, branchLength);
    }
}
