package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.bobot_state.BobotState;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.vision.VisionConstants;
import java.util.function.Supplier;

public class DriveRelativeToAprilTag {
  public Command driveToOffsetCommand(
      Drive drive,
      int tagId,
      Supplier<Double> xOffset,
      Supplier<Double> yOffset,
      Supplier<Rotation2d> thetaOffset) {
    PIDController xController = new PIDController(0.0, 0.0, 0.0);
    PIDController yController = new PIDController(0.0, 0.0, 0.0);
    PIDController thetaController = new PIDController(0.0, 0.0, 0.0);

    Transform2d offset = new Transform2d(xOffset.get(), yOffset.get(), thetaOffset.get());

    return Commands.run(
        () -> {
          Pose2d tagPose =
              VisionConstants.fieldLayout
                  .getTagPose(tagId)
                  .get()
                  .toPose2d(); // Need to change this to the relative position calc'ed by the camera

          Pose2d targetPose = tagPose.transformBy(offset);

          Transform2d error = BobotState.getGlobalPose().minus(targetPose);

          double xSpeed = xController.calculate(error.getX());
          double ySpeed = yController.calculate(error.getY());
          double angularSpeed = thetaController.calculate(error.getRotation().getRadians());

          ChassisSpeeds speeds =
              new ChassisSpeeds(
                  xSpeed * drive.getMaxLinearSpeedMetersPerSec(),
                  ySpeed * drive.getMaxLinearSpeedMetersPerSec(),
                  angularSpeed);
          boolean isFlipped =
              DriverStation.getAlliance().isPresent()
                  && DriverStation.getAlliance().get() == Alliance.Red;

          drive.runVelocity(
              ChassisSpeeds.fromFieldRelativeSpeeds(
                  speeds,
                  isFlipped
                      ? drive.getRotation().plus(new Rotation2d(Math.PI))
                      : drive.getRotation()));
        },
        drive);
  }

  public static Command drivePerpendicularToPoseCommand(
      Drive drive, Supplier<Pose2d> maybeTargetPose, Supplier<Double> perpendicularInput) {
    PIDController parallelController = new PIDController(5.0, 0.0, 0.0);
    PIDController thetaController = new PIDController(5.0, 0.0, 0.0);

    return Commands.run(
            () -> {
              Pose2d robotPose = BobotState.getGlobalPose();
              Pose2d targetPose = maybeTargetPose.get();

              Rotation2d desiredTheta = targetPose.getRotation().plus(Rotation2d.kPi);

              // https://en.wikipedia.org/wiki/Vector_projection#Scalar_projection
              Translation2d robotToTarget = robotPose.minus(targetPose).getTranslation();
              Rotation2d angleBetween = robotToTarget.getAngle();
              double parallelError = -robotToTarget.getNorm() * angleBetween.getSin();

              Rotation2d thetaError = robotPose.getRotation().minus(desiredTheta);

              double parallelSpeed = parallelController.calculate(parallelError);

              double angularSpeed = thetaController.calculate(thetaError.getRadians());

              // The error is here, need to fix
              ChassisSpeeds speeds =
                  ChassisSpeeds.fromRobotRelativeSpeeds(
                      perpendicularInput.get() * drive.getMaxLinearSpeedMetersPerSec(),
                      parallelSpeed,
                      angularSpeed,
                      desiredTheta);
              boolean isFlipped =
                  DriverStation.getAlliance().isPresent()
                      && DriverStation.getAlliance().get() == Alliance.Red;

              drive.runVelocity(
                  ChassisSpeeds.fromFieldRelativeSpeeds(
                      speeds,
                      isFlipped
                          ? drive.getRotation().plus(new Rotation2d(Math.PI))
                          : drive.getRotation()));
            })
        .andThen(
            () -> {
              parallelController.close();
              thetaController.close();
            });
  }
}
