package org.uacr.purepursuit.path.segment;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;
import org.uacr.purepursuit.math.Vector;
import org.uacr.purepursuit.path.point.PathPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PointSegment extends Segment {

    /**
     * Distance between each point (inches)
     */
    private double mPointSpacing = 1;

    /**
     * The amount of smoothing to be done on the path (larger number = more smoothing)
     */
    private double mPathSmoothing = 0.5;

    /**
     * Waypoints along path specified by behavior
     */
    private List<Point> mPoints;

    /**
     * All the points along the path, created from the waypoints (fPoints)
     */
    @Nullable
    private List<PathPoint> mPath;

    /**
     * Pass in an ArrayList of waypoints
     */
    public PointSegment(List<Point> points) {
        mPoints = points;
    }

    /**
     * Pass in a comma separated list or array of waypoints
     */
    public PointSegment(Point... points) {
        this(new ArrayList<>(Arrays.asList(points)));
    }

    /**
     * Getters and Setters for path specific creation and following data
     */

    public double getPointSpacing() {
        return mPointSpacing;
    }

    public void setPointSpacing(double pointSpacing) {
        mPointSpacing = pointSpacing;
    }

    public double getPathSmoothing() {
        return mPathSmoothing;
    }

    public void setPathSmoothing(double pathSmoothing) {
        mPathSmoothing = pathSmoothing;
    }

    /**
     * Returns a single PathPoint from fPath
     *
     * @param index the index of the PathPoint
     * @return a PathPoint
     */
    public PathPoint getPoint(int index) {
        return getPathPoint(index);
    }

    /**
     * Returns all points in path (fPath).
     *
     * @return a PathPoint ArrayList
     */
    public List<PathPoint> getPoints() {
        if (mPath != null) {
            return new ArrayList<>(mPath);
        }
        build();
        return getPoints();
    }

    /**
     * Returns a single PathPoint from fPath
     *
     * @param point the index of the PathPoint
     * @return a PathPoint
     */
    private PathPoint getPathPoint(int point) {
        if (mPath != null) {
            return mPath.get(point);
        }
        build();
        return getPathPoint(point);
    }

    /**
     * Returns all points in path (fPath).
     *
     * @return a PathPoint ArrayList
     */
    private List<PathPoint> getPath() {
        if (mPath != null) {
            return mPath;
        }
        build();
        return getPath();
    }

    /**
     * Turns all the waypoints (fPoints) into a path (fPath).
     */
    public void build() {

        if (mPath != null) {
            return;
        }

        if (mPoints.size() == 0) {
            mPath = new ArrayList<>();
            return;
        }

        fill();

        smooth();
    }

    /**
     * Fills the spaces between waypoints (fPoints) with a point fPointSpacing inches.
     */
    private void fill() {
        ArrayList<Point> newPoints = new ArrayList<>();

        for (int s = 1; s < mPoints.size(); s++) {
            Vector vector = new Vector(mPoints.get(s - 1), mPoints.get(s));

            int numPointsFit = (int) Math.ceil(vector.magnitude() / mPointSpacing);

            vector = vector.normalize().scale(mPointSpacing);

            for (int i = 0; i < numPointsFit; i++) {
                newPoints.add(mPoints.get(s - 1).add(vector.scale(i)));
            }
        }

        newPoints.add(mPoints.get(mPoints.size() - 1));

        mPoints = newPoints;
    }

    /**
     * Smooths the straight lines of points into a curved path.
     */
    private void smooth() {
        double change = 0.5;
        double changedPoints = 1;
        while (change / changedPoints >= 0.01) {
            change = 0;
            changedPoints = 0;

            List<Point> newPoints = new ArrayList<>(mPoints);

            for (int i = 1; i < mPoints.size() - 1; i++) {
                Point point = mPoints.get(i);

                Vector middle = new Vector(mPoints.get(i + 1).subtract(mPoints.get(i - 1)));

                middle = new Vector(mPoints.get(i - 1).add(middle.normalize().scale(middle.magnitude() / 2)));

                Vector delta = new Vector(middle.subtract(point));

                Point newPoint = point.add(delta.normalize().scale(delta.magnitude() * mPathSmoothing));

                if (!Double.isNaN(newPoint.getX()) && !Double.isNaN(newPoint.getY())) {
                    newPoints.set(i, newPoint);
                    change += point.distance(newPoint);
                    changedPoints++;
                }
            }

            mPoints = newPoints;
        }
    }

    @Override
    public double length() {
        return 0;
    }

    @Override
    public Point getLookaheadPoint(Pose2d currentPosition) {
        return null;
    }

    @Override
    public double getDistance(Pose2d currentPosition) {
        return 0;
    }

    @Override
    public boolean isDone(Pose2d currentPosition) {
        return false;
    }

    @Override
    public double getInitialAngle() {
        return 0;
    }

    @Override
    public double getFinalAngle() {
        return 0;
    }

    @Override
    public Map<Double, Double> getSpeedReductions() {
        return null;
    }
}
