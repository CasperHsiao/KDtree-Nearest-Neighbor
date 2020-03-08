package pointsets;

import java.util.ArrayList;
import java.util.List;

/**
 * Naive nearest-neighbor implementation using a linear scan.
 */
public class NaivePointSet<T extends Point> implements PointSet<T> {
    private List<T> pointSet;
    private int size;

    /**
     * Instantiates a new NaivePointSet with the given points.
     * @param points a non-null, non-empty list of points to include
     *               Assumes that the list will not be used externally afterwards (and thus may
     *               directly store and mutate the array).
     */
    public NaivePointSet(List<T> points) {
        pointSet = new ArrayList<T>(points);
        size = this.pointSet.size();
    }

    /**
     * Returns the point in this set closest to the given point in O(N) time, where N is the number
     * of points in this set.
     */
    @Override
    public T nearest(Point target) {
        double result = this.pointSet.get(0).distanceSquaredTo(target);
        int resultIndex = 0;
        for (int i = 1; i < this.size; i++) {
            double distanceSquared = this.pointSet.get(i).distanceSquaredTo(target);
            if (distanceSquared < result) {
                result = distanceSquared;
                resultIndex = i;
            }
        }
        return this.pointSet.get(resultIndex);
    }

    @Override
    public List<T> allPoints() {
        return this.pointSet;
    }
}
