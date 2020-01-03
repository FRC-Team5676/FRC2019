/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.AnalogInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private DifferentialDrive m_myRobot;
  private Joystick m_Stick;
  WPI_TalonSRX LeftBackDrive = new WPI_TalonSRX(0);
  WPI_TalonSRX RightFrontDrive = new WPI_TalonSRX(3);
  VictorSP LeftFrontDrive = new VictorSP(9);
  VictorSP RightBackDrive = new VictorSP(6);
  SpeedControllerGroup m_right = new SpeedControllerGroup(LeftBackDrive, LeftFrontDrive);
  SpeedControllerGroup m_left = new SpeedControllerGroup(RightBackDrive, RightFrontDrive);

  boolean Use_US_Master;
  boolean Use_US_Front;
  boolean Use_US_LeftRight;
  double P_Max;
  double P_Min_Front;
  double P_Min_LeftRight;
  AnalogInput US_Input = new AnalogInput(0);

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_Stick = new Joystick(0);
    Use_US_Master = true;
    Use_US_Front = false;
    Use_US_LeftRight = true;
    P_Max = 0.5;
    P_Min_Front = 0.0;
    P_Min_LeftRight = 0.0;
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
      // Put custom auto code here
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    double P_Front = 1;
    double P_LeftRight = 1;
    double aInput = US_Input.getVoltage();

    if (Use_US_Master) {

      if (Use_US_Front && aInput < P_Max) {
        P_Front = 1 - aInput / P_Max;
        if (P_Front < P_Min_Front) {
          P_Front = P_Min_Front;
        }
      } else {
        P_Front = 1;
      }

      if (Use_US_LeftRight && aInput < P_Max) {
        P_LeftRight = 1 - aInput / P_Max;
        if (P_LeftRight < P_Min_LeftRight) {
          P_LeftRight = P_Min_LeftRight;
        }
      } else {
        P_LeftRight = 1;
      }
    } else {
      P_Front = 1;
      P_LeftRight = 1;
    }
    System.out.println("Voltage: " + aInput + " P_Front: " + P_Front + " P_LeftRight: " + P_LeftRight);
    System.out.println("Forward: " + -m_Stick.getY() * P_Front + " Turn: " + m_Stick.getX() * P_LeftRight);
    m_myRobot.arcadeDrive(-m_Stick.getY() * P_Front, m_Stick.getX() * P_LeftRight);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
