package org.uacr.purepursuit.controller;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;
import org.uacr.purepursuit.math.Vector;
import org.uacr.purepursuit.path.Path;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import javax.annotation.Nullable;

public abstract class WestCoastPurePursuitController extends PurePursuitController {

    private static final Logger sLogger = LogManager.getLogger(WestCoastPurePursuitController.class);

    private final double fTrackWidth;

    @Nullable
    private Path mCurrentPath;
    private Pose2d mCurrentPose;
    private Pose2d mFollowPose;
    private FollowDirection mFollowDirection;
    private boolean mIsFollowing;
    private double mDeltaAngle;

    public WestCoastPurePursuitController(double trackWidth) {
        fTrackWidth = trackWidth;

        mCurrentPath = null;
        mCurrentPose = new Pose2d();
        mFollowPose = new Pose2d();
        mFollowDirection = FollowDirection.FORWARD;
        mIsFollowing = false;
        mDeltaAngle = 0;
    }

    public double getTrackWidth() {
        return fTrackWidth;
    }

    public void followPath(Path path) {
        mCurrentPath = path;
        mCurrentPath.reset();
        resetFollower();
        mIsFollowing = true;
    }

    public boolean isFollowing() {
        return mIsFollowing;
    }

    public boolean isPathFinished() {
        return !isFollowing();
    }

    public FollowDirection getFollowDirection() {
        return mFollowDirection;
    }

    public void setFollowDirection(FollowDirection followDirection) {
        mFollowDirection = followDirection;
    }

    public void resetFollower() {
        mCurrentPose = new Pose2d();
        mFollowPose = new Pose2d();
    }

    public void updateFollower() {
        if (mCurrentPath == null || !mIsFollowing) {
            stopDrive();

            return;
        }

        mCurrentPose = getCurrentPose();

        mFollowPose = mCurrentPose.clone();

        if (mFollowDirection == FollowDirection.REVERSE) {
            mFollowPose = new Pose2d(mFollowPose.getX(), mFollowPose.getY(), ((mFollowPose.getHeading() + 360) % 360) - 180);
        }

        // Uses the path object to calculate curvature and velocity values
        double velocity = mCurrentPath.getVelocity(mFollowPose);
        Point lookaheadPoint = mCurrentPath.getLookaheadPoint(mFollowPose);

        sLogger.info("Length: {} - Current Pose: {} - Lookahead: {} - Velocity: {}", mCurrentPath.length(), mFollowPose, lookaheadPoint, velocity);

        updateDriveVelocities(velocity, getCurvature(mCurrentPose, lookaheadPoint));

        if(mCurrentPath.isDone(mFollowPose)) {
            mIsFollowing = false;
        }
    }

    protected void updateDriveVelocities(double velocity, double curvature) {
        if (mFollowDirection == FollowDirection.REVERSE) {
            setDriveVelocities(-(velocity * ((1.5 - curvature * fTrackWidth) / 1.5)),
                    -(velocity * ((1.5 + curvature * fTrackWidth) / 1.5)));
        } else {
            setDriveVelocities(velocity * ((1.5 + curvature * fTrackWidth) / 1.5),
                    velocity * ((1.5 - curvature * fTrackWidth) / 1.5));
        }
    }

    protected double getCurvature(Pose2d currentPosition, Point point) {
        Vector delta = new Vector(point.subtract(currentPosition));

        double angle = Math.toDegrees(Math.atan2(delta.getY(), Math.abs(delta.getX()) > 0.3 ? delta.getX() : 0.3 * Math.signum(delta.getX())));

        mDeltaAngle = currentPosition.getHeading() - angle;

        if (Math.abs(mDeltaAngle) > 180) mDeltaAngle = -Math.signum(mDeltaAngle) * (360 - Math.abs(mDeltaAngle));

        double curvature = (Math.abs(mDeltaAngle) > 90 ? Math.signum(mDeltaAngle) : Math.sin(Math.toRadians(mDeltaAngle))) / (delta.magnitude() / 2);

        if (Double.isInfinite(curvature) || Double.isNaN(curvature)) return 0.0;

        return curvature;
    }

    public void stopDrive() {
        setDriveVelocities(0.0, 0.0);
    }

    public abstract void setDriveVelocities(double leftVelocity, double rightVelocity);

    public abstract Pose2d getCurrentPose();

    public enum FollowDirection {
        FORWARD,
        REVERSE
    }
}
