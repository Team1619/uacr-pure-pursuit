package purepursuit.path.segment;

import purepursuit.PathUtil;
import purepursuit.math.*;
import purepursuit.math.Vector;

import java.util.*;

public class LineSegment extends Segment {

    private final List<Line> fLines;

    private int mCurrentLineIndex;
    private boolean mIsDone;

    public LineSegment(List<Point> points) {
        fLines = new ArrayList<>();

        for (int p = 0; p < points.size() - 1; p++) {
            fLines.add(new Line(points.get(p), points.get(p + 1)));
        }

        mCurrentLineIndex = 0;
        mIsDone = false;
    }

    public LineSegment(Point... points) {
        this(Arrays.asList(points));
    }

    public Point getLookaheadPoint(Pose2d currentPosition) {
        if (mCurrentLineIndex > fLines.size()) {
            return null;
        }

        Point lookaheadPoint = null;

        while (mCurrentLineIndex < fLines.size()) {
            lookaheadPoint = getCorrectIntersection(currentPosition, fLines.get(mCurrentLineIndex));
            if (lookaheadPoint != null) {
                break;
            }
            mCurrentLineIndex++;
        }

        if (lookaheadPoint == null) {
            lookaheadPoint = fLines.get(fLines.size() - 1).terminal();
        }

        return lookaheadPoint;
    }

    @Override
    public double getDistance(Pose2d currentPosition) {
        Point lookaheadPoint = getLookaheadPoint(currentPosition);

        double distance = 0.0;

        for(int l = 0; l < mCurrentLineIndex; l++) {
            distance += fLines.get(0).length();
        }

        return distance + fLines.get(mCurrentLineIndex).distanceFromInitial(lookaheadPoint);
    }

    private Point getCorrectIntersection(Pose2d currentPosition, Line line) {
        Circle lookaheadCircle = new Circle(currentPosition, getLookaheadDistance());

        List<Point> intersections = lookaheadCircle.getIntersections(line);

        if (intersections.isEmpty()) {
            Point closestPoint = line.closestPoint(currentPosition);

            if (line.isInSegment(closestPoint)) {
                return closestPoint;
            }
            return null;
        }

        if (intersections.size() <= 1) {
            Point intersection = intersections.get(0);

            if (line.isInSegment(intersection)) {
                return intersection;
            }

            return null;
        }

        for (Point intersection : intersections) {
            if (new Vector(line.closestPoint(intersection).subtract(line.initial())).angle() == line.delta().angle()) {
                if (line.isInSegment(intersection)) {
                    return intersection;
                } else {
                    return null;
                }
            }
        }

        return null;
    }

    public double length() {
        return fLines.stream().mapToDouble(Line::length).sum();
    }

    @Override
    public boolean isDone(Pose2d currentPosition) {
        return mIsDone;
    }

    @Override
    public double getInitialAngle() {
        return fLines.get(0).delta().angle();
    }

    @Override
    public double getFinalAngle() {
        return fLines.get(fLines.size() - 1).delta().angle();
    }

    @Override
    public Map<Double, Double> getSpeedReductions() {
        Map<Double, Double> speedReductions = new HashMap<>();

        double distance = 0.0;

        for (int p = 0; p < fLines.size() - 1; p++) {
            distance += fLines.get(p).length();

            double speedReduction = Math.abs(PathUtil.angleWrap(fLines.get(p + 1).delta().angle() - fLines.get(p).delta().angle())) / 90;

            if(0 < speedReduction && speedReduction <=1) {
                speedReductions.put(distance, speedReduction);
            }
        }

        return speedReductions;
    }
}
