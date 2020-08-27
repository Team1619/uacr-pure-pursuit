package purepursuit.path.velocity;

import purepursuit.PathUtil;
import purepursuit.math.Line;
import purepursuit.math.Point;
import purepursuit.math.Vector;
import purepursuit.path.PathConstrains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrapezoidVelocityProfile implements VelocityProfile {

    public final List<Line> fVelocityProfile;

    public TrapezoidVelocityProfile(PathConstrains constrains, double length, Map<Double, Double> speedReductions) {
        fVelocityProfile = new ArrayList<>();

        Line minVelocityLine = new Line(new Point(0, constrains.minVelocity), new Vector(length, 0));
        Line maxVelocityLine = new Line(new Point(0, constrains.maxVelocity), new Vector(length, 0));

        fVelocityProfile.add(new Line(minVelocityLine.initial(), new Line(minVelocityLine.initial(), minVelocityLine.initial().add(new Point(1, constrains.maxAcceleration))).intersection(maxVelocityLine)));

        for(double reductionDistance : speedReductions.keySet().stream().sorted().collect(Collectors.toList())) {
            Point speedReductionPoint = new Point(reductionDistance, PathUtil.interpolate(speedReductions.get(reductionDistance), 0, 1, constrains.maxVelocity, constrains.minVelocity));

            fVelocityProfile.add(new Line(new Line(speedReductionPoint, speedReductionPoint.add(new Point(-1, constrains.maxDeceleration))).intersection(maxVelocityLine), speedReductionPoint));
            fVelocityProfile.add(new Line(speedReductionPoint, new Line(speedReductionPoint, speedReductionPoint.add(new Point(1, constrains.maxAcceleration))).intersection(maxVelocityLine)));
        }

        fVelocityProfile.add(new Line(new Line(minVelocityLine.terminal(), minVelocityLine.terminal().add(new Point(-1, constrains.maxDeceleration))).intersection(maxVelocityLine), minVelocityLine.terminal()));

        for(int l = 0; l < fVelocityProfile.size() - 1; l++) {
            if(PathUtil.toleranceEquals(Math.abs(fVelocityProfile.get(l).slope()), 0, 0.00001)) {
                continue;
            }

            while (true) {
                Point nextLineIntersection = fVelocityProfile.get(l).intersection(fVelocityProfile.get(l + 1));

                if (nextLineIntersection.getY() > constrains.maxVelocity) {
                    fVelocityProfile.add(l + 1, new Line(fVelocityProfile.get(l).intersection(maxVelocityLine),
                            fVelocityProfile.get(l + 1).intersection(maxVelocityLine)));
                    break;
                } else if (fVelocityProfile.get(l).slope() > 0) {
                    if (fVelocityProfile.get(l + 1).isInSegment(nextLineIntersection)) {
                        fVelocityProfile.set(l, new Line(fVelocityProfile.get(l).initial(), nextLineIntersection));
                        fVelocityProfile.set(l + 1, new Line(nextLineIntersection, fVelocityProfile.get(l + 1).terminal()));
                        break;
                    }
                    fVelocityProfile.remove(l + 1);
                    fVelocityProfile.remove(l + 1);
                } else {
                    break;
                }
            }
        }
    }


    public double getVelocity(double distance) {
        for(Line line : fVelocityProfile) {
            if(line.isInDomain(distance)) {
                return line.evaluateX(distance).getY();
            }
        }

        return -1;
    }
}
