/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Robot;

import Map.ControlPanel;
import Map.Map;
import Map.MapFrame;
import Map.MapPanel;
import Utils.CommMgr;
import Utils.MapDescriptor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author denis
 */


//explorationController only need the exploredMap
public class ExploreController {
    Robot robot;
    //Map map;
    MapPanel mapPanel;
    Boolean isRealRobot;
    MapDescriptor md;
    ControlPanel c_panel;
    MapFrame mapFrame;
    Boolean fpathStart = false;
    
    public ExploreController(Robot robot, MapFrame mapFrame) {
        this.mapFrame = mapFrame;
        this.robot = robot;
        //this.map = map;
        this.mapPanel = mapFrame.getMapPanel();
        this.md = new MapDescriptor();
        this.c_panel = mapFrame.getControlPanel();
        this.isRealRobot = robot.getIsRealRobot();
    }
    
    public void setFpathStart(Boolean val){
        this.fpathStart = val;
    }
    /**
     * Takes in a MOVEMENT and moves the robot accordingly by changing its position and direction. Sends the movement
     * if this.realBot is set.
     */
    public void move(RobotConstants.MOVEMENT m, boolean sendMoveToAndroid) {
        String res = new String("N");

        if (!isRealRobot) {
            //
            //System.out.println("is not real robot");
            // Emulate real movement by pausing execution.
            try {
                TimeUnit.MILLISECONDS.sleep(robot.getSpeed());
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        } else{
            res = sendAndReceiveMovement(m);
            System.out.println("Sent move to arduino: " + RobotConstants.MOVEMENT.print(m));
        }
                
        if(!isRealRobot || (isRealRobot && res.equals("Y") && m != RobotConstants.MOVEMENT.CAL_SIDE && m != RobotConstants.MOVEMENT.CAL_COR)){

            System.out.println("Updating UI move: " + RobotConstants.MOVEMENT.print(m));

            switch (m) {
                case FORWARD:
                    switch (robot.getRobotDir()) {
                        case NORTH:
                            robot.setRobotPosRow(robot.getRobotPosRow() - 1);
                            break;
                        case EAST:
                            robot.setRobotPosCol(robot.getRobotPosCol() + 1);
                            break;
                        case SOUTH:
                            robot.setRobotPosRow(robot.getRobotPosRow() + 1);
                            break;
                        case WEST:
                            robot.setRobotPosCol(robot.getRobotPosCol() - 1);
                            break;
                    }
                    break;
                case BACKWARD:
                    switch (robot.getRobotDir()) {
                        case NORTH:
                            robot.setRobotPosRow(robot.getRobotPosRow() + 1);
                            break;
                        case EAST:
                            robot.setRobotPosCol(robot.getRobotPosCol() - 1);
                            break;
                        case SOUTH:
                            robot.setRobotPosRow(robot.getRobotPosRow() - 1);
                            break;
                        case WEST:
                            robot.setRobotPosCol(robot.getRobotPosCol() + 1);
                            break;
                    }
                    break;
                case RIGHT:
                case LEFT:
                    robot.setRobotDir(findNewDirection(m));
                    break;
                case CAL_COR:
                case CAL_SIDE:
                    break;
                default:
                    System.out.println("Error in Robot.move()!");
                    break;
            }

            if (isRealRobot) {
                sendMovement(m, sendMoveToAndroid);
            }
            robot.updateTouchedGoal();
        }
    }

    /**
     * Overloaded method that calls this.move(MOVEMENT m, boolean sendMoveToAndroid = true).
     */
    public void move(RobotConstants.MOVEMENT m) {
        this.move(m, true);

    }

    /**
     * Sends a number instead of 'F' for multiple continuous forward movements.
     */
    public void moveForwardMultiple(int count) {
        if (count == 1) {
            move(RobotConstants.MOVEMENT.FORWARD);
        } else {
            CommMgr comm = CommMgr.getCommMgr();
            if (count == 10) {
                comm.sendMsg("0", CommMgr.INSTRUCTIONS);
            } else if (count < 10) {
                comm.sendMsg(Integer.toString(count), CommMgr.INSTRUCTIONS);
            }

            switch (robot.getRobotDir()) {
                case NORTH:
                    robot.setRobotPosRow(robot.getRobotPosRow() - count);
                    break;
                case EAST:
                    robot.setRobotPosCol(robot.getRobotPosCol() + count);
                    break;
                case SOUTH:
                    robot.setRobotPosRow(robot.getRobotPosRow() + count);
                    break;
                case WEST:
                    robot.setRobotPosCol(robot.getRobotPosCol() - count);
                    break;
            }

            comm.sendMsg(robot.getRobotPosRow() + "," + robot.getRobotPosCol() + "," + RobotConstants.DIRECTION.print(robot.getRobotDir()), CommMgr.BOT_POS);
        }
    }

    /**
     * Uses the CommMgr to send the next movement to the robot. (arduino and android)
     */
    private void sendMovement(RobotConstants.MOVEMENT m, boolean sendMoveToAndroid) {
        CommMgr comm = CommMgr.getCommMgr();
        if (m != RobotConstants.MOVEMENT.CAL_SIDE && sendMoveToAndroid) {
            //String[] mapdes  = md.generateMapDescriptor(mapPanel.getMap());
            comm.sendMsg("R," + robot.getRobotPosRow() + "," + robot.getRobotPosCol() + "," + RobotConstants.DIRECTION.print(robot.getRobotDir()), CommMgr.BOT_POS);
        }
    }
    
    private String sendAndReceiveMovement(RobotConstants.MOVEMENT m) {
        String status = new String();
        CommMgr comm = CommMgr.getCommMgr();
        if(!fpathStart){
            comm.sendMsg(RobotConstants.MOVEMENT.print(m) + "", CommMgr.INSTRUCTIONS);
            status = comm.recvMsg(); //tell arduino to give msg (Y)
        }
        return status;
    }


    /**
     * Sets the sensors' position and direction values according to the robot's current position and direction.
     */
    public void updateSensors() {
        robot.setSensors();
    }

    /**
     * Uses the current direction of the robot and the given movement to find the new direction of the robot.
     */
    private RobotConstants.DIRECTION findNewDirection(RobotConstants.MOVEMENT m) {
        //System.out.println("findNewDirection() : " + robot.findNewDirection(m));
        return robot.findNewDirection(m);
    }

    /**
     * Calls the .sense() method of all the attached sensors and stores the received values in an integer array.
     *
     * @return [SRFrontLeft, SRFrontCenter, SRFrontRight, SRLeft, SRRight, LRLeft]
     */
    public int[] sense(Map explorationMap, Map realMap) {
        return robot.sense(explorationMap, realMap);
    }
    
    
    public void Explore(){
        move(RobotConstants.MOVEMENT.FORWARD); 
        move(RobotConstants.MOVEMENT.LEFT);
        
    }
}
