/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.InvertType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.livewindow.LiveWindow;
//import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.cameraserver.CameraServer;

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
  private final Joystick m_stick = new Joystick(0);
  private final Timer m_timer = new Timer();

  // Spark RightIntake = new Spark(7);
  // Spark LeftIntake = new Spark(8);
  DoubleSolenoid Beak_Extend;
  DoubleSolenoid Floor_Piston_Lift; // will lift the robot
  // DoubleSolenoid Intake_Hold;
  DoubleSolenoid Hatch_Clamp;
  DoubleSolenoid Rocket_Ramp_Piston;

  //boolean intake_half_speed;
  //long button2_time;

  DoubleSolenoid.Value Beak_Extend_Value;
  long button4_time;
  long teleop_time;

  DoubleSolenoid.Value Hatch_Clamp_Value;
  long button3_time;
  long teleop_time2;

  DoubleSolenoid.Value Rocket_Ramp_Piston_Value;
  long button6_time;
  long teleop_time3;

  DoubleSolenoid.Value Floor_Piston_Lift_Value;
  long button5_time;
  long teleop_time4;

  DigitalInput limit_switch;
  DigitalInput reflective_sensor;

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

    limit_switch = new DigitalInput(0);
    reflective_sensor = new DigitalInput(7);
    CameraServer.getInstance().startAutomaticCapture(0);
    CameraServer.getInstance().startAutomaticCapture(1);

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
    Hatch_Clamp.set(DoubleSolenoid.Value.kForward);
    Beak_Extend.set(DoubleSolenoid.Value.kReverse);
    // LiveWindow.addActuator("SomeSubsystem","Beak", Beak_Extend);

    teleop_time = System.currentTimeMillis();
    teleop_time2 = System.currentTimeMillis();
    teleop_time3 = System.currentTimeMillis();
    teleop_time4 = System.currentTimeMillis();

  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {

    if (System.currentTimeMillis() - teleop_time > 1000 && System.currentTimeMillis() - teleop_time < 2000) {
      Beak_Extend.set(DoubleSolenoid.Value.kForward);
    }

    // SmartDashboard.putBoolean("Limit Switch", Hatch_Clamp.get());
    SmartDashboard.putBoolean("Reflective Sensor", reflective_sensor.get());

    /*if (m_stick.getRawButton(1)) {
      intake_conveyor.set(-1.0);
    } else {
        intake_conveyor.set(0);
    }*/

    if (m_stick.getRawButton(2)) {
      intake_conveyor.set(-0.5);
    } else {
        intake_conveyor.set(0);
    }

    if (m_stick.getRawButton(3)) {
      // Hatch_Clamp.set(DoubleSolenoid.Value.kReverse);
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
      System.out.println("Hatch Clamp - Clamped");
      SmartDashboard.putBoolean("Hatch Clamp", true);
    } else {
      System.out.println("Hatch Clamp - NOT Clamped");
      SmartDashboard.putBoolean("Hatch Clamp", false);
    }

    if (m_stick.getRawButton(4)) {
      if (Beak_Extend_Value == Value.kForward) {
        Beak_Extend.set(DoubleSolenoid.Value.kReverse);
      } else {
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
      System.out.println("Beak - NOT Extended");
      SmartDashboard.putBoolean("Beak Extend", false);
    }
    /*
     * if (m_stick.getRawButton(5)) {
     * Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kReverse); }
     */

    if (m_stick.getRawButton(5)) {
      // Floor_Piston_Lift.set(DoubleSolenoid.Value.kReverse);
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

    if (m_stick.getRawButton(6)) {
      // Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kForward);
      if (Rocket_Ramp_Piston_Value == Value.kForward) {
        Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kReverse);
      } else {
        Rocket_Ramp_Piston.set(DoubleSolenoid.Value.kForward);
      }
      button6_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button6_time > 250) {
        Rocket_Ramp_Piston_Value = Rocket_Ramp_Piston.get();
      }
    }

    // if (limit_switch.get() || reflective_sensor.get()) {
    // BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
    // } else {
    // BackdrivePiston.set(DoubleSolenoid.Value.kForward);
    // }
    String work = "";

    /* get gamepad stick values */
    double forward = -1 * m_stick.getRawAxis(1); /* positive is forwardard */
    double turn = +1 * m_stick.getRawAxis(0); /* positive is right */
    double forward_low = -1 * 0.5 * m_stick.getRawAxis(5); /* positive is forwardard */
    double turn_low = +1 * 0.5 * m_stick.getRawAxis(4); /* positive is right */
    
    double pivot_down = +1 * m_stick.getRawAxis(3); /* positive is down */
    double pivot_up = -1 * m_stick.getRawAxis(2); /* negative is up */

    /* deadband gamepad 10% */
    if (Math.abs(forward) < 0.10) {
      forward = 0;
    }
    if (Math.abs(turn) < 0.10) {
      turn = 0;
    }
    if (Math.abs(forward_low) < 0.10) {
      forward_low = 0;
    }
    if (Math.abs(turn_low) < 0.10) {
      turn_low = 0;
    }
    if (Math.abs(pivot_down) < 0.10) {
      pivot_down = 0;
    }
    if (Math.abs(pivot_up) < 0.10) {
      pivot_up = 0;
    }

    /* drive robot */
    if (forward != 0 || turn != 0) {
      robot.arcadeDrive(forward, turn);
    }
    if (forward_low != 0 || turn_low != 0) {
      robot.arcadeDrive(forward_low, turn_low);
    }

    /* pivot intake arm */
    if (Math.abs(pivot_down) > 0 && Math.abs(pivot_up) > 0) {
      pivot_motor.set(0);
    } else {
      if (Math.abs(pivot_down) > 0) {
        pivot_motor.set(pivot_down);
      }
      if (Math.abs(pivot_up) > 0) {
        pivot_motor.set(pivot_up);
      }
      if (pivot_down == 0 && pivot_up == 0) {
        pivot_motor.set(0);
      }
    }

    /*
     * [2] Make sure Gamepad forwardard is positive for FORWARD, and GZ is positive
     * for RIGHT
     */
    work += " GF:" + forward + " GT:" + turn + " LT:" + pivot_up + " RT:" + pivot_down;

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

    /* print to console if message */
    if (work != "") {
      System.out.println(work);
    }

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
