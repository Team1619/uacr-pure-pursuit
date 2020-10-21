package org.uacr.purepursuit.path;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.path.segment.LineSegment;
import org.uacr.purepursuit.path.segment.PointSegment;
import org.uacr.purepursuit.path.segment.Segment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathBuilder {

    public static PathBuilder start(Point point, PathConstrains constrains) {
        return new PathBuilder(point, constrains);
    }

    public static PathBuilder start(Point point) {
        return start(point, new PathConstrains());
    }

    public static PathBuilder start(double x, double y, PathConstrains constrains) {
        return start(new Point(x, y), constrains);
    }

    public static PathBuilder start(double x, double y) {
        return start(new Point(x, y));
    }

    private final PathConstrains fDefaultConstraints;
    private final List<Segment> fSegments;

    private SegmentBuildMode mCurrentSegmentBuildMode;
    private Segment mCurrentSegment;
    private List<Point> mCurrentPoints;
    private Point mCurrentPoint;

    private PathBuilder(Point point, PathConstrains constrains) {
        fDefaultConstraints = constrains;
        fSegments = new ArrayList<>();

        mCurrentSegmentBuildMode = SegmentBuildMode.NONE;
        mCurrentSegment = null;
        mCurrentPoints = new ArrayList<>();
        mCurrentPoint = point;
        mCurrentPoints.add(mCurrentPoint);
    }

    public PathBuilder lineTo(Point point) {
        if(mCurrentSegmentBuildMode != SegmentBuildMode.LINE) {
            createSegment();
        }

        mCurrentSegmentBuildMode = SegmentBuildMode.LINE;
        mCurrentPoint = point;
        mCurrentPoints.add(mCurrentPoint);
        return this;
    }

    public PathBuilder lineTo(double x, double y) {
        return lineTo(new Point(x, y));
    }

    public PathBuilder pointsTo(List<Point> points) {
        createSegment();

        mCurrentSegmentBuildMode = SegmentBuildMode.POINT;
        mCurrentPoint = points.get(points.size() - 1);
        return this;
    }

    public PathBuilder pointsTo(Point... points) {
        return pointsTo(Arrays.asList(points));
    }

    private void createSegment() {
        if(mCurrentSegmentBuildMode == SegmentBuildMode.NONE || mCurrentPoints.size() < 1) {
            return;
        }

        switch (mCurrentSegmentBuildMode) {
            case LINE:
                fSegments.add(new LineSegment(mCurrentPoints));
                break;
            case POINT:
                fSegments.add(new PointSegment(mCurrentPoints));
                break;
        }

        mCurrentPoints.clear();
    }

    public Path build() {
        createSegment();

        return Path.createCompoundPath(fSegments, fDefaultConstraints);
    }

    private enum SegmentBuildMode {
        NONE,
        LINE,
        POINT
    }
}
