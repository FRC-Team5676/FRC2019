/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5676.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	Gyro gyro;
	double Kp = 0.01; 
	private DifferentialDrive m_myRobot;
	private Joystick m_leftStick;
	private Joystick m_rightStick;
	final String kDefaultAuto = "Default";
	final String Left = "Left";
	final String Right = "Right";
	final String Center = "Center";
	final String Test = "Test";
	final String KeepItStraight = "KeepItStraight";
    String autoSelected;
	SendableChooser<String> chooser;
	Spark RightIntake = new Spark(8);
	Spark LeftIntake = new Spark(7);
	WPI_TalonSRX LeftBackDrive = new WPI_TalonSRX(0);
	WPI_TalonSRX Climber = new WPI_TalonSRX(1);
	WPI_TalonSRX Intake = new WPI_TalonSRX(2);
	WPI_TalonSRX RightFrontDrive = new WPI_TalonSRX(3);
	VictorSP LeftFrontDrive = new VictorSP(9);
	VictorSP RightBackDrive = new VictorSP(6);
	SpeedControllerGroup m_right = new SpeedControllerGroup(LeftBackDrive, LeftFrontDrive);
	SpeedControllerGroup m_left = new SpeedControllerGroup(RightBackDrive, RightFrontDrive);
	DigitalInput TopPlacer;
	DigitalInput BotPlacer;
	DigitalInput TopClimber;
	DigitalInput BotClimber;
    DoubleSolenoid BackdrivePiston;
	DoubleSolenoid ClampPiston;
    DoubleSolenoid RotatePiston;
    Compressor c = new Compressor(0);
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
        ClampPiston = new DoubleSolenoid(2,3);
        RotatePiston = new DoubleSolenoid(4,5);
		BackdrivePiston = new DoubleSolenoid(0,1);
		TopPlacer = new DigitalInput(0);
		BotPlacer = new DigitalInput(1);
		TopClimber = new DigitalInput(2);
		BotClimber = new DigitalInput(3);
		m_myRobot = new DifferentialDrive(m_left, m_right);
		m_leftStick = new Joystick(0);
		m_rightStick = new Joystick(1);
		LeftBackDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10); 
		LeftBackDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		RightFrontDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10); 
		RightFrontDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		Intake.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10); 
		Intake.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);  
		chooser = new SendableChooser<String>();
		chooser.addDefault("Default Auto", kDefaultAuto);
		chooser.addObject("Left", Left);
		chooser.addObject("Right", Right);
		chooser.addObject("Center", Center);
		chooser.addObject("Test", Test);
		chooser.addObject("KeepItStraight", KeepItStraight);
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.updateValues();
        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
        CameraServer.getInstance().startAutomaticCapture();
        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);

        
	}

	@Override
	public void disabledPeriodic()
	{
	}
	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	int x = 0;
	int autoloopcounter = 0;
	int ForwardDistance = 30000;
	int IntakeDistance = 12000;
	int TurnRight90 = 80;
	int TurnLeft90 = -80;
	int TurnRight45 = 40;
	int TurnLeft45 = -40;
	boolean Turned = false;
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		
			case Left:
				SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
				SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("angle", gyro.getAngle());
				SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
				SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
				SmartDashboard.putBoolean("TopClimber", TopClimber.get());
				SmartDashboard.putBoolean("BotClimber", BotClimber.get());
				String gameData = DriverStation.getInstance().getGameSpecificMessage();
				if(gameData.charAt(0) == 'L'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				        x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance + 2000 && x == 2) {
						m_myRobot.arcadeDrive(-.6, .125);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > ForwardDistance + 2000 && x == 2) {
						m_myRobot.arcadeDrive(0, 0);
						Timer.delay(.25);
						x = x + 1;
					}
					if(gyro.getAngle() < TurnRight90 && x == 3){
						m_myRobot.arcadeDrive(0, .6);
					}
			    	if(gyro.getAngle() > TurnRight90 && x == 3){
						m_myRobot.arcadeDrive(0, 0);
						x= x +1;
					}
					if(autoloopcounter < 100 && x == 4) {
						m_myRobot.arcadeDrive(-.6, 0);
						Intake.set(ControlMode.PercentOutput, -.7);
						autoloopcounter ++;
					}
					if(autoloopcounter == 100 && x == 4) {
						Intake.set(ControlMode.PercentOutput, 0);
						m_myRobot.arcadeDrive(0, 0);
						x = x + 1;
						autoloopcounter = 0;
					}
			    	if(x == 5) {
			    		RotatePiston.set(DoubleSolenoid.Value.kForward);
			    		Timer.delay(.25);
			    	}
			    	if(x == 5) {
			    		x = x +1;
			    	}
					if(autoloopcounter < 50 && x == 6) {
						RightIntake.set(.7);
						LeftIntake.set(.7);
					}
					if(autoloopcounter == 50 && x == 6) {
						RightIntake.set(0);
						LeftIntake.set(0);
						x = x+1;
						autoloopcounter = 0;
					}
					if(x == 7) {
			    		ClampPiston.set(DoubleSolenoid.Value.kForward);
			    	}
			    	if(x == 7) {
			    		x = x +1;
			    	}
				}
				if(gameData.charAt(0) == 'R'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					double angle = gyro.getAngle();
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
					}
					if(x == 0) {
						x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
					if(x == 2) {
						RotatePiston.set(DoubleSolenoid.Value.kForward);
					}
					if(x == 2) {
						Timer.delay(.75);
						x = x +1;
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance && x == 3) {
						ClampPiston.set(DoubleSolenoid.Value.kReverse);
					}
					if(ClampPiston.get() == DoubleSolenoid.Value.kReverse & x == 3) {
						x = x + 1;
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance && x == 4) {
						m_myRobot.arcadeDrive(-.6, 0);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > ForwardDistance && x == 4) {
					m_myRobot.arcadeDrive(0, 0);
					x = x + 1;
					}
				}
				break;
			case Right:
				SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
				SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("angle", gyro.getAngle());
				SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
				SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
				SmartDashboard.putBoolean("TopClimber", TopClimber.get());
				SmartDashboard.putBoolean("BotClimber", BotClimber.get());
				gameData = DriverStation.getInstance().getGameSpecificMessage();
				if(gameData.charAt(0) == 'R'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					double angle = gyro.getAngle();
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				        x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance + 2000 && x == 2) {
						m_myRobot.arcadeDrive(-.6, .15);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > ForwardDistance + 2000 && x == 2) {
						m_myRobot.arcadeDrive(0, 0);
						Timer.delay(.25);
						x = x + 1;
					}
					if(gyro.getAngle() > TurnLeft90 && x == 3){
						m_myRobot.arcadeDrive(0, -.6);
					}
			    	if(gyro.getAngle() < TurnLeft90 && x == 3){
						m_myRobot.arcadeDrive(0, 0);
						x= x +1;
					}
					if(autoloopcounter < 100 && x == 4) {
						m_myRobot.arcadeDrive(-.6, 0);
						Intake.set(ControlMode.PercentOutput, -.7);
						autoloopcounter ++;
					}
					if(autoloopcounter == 100 && x == 4) {
						Intake.set(ControlMode.PercentOutput, 0);
						m_myRobot.arcadeDrive(0, 0);
						x = x + 1;
						autoloopcounter = 0;
					}
			    	if(x == 5) {
			    		RotatePiston.set(DoubleSolenoid.Value.kForward);
			    		Timer.delay(.25);
			    	}
			    	if(x == 5) {
			    		x = x +1;
			    	}
					if(autoloopcounter < 50 && x == 6) {
						RightIntake.set(.7);
						LeftIntake.set(.7);
					}
					if(autoloopcounter == 50 && x == 6) {
						RightIntake.set(0);
						LeftIntake.set(0);
						x = x+1;
						autoloopcounter = 0;
					}
					if(x == 7) {
			    		ClampPiston.set(DoubleSolenoid.Value.kForward);
			    	}
			    	if(x == 7) {
			    		x = x +1;
			    	}
				}
				if(gameData.charAt(0) == 'L'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					double angle = gyro.getAngle();
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
					}
					if(x == 0) {
						x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
					if(x == 2) {
						RotatePiston.set(DoubleSolenoid.Value.kForward);
					}
					if(x == 2) {
						Timer.delay(.75);
						x = x +1;
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance && x == 3) {
						ClampPiston.set(DoubleSolenoid.Value.kReverse);
					}
					if(ClampPiston.get() == DoubleSolenoid.Value.kReverse & x == 3) {
						x = x + 1;
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance && x == 4) {
						m_myRobot.arcadeDrive(-.6, 0);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > ForwardDistance && x == 4) {
					m_myRobot.arcadeDrive(0, 0);
					x = x + 1;
					}
				}
				break;
				
			case Center:
				SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
				SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("angle", gyro.getAngle());
				SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
				SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
				SmartDashboard.putBoolean("TopClimber", TopClimber.get());
				SmartDashboard.putBoolean("BotClimber", BotClimber.get());
				gameData = DriverStation.getInstance().getGameSpecificMessage();
				if(gameData.charAt(0) == 'L'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
					}
					if(x == 0) {
						x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < 2000 && x == 2) {
					m_myRobot.arcadeDrive(-.6, 0);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > 2000 && x == 2) {
						m_myRobot.arcadeDrive(0, 0);
						Timer.delay(.25);
						x = x + 1;
					}
					if(gyro.getAngle() > TurnLeft45 && x == 3){
						m_myRobot.arcadeDrive(0, -.6);
					}
			    	if(gyro.getAngle() < TurnLeft45 && x == 3){
						m_myRobot.arcadeDrive(0, 0);
						x= x +1;
					}
			    	if(x == 4) {
			    		RightFrontDrive.setSelectedSensorPosition(0,0,10);
			    	}
			    	if(x == 4) {
			    		x = x + 1;
			    	}
			    	if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < 25000 && x == 5) {
						m_myRobot.arcadeDrive(-.6, 0);
						}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > 25000 && x == 5) {
						m_myRobot.arcadeDrive(0, 0);
						//Timer.delay(.25);
						x = x + 1;
					}
					if(x == 6) {
						gyro.reset();
						Timer.delay(.5);
					}
					if(x == 6) {
						x = x +1;
					}
					if(gyro.getAngle() < 50 && x == 7){
						m_myRobot.arcadeDrive(0, .6);
					}
			    	if(gyro.getAngle() > 50 && x == 7){
						m_myRobot.arcadeDrive(0, 0);
						x= x +1;
					}
			    	if(x == 8) {
			    		RotatePiston.set(DoubleSolenoid.Value.kForward);
			    		Timer.delay(.25);
			    	}
			    	if(x == 8) {
			    		x = x +1;
			    	}
					if(autoloopcounter < 100 && x == 9) {
						Intake.set(ControlMode.PercentOutput, -.7);
						m_myRobot.arcadeDrive(-.5, 0);
						autoloopcounter ++;
					}
					if(autoloopcounter == 100 && x == 9) {
						m_myRobot.arcadeDrive(0, 0);
						Intake.set(ControlMode.PercentOutput, 0);
						x = x + 1;
						autoloopcounter = 0;
					}
					if(autoloopcounter < 50 && x == 10) {
						RightIntake.set(.7);
						LeftIntake.set(.7);
					}
					if(autoloopcounter == 50 && x == 10) {
						RightIntake.set(0);
						LeftIntake.set(0);
						x = x+1;
						autoloopcounter = 0;
					}
					if(x == 11) {
			    		ClampPiston.set(DoubleSolenoid.Value.kForward);
			    	}
			    	if(x == 11) {
			    		x = x +1;
			    	}
				}
				if(gameData.charAt(0) == 'R'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					double angle = gyro.getAngle();
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
					}
					if(x == 0) {
						x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
			    	if(x == 2) {
			    		RotatePiston.set(DoubleSolenoid.Value.kForward);
			    		Timer.delay(.25);
			    	}
			    	if(x == 2) {
			    		x = x +1;
			    	}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < 14000 && x == 3) {
						m_myRobot.arcadeDrive(-.6, -angle*Kp);
						Intake.set(ControlMode.PercentOutput, -.7);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > 14000 && x == 3) {
						m_myRobot.arcadeDrive(0, 0);
						Intake.set(ControlMode.PercentOutput, 0);
						x = x + 1;
					}
					if(autoloopcounter < 100 && x == 4) {
						m_myRobot.arcadeDrive(-.6, 0);
						autoloopcounter ++;
					}
					if(autoloopcounter > 99 && x == 4) {
						m_myRobot.arcadeDrive(0, 0);
						x = x + 1;
						autoloopcounter = 0;
					}
					if(autoloopcounter < 50 && x == 5) {
						RightIntake.set(.7);
						LeftIntake.set(.7);
					}
					if(autoloopcounter == 50 && x == 5) {
						RightIntake.set(0);
						LeftIntake.set(0);
						x = x+1;
						autoloopcounter = 0;
					}
					if(x == 6) {
			    		ClampPiston.set(DoubleSolenoid.Value.kForward);
			    	}
			    	if(x == 6) {
			    		x = x +1;
			    	}
				}
				break;
			case Test:
				SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
				SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("angle", gyro.getAngle());
				SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
				SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
				SmartDashboard.putBoolean("TopClimber", TopClimber.get());
				SmartDashboard.putBoolean("BotClimber", BotClimber.get());
				gameData = DriverStation.getInstance().getGameSpecificMessage();
				if(gameData.charAt(1) == 'L'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					double angle = gyro.getAngle();
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				        x = x + 1;
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance + 35000 && x == 1) {
						m_myRobot.arcadeDrive(-.6, .125);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > ForwardDistance + 35000 && x == 1) {
						m_myRobot.arcadeDrive(0, 0);
						Timer.delay(.25);
						x = x + 1;
					}
					if(gyro.getAngle() < 70 && x == 2){
						m_myRobot.arcadeDrive(0, .6);
					}
			    	if(gyro.getAngle() > 70 && x == 2){
						m_myRobot.arcadeDrive(0, 0);
						x= x +1;
					}
					if(x == 3 && TopPlacer.get() == false) {
						m_myRobot.arcadeDrive(0, 0);
						Intake.set(ControlMode.PercentOutput, -.6);
						autoloopcounter ++;
					}
					if(x == 3 && TopClimber.get() == true) {
						Intake.set(ControlMode.PercentOutput, 0);
						m_myRobot.arcadeDrive(0, 0);
						x = x + 1;
						autoloopcounter = 0;
					}
					if(x == 4) {
						RotatePiston.set(DoubleSolenoid.Value.kForward);
						Timer.delay(.75);
					}
					if(x == 4) {
						x = x+1;
					}
					if(x == 5) {
			    		RightFrontDrive.setSelectedSensorPosition(0,0,10);
			    	}
			    	if(x == 5) {
			    		x = x + 1;
			    	}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < 5000 && x == 6) {
						m_myRobot.arcadeDrive(-.4, .0);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > 5000 && x == 6) {
						m_myRobot.arcadeDrive(0, 0);
						Timer.delay(.25);
						x = x + 1;
					}
					if(x == 7) {
						ClampPiston.set(DoubleSolenoid.Value.kReverse);
					}
					if(x == 7) {
						x = x+1;
					}
					if(x == 8) {
						RotatePiston.set(DoubleSolenoid.Value.kReverse);
					}
					if(x == 8) {
						x = x+1;
					}
					if(x == 9 && autoloopcounter < 50) {
						m_myRobot.arcadeDrive(.4, 0);
						autoloopcounter++;
					}
					if(x == 9 && autoloopcounter == 50) {
						m_myRobot.arcadeDrive(0, 0);
					}
				}
				break;
			case kDefaultAuto:
			default:
				SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
				SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
				SmartDashboard.putNumber("angle", gyro.getAngle());
				SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
				SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
				SmartDashboard.putBoolean("TopClimber", TopClimber.get());
				SmartDashboard.putBoolean("BotClimber", BotClimber.get());
				gameData = DriverStation.getInstance().getGameSpecificMessage();
				if(gameData.charAt(0) == 'L'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
					}
					if(x == 0) {
						x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < 2000 && x == 2) {
					m_myRobot.arcadeDrive(-.6, 0);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > 2000 && x == 2) {
						m_myRobot.arcadeDrive(0, 0);
						Timer.delay(.25);
						x = x + 1;
					}
					if(gyro.getAngle() > TurnLeft45 && x == 3){
						m_myRobot.arcadeDrive(0, -.6);
					}
			    	if(gyro.getAngle() < TurnLeft45 && x == 3){
						m_myRobot.arcadeDrive(0, 0);
						x= x +1;
					}
			    	if(x == 4) {
			    		RightFrontDrive.setSelectedSensorPosition(0,0,10);
			    	}
			    	if(x == 4) {
			    		x = x + 1;
			    	}
			    	if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < 25000 && x == 5) {
						m_myRobot.arcadeDrive(-.6, 0);
						}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > 25000 && x == 5) {
						m_myRobot.arcadeDrive(0, 0);
						//Timer.delay(.25);
						x = x + 1;
					}
					if(x == 6) {
						gyro.reset();
						Timer.delay(.5);
					}
					if(x == 6) {
						x = x +1;
					}
					if(gyro.getAngle() < 50 && x == 7){
						m_myRobot.arcadeDrive(0, .6);
					}
			    	if(gyro.getAngle() > 50 && x == 7){
						m_myRobot.arcadeDrive(0, 0);
						x= x +1;
					}
			    	if(x == 8) {
			    		RotatePiston.set(DoubleSolenoid.Value.kForward);
			    		Timer.delay(.25);
			    	}
			    	if(x == 8) {
			    		x = x +1;
			    	}
					if(autoloopcounter < 100 && x == 9) {
						Intake.set(ControlMode.PercentOutput, -.7);
						m_myRobot.arcadeDrive(-.5, 0);
						autoloopcounter ++;
					}
					if(autoloopcounter == 100 && x == 9) {
						m_myRobot.arcadeDrive(0, 0);
						Intake.set(ControlMode.PercentOutput, 0);
						x = x + 1;
						autoloopcounter = 0;
					}
					if(autoloopcounter < 50 && x == 10) {
						RightIntake.set(.7);
						LeftIntake.set(.7);
					}
					if(autoloopcounter == 50 && x == 10) {
						RightIntake.set(0);
						LeftIntake.set(0);
						x = x+1;
						autoloopcounter = 0;
					}
					if(x == 11) {
			    		ClampPiston.set(DoubleSolenoid.Value.kForward);
			    	}
			    	if(x == 11) {
			    		x = x +1;
			    	}
				}
				if(gameData.charAt(0) == 'R'){
					RightFrontDrive.setNeutralMode(NeutralMode.Brake);
					LeftBackDrive.setNeutralMode(NeutralMode.Brake);
					double angle = gyro.getAngle();
					if(x == 0) {
						gyro.reset();
				        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
					}
					if(x == 0) {
						x = x + 1;
					}
					if(x == 1) {
			    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
			    	}
			    	if(x == 1) {
			    		x = x +1;
			    	}
			    	if(x == 2) {
			    		RotatePiston.set(DoubleSolenoid.Value.kForward);
			    		Timer.delay(.25);
			    	}
			    	if(x == 2) {
			    		x = x +1;
			    	}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < 14000 && x == 3) {
						m_myRobot.arcadeDrive(-.6, -angle*Kp);
						Intake.set(ControlMode.PercentOutput, -.7);
					}
					if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > 14000 && x == 3) {
						m_myRobot.arcadeDrive(0, 0);
						Intake.set(ControlMode.PercentOutput, 0);
						x = x + 1;
					}
					if(autoloopcounter < 100 && x == 4) {
						m_myRobot.arcadeDrive(-.6, 0);
						autoloopcounter ++;
					}
					if(autoloopcounter > 99 && x == 4) {
						m_myRobot.arcadeDrive(0, 0);
						x = x + 1;
						autoloopcounter = 0;
					}
					if(autoloopcounter < 50 && x == 5) {
						RightIntake.set(.7);
						LeftIntake.set(.7);
					}
					if(autoloopcounter == 50 && x == 5) {
						RightIntake.set(0);
						LeftIntake.set(0);
						x = x+1;
						autoloopcounter = 0;
					}
					if(x == 6) {
			    		ClampPiston.set(DoubleSolenoid.Value.kForward);
			    	}
			    	if(x == 6) {
			    		x = x +1;
			    	}
				}
				break;
			case KeepItStraight:
				RightFrontDrive.setNeutralMode(NeutralMode.Brake);
				LeftBackDrive.setNeutralMode(NeutralMode.Brake);
				if(x == 0) {
					gyro.reset();
			        RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
			        LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				}
				if(x == 0) {
					x = x + 1;
				}
				if(x == 1) {
		    		ClampPiston.set(DoubleSolenoid.Value.kReverse);
		    	}
		    	if(x == 1) {
		    		x = x +1;
		    	}
				if(x == 2) {
					RotatePiston.set(DoubleSolenoid.Value.kForward);
				}
				if(x == 2) {
					x = x +1;
				}
				if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) < ForwardDistance && x == 3) {
					m_myRobot.arcadeDrive(-.6, 0);
				}
				if(SmartDashboard.getNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0))) > ForwardDistance && x == 3) {
					m_myRobot.arcadeDrive(0, 0);
					x = x + 1;
				}
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	boolean Clamp = false;
	boolean CanClimb = false;
	@Override	
	public void teleopPeriodic() {

		while(!TopPlacer.get() && !BotPlacer.get() && !TopClimber.get() && !BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
			
			double goingupintake = m_leftStick.getRawAxis(3);
			double goingdownintake = m_leftStick.getRawAxis(2);
			double goingupclimber = m_rightStick.getRawAxis(3);
			double goingdownclimber = m_rightStick.getRawAxis(2);
			
			if(goingupclimber > goingdownclimber) {
				Climber.set(ControlMode.PercentOutput,goingupclimber);
			}
			else if(goingupclimber < goingdownclimber) {
				Climber.set(ControlMode.PercentOutput,-goingdownclimber);
			}
			
			if(goingupintake > goingdownintake) {
				Intake.set(ControlMode.PercentOutput,-goingupintake);
			}
			else if(goingupintake < goingdownintake) {
				Intake.set(ControlMode.PercentOutput,goingdownintake);
			}
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
				
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
			}
		while(TopPlacer.get() && !BotPlacer.get() && !TopClimber.get() && !BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());			
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
			
			double goingdownintake = m_leftStick.getRawAxis(2);
			double goingupclimber = m_rightStick.getRawAxis(3);
			double goingdownclimber = m_rightStick.getRawAxis(2);
			Intake.set(ControlMode.PercentOutput, goingdownintake);
			
			if(goingupclimber > goingdownclimber) {
				Climber.set(ControlMode.PercentOutput,goingupclimber);
			}
			else if(goingupclimber < goingdownclimber) {
				Climber.set(ControlMode.PercentOutput,-goingdownclimber);
			}
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
				
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
			}
		while(TopPlacer.get() && !BotPlacer.get() && TopClimber.get() && !BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
			
			double goingdownintake = m_leftStick.getRawAxis(2);
			double goingdownclimber = m_rightStick.getRawAxis(2);
			
			Intake.set(ControlMode.PercentOutput, goingdownintake);
			Climber.set(ControlMode.PercentOutput,-goingdownclimber);
			
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
			}
		while(TopPlacer.get() && !BotPlacer.get() && !TopClimber.get() && BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
			
			double goingdownintake = m_leftStick.getRawAxis(2);
			double goingupclimber = m_rightStick.getRawAxis(3);
			Intake.set(ControlMode.PercentOutput, goingdownintake);	
			Climber.set(ControlMode.PercentOutput,goingupclimber);
			if(CanClimb == true) {
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_rightStick.getRawButton(1)){
				
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
			}
		
		while(!TopPlacer.get() && BotPlacer.get() && !TopClimber.get() && !BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
		    double goingupintake = m_leftStick.getRawAxis(3);
			double goingupclimber = m_rightStick.getRawAxis(3);
			double goingdownclimber = m_rightStick.getRawAxis(2);
			Intake.set(ControlMode.PercentOutput, -goingupintake);
			if(goingupclimber > goingdownclimber) {
				Climber.set(ControlMode.PercentOutput,goingupclimber);
			}
			else if(goingupclimber < goingdownclimber) {
				Climber.set(ControlMode.PercentOutput,-goingdownclimber);
			}
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
				
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
		}
		while(!TopPlacer.get() && BotPlacer.get() && TopClimber.get() && !BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
			
		    double goingupintake = m_leftStick.getRawAxis(3);
			double goingdownclimber = m_rightStick.getRawAxis(2);
			Intake.set(ControlMode.PercentOutput, -goingupintake);
			Climber.set(ControlMode.PercentOutput, -goingdownclimber);
			
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
				
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
		}
		while(!TopPlacer.get() && BotPlacer.get() && !TopClimber.get() && BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
		    double goingupintake = m_leftStick.getRawAxis(3);
			double goingupclimber = m_rightStick.getRawAxis(3);
			Intake.set(ControlMode.PercentOutput,-goingupintake);
			Climber.set(ControlMode.PercentOutput,goingupclimber);
			if(CanClimb == true) {
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
				
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
		}
		while(!TopPlacer.get() && !BotPlacer.get() && TopClimber.get() && !BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 12000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 6000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
			
			double goingupintake = m_leftStick.getRawAxis(3);
			double goingdownintake = m_leftStick.getRawAxis(2);
			double goingdownclimber = m_rightStick.getRawAxis(2);
			
			Climber.set(ControlMode.PercentOutput,-goingdownclimber);
			
			
			if(goingupintake > goingdownintake) {
				Intake.set(ControlMode.PercentOutput,-goingupintake);
			}
			else if(goingupintake < goingdownintake) {
				Intake.set(ControlMode.PercentOutput, goingdownintake);
			}
			
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
				
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
			}
		while(!TopPlacer.get() && !BotPlacer.get() && !TopClimber.get() && BotClimber.get()) {
			if(ClampPiston.getAll() == 8.0) {
				Clamp = true;
			}
			else if(ClampPiston.getAll() == 4.0) {
				Clamp = false;
			}
			SmartDashboard.putBoolean("GREEN MEANS CLAMPPED", Clamp);
			SmartDashboard.putNumber("Intake", (Intake.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("RightDrive", (RightFrontDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("LeftDrive", (-LeftBackDrive.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("angle", gyro.getAngle());
			SmartDashboard.putBoolean("TopIntake", TopPlacer.get());
			SmartDashboard.putBoolean("BotIntake", BotPlacer.get());
			SmartDashboard.putBoolean("TopClimber", TopClimber.get());
			SmartDashboard.putBoolean("BotClimber", BotClimber.get());
			if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-m_rightStick.getY(), 0.75*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) < 15000) {
				m_myRobot.arcadeDrive(m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
		if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))>Math.abs((m_leftStick.getY()+m_leftStick.getX())) && Intake.getSelectedSensorPosition(0) < 15000 ) {
				m_myRobot.arcadeDrive(-0.5*m_rightStick.getY(), 0.5*m_rightStick.getX());	
			}
			else if(Math.abs((m_rightStick.getY()+m_rightStick.getX()))<Math.abs(m_leftStick.getY()+m_leftStick.getX()) && Intake.getSelectedSensorPosition(0) > 15000) {
				m_myRobot.arcadeDrive(0.5*m_leftStick.getY(), 0.75*m_leftStick.getX());	
			}
			
			RightFrontDrive.setNeutralMode(NeutralMode.Coast);
			LeftBackDrive.setNeutralMode(NeutralMode.Coast);
			
			double goingupintake = m_leftStick.getRawAxis(3);
			double goingdownintake = m_leftStick.getRawAxis(2);
			double goingupclimber = m_rightStick.getRawAxis(3);
			Climber.set(ControlMode.PercentOutput,goingupclimber);
			
			if(goingupintake > goingdownintake) {
				Intake.set(ControlMode.PercentOutput,-goingupintake);
			}
			else if(goingupintake < goingdownintake) {
			Intake.set(ControlMode.PercentOutput,goingdownintake);
			}
			if(CanClimb == true) {
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_rightStick.getRawButton(1)){
				BackdrivePiston.set(DoubleSolenoid.Value.kForward);
				
			}
			if(m_rightStick.getRawButton(2)){
				BackdrivePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_rightStick.getRawButton(3)){
				CanClimb = true;
			}
			if(m_rightStick.getRawButton(4)){
				CanClimb = false;
			}
			if(m_leftStick.getRawButton(1)){
				RightIntake.set(.7);
				LeftIntake.set(.7);
			}
			if(m_leftStick.getRawButton(2)){
				RightIntake.set(-.7);
				LeftIntake.set(-.7);
			}
			if(!m_leftStick.getRawButton(1) && !m_leftStick.getRawButton(2) ){
				RightIntake.set(0);
				LeftIntake.set(0);
			}
			if(m_leftStick.getRawButton(3)){
				ClampPiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(4)){
				ClampPiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(5)){
				RotatePiston.set(DoubleSolenoid.Value.kReverse);
			}
			if(m_leftStick.getRawButton(6)){
				RotatePiston.set(DoubleSolenoid.Value.kForward);
			}
			if(m_leftStick.getRawButton(10)) {
				Intake.setSelectedSensorPosition(0, 0, 10);
				RightFrontDrive.setSelectedSensorPosition(0, 0, 10);
				LeftBackDrive.setSelectedSensorPosition(0, 0, 10);
				gyro.reset();
			}
			
			}
		
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
