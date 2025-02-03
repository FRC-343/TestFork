package frc.robot.subsystems.quest;

import edu.wpi.first.math.geometry.Pose2d;
import org.littletonrobotics.junction.AutoLog;

public interface QuestIO extends AutoCloseable {
  @AutoLog
  public static class QuestIOInputs {
    public boolean connected = false;

    // These are with relative with offsets applied (probably what you want)
    public Pose2d questPose = new Pose2d();
    public Pose2d robotPose = new Pose2d();

    public Pose2d rawPose = new Pose2d();

    public double timestamp = 0;
    public double batteryLevel = 0;
  }

  public default void updateInputs(QuestIOInputs inputs) {}

  /** Sets supplied pose as origin of all calculations */
  public default void resetPose(Pose2d pose) {}

  /** Zeroes the absolute 3D position of the robot (similar to long-pressing the quest logo) */
  public default void zeroAbsolutePosition() {}

  @Override
  public default void close() {}
}
