package frc.robot.subsystems.superstructure.constants;

import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.signals.GravityTypeValue;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import frc.robot.subsystems.superstructure.pivot.PivotConstraints;

public class PivotConstants {
  public static final int canId = 5;

  public static final DCMotor gearbox = DCMotor.getFalcon500Foc(1);
  public static final double reduction = 25.0 * (60.0 / 24.0);
  public static final double moi = 1.0;

  public static final boolean invert = true;
  public static final double currentLimitAmps = 30;

  public static final boolean isBrakeMode = true;

  public static final PivotConstraints constraints = new PivotConstraints(-0.25, 0.25);

  public static final Rotation2d intialPosition = Rotation2d.fromDegrees(-90);

  // https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-vertical-arm.html#combined-feedforward-and-feedback-control
  public static final Slot0Configs gains =
      new Slot0Configs()
          // feedforward
          .withKG(0.3)
          .withGravityType(GravityTypeValue.Arm_Cosine)
          .withKS(0.0)
          .withKV(1.1)
          // feedback
          .withKP(18.0)
          .withKI(0.0)
          .withKD(0.0);

  public static final MotionMagicConfigs mmConfig =
      new MotionMagicConfigs()
          .withMotionMagicCruiseVelocity(2.5 * reduction)
          .withMotionMagicAcceleration(10 * reduction)
          .withMotionMagicJerk(1000 * reduction);

  public static final boolean foc = true;
}
