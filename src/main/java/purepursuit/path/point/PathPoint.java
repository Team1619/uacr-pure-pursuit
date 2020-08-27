package purepursuit.path.point;

import purepursuit.math.Point;
import purepursuit.path.heading.HeadingController;

public class PathPoint extends Point {

    public PathPoint(Point point, HeadingController headingController) {
        super(point.getX(), point.getY());
    }

    public PathPoint(double x, double y, HeadingController headingController) {
        this(new Point(x, y), headingController);
    }

    public PathPoint(Point point) {
        this(point, null);
    }

    public PathPoint(double x, double y) {
        this(x, y, null);
    }
}
