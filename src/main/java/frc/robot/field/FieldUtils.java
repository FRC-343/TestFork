package frc.robot.field;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.bobot_state.BobotState;
import frc.robot.field.FieldConstants.AprilTagStruct;
import frc.robot.field.FieldConstants.ReefFace;
import frc.robot.subsystems.vision.VisionConstants;
import java.util.List;

public class FieldUtils {
  public static Alliance getAlliance() {
    return DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue);
  }

  public static boolean isBlueAlliance() {
    return FieldUtils.getAlliance() == Alliance.Blue;
  }

  public static boolean isRedAlliance() {
    return FieldUtils.getAlliance() == Alliance.Red;
  }

  public static double getFlipped() {
    return FieldUtils.isRedAlliance() ? -1 : 1;
  }

  public static Rotation2d getAngleOfTag17() {
    return VisionConstants.fieldLayout
        .getTagPose(17)
        .get()
        .getRotation()
        .toRotation2d()
        .plus(new Rotation2d(Math.PI));
  }

  public static ReefFace getClosestReef() {
    List<ReefFace> reefTags =
        FieldUtils.isBlueAlliance() ? FieldConstants.blueReefTags : FieldConstants.redReefTags;
    Translation2d robotTranslation = BobotState.getGlobalPose().getTranslation();

    ReefFace closestReef =
        reefTags.stream()
            .reduce(
                (ReefFace reef1, ReefFace reef2) ->
                    robotTranslation.getDistance(
                                reef1.tag().pose().getTranslation().toTranslation2d())
                            < robotTranslation.getDistance(
                                reef2.tag().pose().getTranslation().toTranslation2d())
                        ? reef1
                        : reef2)
            .orElse(null);

    return closestReef;
  }

  public static AprilTagStruct getClosestHPSTag() {
    List<AprilTagStruct> hpsTags =
        FieldUtils.isBlueAlliance() ? FieldConstants.blueHPSTags : FieldConstants.redHPSTags;

    Translation2d robotTranslation = BobotState.getGlobalPose().getTranslation();

    AprilTagStruct closestTag =
        hpsTags.stream()
            .reduce(
                (AprilTagStruct tag1, AprilTagStruct tag2) ->
                    robotTranslation.getDistance(tag1.pose().getTranslation().toTranslation2d())
                            < robotTranslation.getDistance(
                                tag2.pose().getTranslation().toTranslation2d())
                        ? tag1
                        : tag2)
            .orElse(null);

    return closestTag;
  }
}
