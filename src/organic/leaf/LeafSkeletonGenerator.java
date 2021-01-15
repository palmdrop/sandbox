package organic.leaf;

import organic.Component;
import organic.structure.branch.Branch;
import organic.structure.branch.Branches;
import organic.structure.branch.SimpleBranch;
import util.math.MathUtils;

public class LeafSkeletonGenerator {
    public Branch<Component> generate(double branchLength, int baseSegments) {
        Branch<Component> root = new SimpleBranch<>(-Math.PI/2, branchLength);

        generateBase(root, branchLength, baseSegments);
        generateBranches(root, branchLength, 0);

        return root;
    }

    private void generateBranches(Branch<Component> current, double branchLength, int iterator) {
        boolean isLast = false;
        if(current.children().isEmpty()) {
            isLast = true;
        }

        if(iterator % 6 == 0) {
            Branch<Component> left = new SimpleBranch<>(-Math.PI/3, branchLength);
            Branch<Component> right = new SimpleBranch<>(Math.PI/3, branchLength);

            growBranch(left, 0.02, branchLength, 20 - iterator/2);
            growBranch(right, -0.02, branchLength, 20 - iterator/2);

            current.children().add(left);
            current.children().add(right);
        }

        if(!isLast) {
            Branch<Component> next = current.children().get(0);
            generateBranches(next, branchLength, iterator + 1);
        }
    }

    private void growBranch(Branch<Component> branchRoot, double angle, double length, int steps) {
        Branches.curve(branchRoot, angle, 0.0, steps, length);
    }

    private Branch<Component> generateBase(Branch<Component> root, double branchLength, int baseSegments) {
        return Branches.curve(root, 0.03, -0.002, baseSegments, branchLength);
    }


}
