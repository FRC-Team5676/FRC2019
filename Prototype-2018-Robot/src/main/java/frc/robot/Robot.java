/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private final DifferentialDrive m_robotDriveleft
      = new DifferentialDrive(new PWMVictorSPX(6), new PWMVictorSPX(9));
  /*private final DifferentialDrive m_robotDriveright 
     = new DifferentialDrive(new PWM VictorSPX(7), new PWMVictorSPX(8));*/
      private final Joystick m_stick = new Joystick(0);
      private final Timer m_timer = new Timer();
      Spark RightIntake = new Spark(7);
      Spark LeftIntake = new Spark(8);

  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      m_robotDriveleft.arcadeDrive(0.5, 0.0); 
      //m_robotDriveright.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      m_robotDriveleft.stopMotor();
     // m_robotDriveright.stopMotor(); // stop robot
    }
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {
    m_robotDriveleft.arcadeDrive(m_stick.getY(), m_stick.getX());
   // m_robotDriveright.arcadeDrive(m_stick.getY(), m_stick.getX());
   if(m_stick.getRawButton(1)){
      RightIntake.set(.7);
      LeftIntake.set(.7);
   }
   if(m_stick.getRawButton(2)){
     RightIntake.set(-.7);
     LeftIntake.set(-.7);
   }
   if(!m_stick.getRawButton(1) && !m_stick.getRawButton(2)){
     RightIntake.set(0);
     LeftIntake.set(0);
   }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
