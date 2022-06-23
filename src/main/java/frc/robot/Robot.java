// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.Constants.AUTO.DISTANGLE;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  private Command m_autonomousCommand;
  private XboxController xbox = new XboxController(0);
  private double fwd, str, rcw, rcwX, rcwY, inverseTanAngleOG = 0;
  private MkSwerveTrain train = MkSwerveTrain.getInstance();
  private boolean bbutton, ybutton, pov = false;
  private SerialPort arduino;
  private Timer timer;
  private String keyIn = "";
  private boolean accessible = false;
  private String bullshit = "30937C22";
  private boolean in;

  private boolean povToggled = false;
  private double povValue = 0;
  @Override
  public void robotInit() {
  //  try{
    //  arduino = new SerialPort(9600, "/dev/ttyACM0", SerialPort.Port.kUSB, 8, SerialPort.Parity.kNone, SerialPort.StopBits.kOne);
    //  System.out.println("Connected on usb port one!");
    //}
    //catch(Exception e)
    //{
      //System.out.println("Failed to connect on usb port one, trying usb port two");
      try
      {
        arduino = new SerialPort(9600, "/dev/ttyACM0", SerialPort.Port.kUSB, 8, SerialPort.Parity.kNone, SerialPort.StopBits.kOne);
        //arduino = new SerialPort(9600, SerialPort.Port.kUSB);
        System.out.println("Connected on usb port zero!");
        in = true;
      }
      catch(Exception e1)
      {
        System.out.println("Failed to connect on usb port two, failed all usb ports. Is your Ardunio plugged in?");
        in = false;
      }
   // }
  
  timer = new Timer();
  timer.start();

    train.startTrain();
    navx.getInstance().reset();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    
    train.startTrain();
    navx.getInstance().reset();
    if (m_autonomousCommand != null) {
     // SmartDashboard.putBoolean("yuy", true);
      m_autonomousCommand.schedule();
    }
    //SmartDashboard.putNumber("stupid dumbass", DriverStation.getMatchTime());
  }

  @Override
  public void autonomousPeriodic() {
    train.updateSwerve();
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    train.startTrain();
    navx.getInstance().reset();
    accessible = false;
    keyIn = "";
 // }
  }
  
  @Override
  public void teleopPeriodic() {
   // keyIn = arduino.readString();//.substring(10, 24);
    /*if(keyIn.length() > 12)
    {
      //substring because idk why but arduino reads number but when put into a string things get funky
      keyIn = keyIn.substring(0,12);
    }
    */
    if(in)
    {
      if(timer.get() > 2)
      {
  
          keyIn = arduino.readString();//.substring(10, 24);
        
        SmartDashboard.putString("string", keyIn);
        //SmartDashboard.putString("string", keyIn);

        //System.out.println(keyIn);
        //System.out.println(arduino.readString());
        System.out.println(keyIn);
        
        //System.out.println(keyIn.toCharArray());
        //arduino.readString();
        timer.reset();
        //System.out.println(arduino.toString());
      }

    SmartDashboard.putBoolean("acess", accessible);//accessible = true;

    if(keyIn.equals(bullshit))
    {
      accessible = true;
    }
  
  
  

      SmartDashboard.putBoolean("your uin", true);
  }
    train.updateSwerve();
    
    fwd = (xbox.getRawAxis(1) - 0.1) / (1 - 0.1);
    str = (xbox.getRawAxis(0) - 0.1) / (1 - 0.1);
    rcw = (xbox.getRawAxis(5) - 0.1) / (1 - 0.1);
    
    rcwY = rcw;
    rcwX =  (xbox.getRawAxis(4) - 0.1) / (1 - 0.1);

    bbutton = xbox.getBButton();
    ybutton = xbox.getYButton();
    pov = xbox.getPOV() != -1;

    //if pov toggle
    if(pov)
    {
      povToggled = true;
      povValue = xbox.getPOV();
    }

    inverseTanAngleOG = (((((( Math.toDegrees(Math.atan(rcwY/rcwX))+360 ))+ (MathFormulas.signumV4(rcwX)))%360) - MathFormulas.signumAngleEdition(rcwX,rcwY))+360)%360;

      if(Math.abs(xbox.getRawAxis(1)) < 0.1)
      {
        fwd = 0;
      }
      if(Math.abs(xbox.getRawAxis(0)) < 0.1)
      {
        str = 0;
      }
      
      if(xbox.getAButton())
      {
        navx.getInstance().reset();

        //if pov toggle

          povValue = 0;
        
      }


      
      //"else" if not toggle
      if(Math.abs(xbox.getRawAxis(5)) >= 0.1 || Math.abs(xbox.getRawAxis(4)) >= 0.1)
      {
//pov toggle
          povToggled = false;
  

        //TODO                     why the FUCK did java make mod (%) stupidly >:(
        rcw = train.moveToAngy((inverseTanAngleOG + 270) % 360);
        SmartDashboard.putNumber("inverseTanAngleOG with the 90", (inverseTanAngleOG + 270) % 360);
      }
      //povtoggle instead of pov for pov toggle and after "else" (when not toggle)
      else if(povToggled)
      {
        rcw = train.moveToAngy((povValue+180)% 360);
      }
      

      if(!povToggled && !bbutton && !ybutton && Math.abs(xbox.getRawAxis(5)) < 0.1 && Math.abs(xbox.getRawAxis(4)) < 0.1)
      {
        rcw = 0;
      }
      if(Math.abs(xbox.getRawAxis(5)) < 0.1)
      {
        rcwY = 0;
      }
      if(Math.abs(xbox.getRawAxis(4)) < 0.1)
      {
        rcwX = 0;
      }
      //penis

      if(fwd != 0 || str != 0 || rcw != 0)
      {
        //weird negative cuz robot is weird. should be negative fwd positive str rcw
        train.etherSwerve(fwd/5, -str/5, rcw, ControlMode.PercentOutput); //+,-,+
        //mDrive.updateDriveDriveRaw();
      }
      else
      {
        train.stopEverything();
      }
      //SmartDashboard.putNumber("angleplus180", ((Math.toDegrees(Math.atan(fwd/str))+180))%(360*MathFormulas.signumV2(str)));
     //SmartDashboard.putNumber("angleminus180", ((Math.toDegrees(Math.atan(fwd/str))-180))%(360*MathFormulas.signumV2(str))); 
     SmartDashboard.putNumber("doesthiswork", inverseTanAngleOG);

     SmartDashboard.putNumber("rcwrobotperiod", rcw);
    SmartDashboard.putBoolean("pov", pov);
    SmartDashboard.putBoolean("povtoggled", povToggled);
   /* else {
      SmartDashboard.putBoolean("your uin", false);
      train.stopEverything();
    }*/
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    train.startTrain();
    train.stopEverything();
    SmartDashboard.putNumber("anglglgl", DISTANGLE.angle);
    SmartDashboard.putNumber("distttt", DISTANGLE.distance);
    SmartDashboard.putNumber("radi", MathFormulas.calculateCircleRadius(50, 10));
  }

  @Override
  public void testPeriodic() 
  {}

  
}
