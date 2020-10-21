package org.uacr.purepursuit.path.heading;

public class ConstantHeadingController extends HeadingController {

    private final double fAngle;

    public ConstantHeadingController(double angle) {
        fAngle = angle;
    }

    @Override
    public Double getHeading(double currentDistance) {
        return fAngle;
    }
}
