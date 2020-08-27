package purepursuit.path;

import purepursuit.PathUtil;
import purepursuit.math.Point;
import purepursuit.math.Pose2d;
import purepursuit.path.segment.Segment;
import purepursuit.path.velocity.TrapezoidVelocityProfile;
import purepursuit.path.velocity.VelocityProfile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Path {

    public static Path createLinePath() {
        return new Path();
    }

    public static Path createPointPath() {
        return new Path();
    }

    public static Path createCompoundPath(List<Segment> segments, PathConstrains constrains) {
        return new Path(segments, constrains);
    }

    public static Path createCompoundPath(List<Segment> segments) {
        return new Path(segments);
    }

    private final List<Segment> fSegments;
    private final VelocityProfile fProfile;

    private int mSegmentIndex;

    private Path(List<Segment> segments, PathConstrains constrains) {
        fSegments = segments;
        mSegmentIndex = 0;

        fProfile = new TrapezoidVelocityProfile(constrains, length(), getSpeedReductions());
    }

    private Path(List<Segment> segments) {
        this(segments, new PathConstrains());
    }

    private Path(Segment... segments) {
        this(Arrays.asList(segments));
    }

    public Point getLookaheadPoint(Pose2d currentPosition) {
        return updateCurrentSegment(currentPosition).getLookaheadPoint(currentPosition);
    }

    public double getVelocity(Pose2d currentPosition) {
        return fProfile.getVelocity(updateCurrentSegment(currentPosition).getDistance(currentPosition));
    }

    private Segment updateCurrentSegment(Pose2d currentPosition) {
        Segment currentSegment = fSegments.get(mSegmentIndex);

        if (mSegmentIndex != fSegments.size() - 1 && currentSegment.isDone(currentPosition)) {
            mSegmentIndex++;
            currentSegment = fSegments.get(mSegmentIndex);
        }

        return currentSegment;
    }

    public Map<Double, Double> getSpeedReductions() {
        Map<Double, Double> speedReductions = new HashMap<>();

        double distance = 0.0;

        for(int s = 0; s < fSegments.size(); s++) {
            Segment segment = fSegments.get(s);

            for(Map.Entry<Double, Double> segmentSpeedReduction : segment.getSpeedReductions().entrySet()) {
                speedReductions.put(segmentSpeedReduction.getKey() + distance, segmentSpeedReduction.getValue());
            }

            distance += segment.length();

            if(s < fSegments.size() - 1) {
                double speedReduction = Math.abs(PathUtil.angleWrap(fSegments.get(s + 1).getInitialAngle() - segment.getFinalAngle())) / 90;

                if (0 < speedReduction && speedReduction <= 1) {
                    speedReductions.put(distance, speedReduction);
                }
            }
        }

        return speedReductions;
    }

    public double length() {
        return fSegments.stream().mapToDouble(Segment::length).sum();
    }
}
