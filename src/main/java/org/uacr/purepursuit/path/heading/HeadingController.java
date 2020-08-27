package org.uacr.purepursuit.path.heading;

import org.uacr.purepursuit.math.Pose2d;

public abstract class HeadingController {

    public abstract Integer getHeading(Pose2d currentPosition);
}
