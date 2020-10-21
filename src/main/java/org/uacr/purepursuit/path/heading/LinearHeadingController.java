package org.uacr.purepursuit.path.heading;

import org.uacr.purepursuit.PathUtil;

public class LinearHeadingController extends HeadingController {

    private final double fLength;
    private final double fInitialHeading;
    private final double fFinalHeading;

    private LinearHeadingController(double length, double initialHeading, double finalHeading) {
        fLength = length;
        fInitialHeading = initialHeading;
        fFinalHeading = finalHeading;
    }

    @Override
    public Double getHeading(double currentDistance) {
        return PathUtil.interpolate(currentDistance, 0, fLength, fInitialHeading, fFinalHeading);
    }
}
