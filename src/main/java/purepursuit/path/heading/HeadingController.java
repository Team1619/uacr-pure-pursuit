package purepursuit.path.heading;

import purepursuit.math.Pose2d;

public abstract class HeadingController {

    public abstract Integer getHeading(Pose2d currentPosition);
}
