/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.InvertType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;
/*import edu.wpi.first.cameraserver.CameraServer; */

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private WPI_VictorSPX left_back_drive = new WPI_VictorSPX(7);
  private WPI_VictorSPX left_front_drive = new WPI_VictorSPX(8);
  private WPI_VictorSPX right_back_drive = new WPI_VictorSPX(6);
  private WPI_VictorSPX right_front_drive = new WPI_VictorSPX(5);
  private WPI_VictorSPX intake_conveyor = new WPI_VictorSPX(4);
  private WPI_VictorSPX pivot_motor = new WPI_VictorSPX(9);
  private final Joystick m_stick_0 = new Joystick(0);
  private final Joystick m_stick_1 = new Joystick(1);

  Solenoid Launcher;
  long button13_time;
  boolean Launcher_Value;
  DoubleSolenoid Beak_Extend;
  DoubleSolenoid Floor_Piston_Lift; // will lift the robot
  DoubleSolenoid Hatch_Clamp;
  DoubleSolenoid Rocket_Ramp_Piston;
  DoubleSolenoid Front_Floor_Piston_Lift; // will lift wheels first

  long teleop_time;

  DoubleSolenoid.Value Front_Floor_Piston_Lift_Value;
  long button2_time;

  DoubleSolenoid.Value Hatch_Clamp_Value;
  long button3_time;

  DoubleSolenoid.Value Beak_Extend_Value;
  long button4_time;

  DoubleSolenoid.Value Floor_Piston_Lift_Value;
  long button5_time;

  DoubleSolenoid.Value Rocket_Ramp_Piston_Value;
  long button6_time;

  long button7_time;
  long button8_time;
  DigitalInput limit_switch;

  boolean Use_US_Master = false;
  double P_Limit;
  double P_Ratio;
  AnalogInput US_Input = new AnalogInput(0);

  DifferentialDrive robot = new DifferentialDrive(left_front_drive, right_front_drive);
  Faults left_faults = new Faults();
  Faults right_faults = new Faults();

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // Intake_Hold = new DoubleSolenoid(2, 0, 1);
    Hatch_Clamp = new DoubleSolenoid(2, 2, 3);
    Rocket_Ramp_Piston = new DoubleSolenoid(2, 4, 5);
    Floor_Piston_Lift = new DoubleSolenoid(3, 0, 1);
    Beak_Extend = new DoubleSolenoid(3, 2, 3);
    Front_Floor_Piston_Lift = new DoubleSolenoid(2, 0, 1);
    Launcher = new Solenoid(3, 4);

    limit_switch = new DigitalInput(0);
    /*
     * CameraServer.getInstance().startAutomaticCapture(0);
     * CameraServer.getInstance().startAutomaticCapture(1);
     */

    /* factory default robotInt */
    right_front_drive.configFactoryDefault();
    right_back_drive.configFactoryDefault();
    left_front_drive.configFactoryDefault();
    left_back_drive.configFactoryDefault();
    intake_conveyor.configFactoryDefault();
    pivot_motor.configFactoryDefault();

    /* set up followers */
    right_back_drive.follow(right_front_drive);
    left_back_drive.follow(left_front_drive);

    /* [3] flip values so robot moves forwardard when stick-forwardard/LEDs-green */
    right_front_drive.setInverted(true); // !< Update this
    left_front_drive.setInverted(false); // !< Update this

    /*
     * set the invert of the followers to match their respective master controllers
     */
    right_back_drive.setInverted(InvertType.FollowMaster);
    left_back_drive.setInverted(InvertType.FollowMaster);

    /*
     * [4] adjust sensor phase so sensor moves positive when Talon LEDs are green
     */
    right_front_drive.setSensorPhase(true);
    left_front_drive.setSensorPhase(true);

    /*
     * WPI drivetrain classes defaultly assume left and right are opposite. call
     * this so we can apply + to both sides when moving forwardard. DO NOT CHANGE
     */
    robot.setRightSideInverted(false);

    Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kReverse);
    Floor_Piston_Lift.set(DoubleSolenoid.Value.kForward);
    Front_Floor_Piston_Lift.set(DoubleSolenoid.Value.kReverse);
    Hatch_Clamp.set(DoubleSolenoid.Value.kForward);
    Beak_Extend.set(DoubleSolenoid.Value.kReverse);
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    teleopInit();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    teleopPeriodic();
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {

    teleop_time = System.currentTimeMillis();
  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {

    /*
     * if (System.currentTimeMillis() - teleop_time > 1000 &&
     * System.currentTimeMillis() - teleop_time < 2000) {
     * Beak_Extend.set(DoubleSolenoid.Value.kForward); }
     */

    SmartDashboard.putBoolean("Rotate Arm Limit Switch", limit_switch.get());

    if (m_stick_0.getRawButton(1) || m_stick_1.getRawButton(1)) {
      intake_conveyor.set(-1.0);
    }

    
    if (m_stick_0.getRawButton(10) || m_stick_1.getRawButton(10)) {
      intake_conveyor.set(-0.5);
    }

    if (!m_stick_0.getRawButton(1) && !m_stick_0.getRawButton(10) && !m_stick_1.getRawButton(1)
        && !m_stick_1.getRawButton(10)) {
      intake_conveyor.set(0);
    }
    
    if (m_stick_0.getRawButton(3) || m_stick_1.getRawButton(3)) {
      if (Hatch_Clamp_Value == Value.kForward) {
        Hatch_Clamp.set(DoubleSolenoid.Value.kReverse);
      } else {
        Hatch_Clamp.set(DoubleSolenoid.Value.kForward);
      }
      button3_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button3_time > 250) {
        Hatch_Clamp_Value = Hatch_Clamp.get();
      }
    }

    if (Hatch_Clamp_Value == Value.kForward) {
      /* System.out.println("Hatch Clamp - Clamped"); */
      SmartDashboard.putBoolean("Hatch Clamp", true);
    } else {
      /* System.out.println("Hatch Clamp - NOT Clamped"); */
      SmartDashboard.putBoolean("Hatch Clamp", false);
    }

    if (m_stick_0.getRawButton(4) || m_stick_1.getRawButton(4)) {
      if (Beak_Extend_Value == Value.kForward) {
        Beak_Extend.set(DoubleSolenoid.Value.kReverse);
      } else {
        Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kReverse);
        Beak_Extend.set(DoubleSolenoid.Value.kForward);
      }
      button4_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button4_time > 250) {
        Beak_Extend_Value = Beak_Extend.get();
      }
    }

    if (Beak_Extend_Value == Value.kForward) {
      System.out.println("Beak - Extended");
      SmartDashboard.putBoolean("Beak Extend", true);
    } else {
      /* System.out.println("Beak - NOT Extended"); */
      SmartDashboard.putBoolean("Beak Extend", false);
    }

    if (m_stick_0.getRawButton(5) || m_stick_1.getRawButton(5)) {
      if (Floor_Piston_Lift_Value == Value.kForward) {
        Floor_Piston_Lift.set(DoubleSolenoid.Value.kReverse);
      } else {
        Floor_Piston_Lift.set(DoubleSolenoid.Value.kForward);
      }
      button5_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button5_time > 250) {
        Floor_Piston_Lift_Value = Floor_Piston_Lift.get();
      }

    }

    if (m_stick_0.getRawButton(2) || m_stick_1.getRawButton(2)) {
      if (Front_Floor_Piston_Lift_Value == Value.kReverse) {
        Front_Floor_Piston_Lift.set(DoubleSolenoid.Value.kForward);
      } else {
        Front_Floor_Piston_Lift.set(DoubleSolenoid.Value.kReverse);
      }
      button2_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button2_time > 250) {
        Front_Floor_Piston_Lift_Value = Front_Floor_Piston_Lift.get();
      }

    }

    if (m_stick_0.getRawButton(6) || m_stick_1.getRawButton(6)) {
      if (Rocket_Ramp_Piston_Value == Value.kForward) {
        Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kReverse);
      } else {
        Hatch_Clamp.set(DoubleSolenoid.Value.kReverse);
        Beak_Extend.set(DoubleSolenoid.Value.kReverse);
        Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kForward);
      }
      button6_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button6_time > 250) {
        Rocket_Ramp_Piston_Value = Rocket_Ramp_Piston.get();
      }
    }

    /* if (m_stick_0.getRawButton(7) || m_stick_1.getRawButton(7)) {
      Use_US_Master = true;
    }

    if (m_stick_0.getRawButton(8) || m_stick_1.getRawButton(8)) {
      Use_US_Master = false;
    } */
    String work = "";

    /* Set Ultrasonic */
    double aInput = US_Input.getVoltage();
    SmartDashboard.putBoolean("Ultrasonic", Use_US_Master);
    if (Use_US_Master && Beak_Extend_Value == Value.kForward) {
      System.out.println("Ultrasonic (V): " + aInput);
      P_Limit = 0.4;
      if (aInput < P_Limit) {
        P_Ratio = 0.5;
      } else {
        P_Ratio = 1;
      }
    } else {
      P_Ratio = 1;
    }

    /* get gamepad stick values */
    double forward_0 = -1 * m_stick_0.getRawAxis(1); /* positive is forwardard */
    double turn_0 = +1 * P_Ratio * m_stick_0.getRawAxis(0); /* positive is right */
    double forward_0_low = -1 * 0.5 * m_stick_0.getRawAxis(5); /* positive is forwardard */
    double turn_0_low = +1 * 0.5 * P_Ratio * m_stick_0.getRawAxis(4); /* positive is right */

    double forward_1 = -1 * m_stick_1.getRawAxis(1); /* positive is forwardard */
    double turn_1 = +1 * P_Ratio * m_stick_1.getRawAxis(0); /* positive is right */
    double forward_1_low = -1 * 0.5 * m_stick_1.getRawAxis(5); /* positive is forwardard */
    double turn_1_low = +1 * 0.5 * P_Ratio * m_stick_1.getRawAxis(4); /* positive is right */

    double pivot_down_0 = -0.5 * m_stick_0.getRawAxis(3); /* negative is down */
    double pivot_up_0 = +0.5 * m_stick_0.getRawAxis(2); /* positive is up */

    double pivot_down_1 = -0.5 * m_stick_1.getRawAxis(3); /* negative is down */
    double pivot_up_1 = +0.5 * m_stick_1.getRawAxis(2); /* positive is up */

    /* deadband gamepad 10% */
    if (Math.abs(forward_0) < 0.10) {
      forward_0 = 0;
    }
    if (Math.abs(turn_0) < 0.10) {
      turn_0 = 0;
    }
    if (Math.abs(forward_0_low) < 0.10) {
      forward_0_low = 0;
    }
    if (Math.abs(turn_0_low) < 0.10) {
      turn_0_low = 0;
    }
    if (Math.abs(pivot_down_0) < 0.10) {
      pivot_down_0 = 0;
    }
    if (Math.abs(pivot_up_0) < 0.10) {
      pivot_up_0 = 0;
    }
    if (Math.abs(forward_1) < 0.10) {
      forward_1 = 0;
    }
    if (Math.abs(turn_1) < 0.10) {
      turn_1 = 0;
    }
    if (Math.abs(forward_1_low) < 0.10) {
      forward_1_low = 0;
    }
    if (Math.abs(turn_1_low) < 0.10) {
      turn_1_low = 0;
    }
    if (Math.abs(pivot_down_1) < 0.10) {
      pivot_down_1 = 0;
    }
    if (Math.abs(pivot_up_1) < 0.10) {
      pivot_up_1 = 0;
    }

    /* drive robot */
    if (forward_0 != 0 || turn_0 != 0) {
      robot.arcadeDrive(forward_0, turn_0);
    }
    if (forward_0_low != 0 || turn_0_low != 0) {
      robot.arcadeDrive(forward_0_low, turn_0_low);
    }
    if (forward_1 != 0 || turn_1 != 0) {
      robot.arcadeDrive(forward_1, turn_1);
    }
    if (forward_1_low != 0 || turn_1_low != 0) {
      robot.arcadeDrive(forward_1_low, turn_1_low);
    }

    /* pivot intake arm */
    /*
     * if (limit_switch.get()) { pivot_down_0 = 0; pivot_down_1 = 0; }
     */
    if ((Math.abs(pivot_down_0) > 0 && Math.abs(pivot_up_0) > 0)
        || (Math.abs(pivot_down_1) > 0 && Math.abs(pivot_up_1) > 0)) {
      pivot_motor.set(0);
    } else {
      if (Math.abs(pivot_down_0) > 0) {
        pivot_motor.set(pivot_down_0);
      }
      if (Math.abs(pivot_up_0) > 0) {
        pivot_motor.set(pivot_up_0);
      }
      if (Math.abs(pivot_down_1) > 0) {
        pivot_motor.set(pivot_down_1);
      }
      if (Math.abs(pivot_up_1) > 0) {
        pivot_motor.set(pivot_up_1);
      }
      if (pivot_down_0 == 0 && pivot_up_0 == 0 && pivot_down_1 == 0 && pivot_up_1 == 0) {
        pivot_motor.set(0);
      }
    }

    /*
     * [2] Make sure Gamepad forwardard is positive for FORWARD, and GZ is positive
     * for RIGHT
     */
    work += " GF:" + forward_0 + " GT:" + turn_0 + " LT:" + pivot_up_0 + " RT:" + pivot_down_0;

    /* get sensor values */
    // double left_position = left_front_drive.GetSelectedSensorPosition(0);
    // double right_position = right_front_drive.GetSelectedSensorPosition(0);
    double left_velocity = left_front_drive.getSelectedSensorVelocity(0); /* Units per 100ms */
    double right_velocity = right_front_drive.getSelectedSensorVelocity(0); /* Units per 100ms */

    work += " L:" + left_velocity + " R:" + right_velocity;

    /*
     * drive motor at least 25%, Talons will auto-detect if sensor is out of phase
     */
    left_front_drive.getFaults(left_faults);
    right_front_drive.getFaults(right_faults);

    if (left_faults.SensorOutOfPhase) {
      work += " L sensor is out of phase";
    }
    if (right_faults.SensorOutOfPhase) {
      work += " R sensor is out of phase";
    }

    /*
     * print to console if message if (work != "") { System.out.println(work); }
     */

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}

/*
 * private static void createAndShowGUI() { //Create and set up the window.
 * JFrame frame = new JFrame("FrameDemo");
 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 * 
 * JLabel emptyLabel = new JLabel(""); emptyLabel.setPreferredSize(new
 * Dimension(175, 100)); frame.getContentPane().add(emptyLabel,
 * BorderLayout.CENTER);
 * 
 * //Display the window frame.pack(); frame.setVisible(true); }
 */

/*
 * public static void main(String[] args) { //Schedule a job for the
 * event-dispatching thread: //creating and showing this application's GUI.
 * javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() {
 * 
 * } }); } }
 */
