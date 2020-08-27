package purepursuit.path.segment;

import purepursuit.math.Point;
import purepursuit.math.Pose2d;

import java.util.Map;

public abstract class Segment {

    private double mLookaheadDistance;

    public Segment() {
        mLookaheadDistance = 15;
    }

    public void setLookaheadDistance(double lookaheadDistance) {
        mLookaheadDistance = lookaheadDistance;
    }

    public double getLookaheadDistance() {
        return mLookaheadDistance;
    }

    public abstract double length();

    public abstract Point getLookaheadPoint(Pose2d currentPosition);

    public abstract double getDistance(Pose2d currentPosition);

    public abstract boolean isDone(Pose2d currentPosition);

    public abstract double getInitialAngle();

    public abstract double getFinalAngle();

    public abstract Map<Double, Double> getSpeedReductions();
}
