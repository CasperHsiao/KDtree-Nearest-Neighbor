package pointsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fast nearest-neighbor implementation using a k-d tree.
 */
public class KDTreePointSet<T extends Point> implements PointSet<T> {
    private Node root;
    private List<T> pointSet;

    /**
     * Instantiates a new KDTreePointSet with a shuffled version of the given points.
     *
     * Randomizing the point order decreases likeliness of ending up with a spindly tree if the
     * points are sorted somehow.
     *
     * @param points a non-null, non-empty list of points to include.
     *               Assumes that the list will not be used externally afterwards (and thus may
     *               directly store and mutate the array).
     */
    public static <T extends Point> KDTreePointSet<T> createAfterShuffling(List<T> points) {
        Collections.shuffle(points);
        return new KDTreePointSet<T>(points);
    }

    /**
     * Instantiates a new KDTreePointSet with the given points.
     *
     * @param points a non-null, non-empty list of points to include.
     *               Assumes that the list will not be used externally afterwards (and thus may
     *               directly store and mutate the array).
     */
    KDTreePointSet(List<T> points) {
        this.root = new Node(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            addRecursive(this.root, points.get(i), 'x');
        }
        this.pointSet = new ArrayList<T>(points);
    }

    private Node addRecursive(Node current, T value, char level) {
        if (current == null) {
            return new Node(value);
        }
        if (level == 'x') {
            double xValue = value.x();
            double xCurrent = current.value.x();
            if (xValue < xCurrent) {
                current.left = addRecursive(current.left, value, 'y');
            } else {
                current.right = addRecursive(current.right, value, 'y');
            }
        } else {
            double yValue = value.y();
            double yCurrent = current.value.y();
            if (yValue < yCurrent) {
                current.left = addRecursive(current.left, value, 'x');
            } else {
                current.right = addRecursive(current.right, value, 'x');
            }
        }
        return current;
    }

    /**
     * Returns the point in this set closest to the given point in (usually) O(log N) time, where
     * N is the number of points in this set.
     * @return
     */
    @Override
    public T nearest(Point target) {
        Node resultNode = nearestHF(this.root, target, this.root, 'x');
        return resultNode.value;
    }

    private Node nearestHF(Node current, Point target, Node best, char level) {
        if (current == null) {
            return best;
        }
        double currentDistance = current.value.distanceSquaredTo(target);
        double bestDistance = best.value.distanceSquaredTo(target);
        if (currentDistance < bestDistance) {
            best = current;
            bestDistance = currentDistance;
        }
        double xTarget = target.x();
        double xCurrent = current.value.x();
        double yTarget = target.y();
        double yCurrent = current.value.y();
        boolean goBad = false;
        Node goodChoice = current.right;
        Node badChoice = current.left;
        if (level == 'x') {
            if (xTarget < xCurrent) {
                goodChoice = current.left;
                badChoice = current.right;
            }
            if ((xTarget - xCurrent)*(xTarget - xCurrent) < bestDistance) {
                goBad = true;
            }
            level = 'y';
        } else {
            if (yTarget < yCurrent) {
                goodChoice = current.left;
                badChoice = current.right;
            }
            if ((yTarget - yCurrent)*(yTarget - yCurrent) < bestDistance) {
                goBad = true;
            }
            level = 'x';
        }
        best = nearestHF(goodChoice, target, best, level);

        if (goBad) {
            best = nearestHF(badChoice, target, best, level);
        }
        return best;
    }

    @Override
    public List<T> allPoints() {
        return this.pointSet;
    }

    private class Node {
        T value;
        Node left;
        Node right;

        Node(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }
}


