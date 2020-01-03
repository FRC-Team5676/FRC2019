/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/*import edu.wpi.first.cameraserver.CameraServer; */

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class T_Shirt_Robot extends TimedRobot {
  private VictorSP pivot_motor = new VictorSP(4);
  private final Joystick m_stick_0 = new Joystick(0);
  private final Joystick m_stick_1 = new Joystick(1);

  
  DoubleSolenoid Launcher;
  DoubleSolenoid Loader; // used to load shirt into chamber
  

  long teleop_time;

  DoubleSolenoid.Value Launcher_Value;
  long button2_time;

  DoubleSolenoid.Value Loader_Value;
  long button3_time;

  

  
   /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    
    Launcher = new DoubleSolenoid(2, 0, 1);
    Loader = new DoubleSolenoid(2, 2, 3);
    
    

    Loader.set(DoubleSolenoid.Value.kReverse);
    
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

    if (m_stick_0.getRawButton(1) || m_stick_1.getRawButton(1)) {
      pivot_motor.set(1.0);
    }

        if (m_stick_0.getRawButton(4) || m_stick_1.getRawButton(4)) {
      pivot_motor.set(1.0);
    }

    if (!m_stick_0.getRawButton(1) && !m_stick_0.getRawButton(4) && !m_stick_1.getRawButton(1)
        && !m_stick_1.getRawButton(4)) {
      pivot_motor.set(0);
    }
    
    
    if (m_stick_0.getRawButton(2) || m_stick_1.getRawButton(2)) {
      if (Launcher_Value == Value.kForward) {
        Launcher.set(DoubleSolenoid.Value.kReverse);
      } else {
        Launcher.set(DoubleSolenoid.Value.kForward);
      }
      button2_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button2_time > 250) {
        Launcher_Value = Launcher.get();
      }
    }

    if (Launcher_Value == Value.kForward) {
      /* System.out.println("Launcher Ready - Ready"); */
      SmartDashboard.putBoolean("Launcher", true);
    } else {
      /* System.out.println("Launcher - NOT Ready"); */
      SmartDashboard.putBoolean("Launcher", false);
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
 * JL abel emptyLabel = new JLabel(""); emptyLabel.setPreferredSize(new
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
