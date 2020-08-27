import purepursuit.math.Point;
import purepursuit.math.Pose2d;
import purepursuit.path.Path;
import purepursuit.path.PathBuilder;

public class Main {
    public static void main(String[] args) {
        Path path = PathBuilder.start(new Point(0, 0))
                .lineTo(50, 0)
                .lineTo(50, 50)
                .lineTo(100, 50)
                .lineTo(100, 0)
                .lineTo(150, 0)
                .build();

        System.out.println(path.getVelocity(new Pose2d(50, 20, 0)));
    }
}
