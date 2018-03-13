/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Robot;

import Map.Map;
import Map.MapConstants;
import Robot.RobotConstants.DIRECTION;
import Robot.RobotConstants.MOVEMENT;
import Utils.CommMgr;
import Utils.MapDescriptor;
import java.lang.Math;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author denis
 */
public class Robot {
    private int posRow;
    private int posCol;
    private DIRECTION robotDir;
    private int speed;
    
    private int timeLimit;
    private int coverage;
    
    private final Sensor SRFrontOne;
    private final Sensor SRFrontTwo;
    private final Sensor SRFrontThree;
    private final Sensor SRRightOne;
    private final Sensor SRRightTwo;
    private final Sensor LRLeft; 
    
    private boolean touchedGoal;
    private final boolean isRealRobot;
    
    public Robot(int row, int col, boolean isRealRobot) {
        posRow = row;
        posCol = col;
        robotDir = RobotConstants.DEFAULT_START_DIR;
        speed = RobotConstants.SPEED;

        this.isRealRobot = isRealRobot;

        SRFrontOne = new Sensor(RobotConstants.SHORT_SENSOR_MIN, RobotConstants.SHORT_SENSOR_MAX, this.posRow - 1, this.posCol - 1, this.robotDir, "SRF1");
        SRFrontTwo = new Sensor(RobotConstants.SHORT_SENSOR_MIN, RobotConstants.SHORT_SENSOR_MAX, this.posRow - 1, this.posCol, this.robotDir, "SRF2");
        SRFrontThree = new Sensor(RobotConstants.SHORT_SENSOR_MIN, RobotConstants.SHORT_SENSOR_MAX, this.posRow - 1, this.posCol + 1, this.robotDir, "SRF3");
        SRRightOne = new Sensor(RobotConstants.SHORT_SENSOR_MIN, RobotConstants.SHORT_SENSOR_MAX, this.posRow - 1, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT), "SRR1");
        SRRightTwo = new Sensor(RobotConstants.SHORT_SENSOR_MIN, RobotConstants.SHORT_SENSOR_MAX, this.posRow + 1, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT), "SRR2");
        LRLeft = new Sensor(RobotConstants.LONG_SENSOR_MIN, RobotConstants.LONG_SENSOR_MAX, this.posRow - 1, this.posCol - 1, findNewDirection(MOVEMENT.LEFT), "LRL");
    }
    
    public void setRobotPos(int row, int col) {
        posRow = row;
        posCol = col;
    }

    public int getRobotPosRow() {
        return posRow;
    }
    
    public void setRobotPosRow(int row){
        this.posRow = row;
    }

    public int getRobotPosCol() {
        return posCol;
    }
    
    public void setRobotPosCol(int col){
        this.posCol = col;
    }

    public void setRobotDir(DIRECTION dir) {
        robotDir = dir;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public int getSpeed(){
        return this.speed;
    }
        public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getCoverage() {
        return coverage;
    }

    public void setCoverage(int coverage) {
        this.coverage = coverage;
    }

    public DIRECTION getRobotDir() {
        return robotDir;
    }

    public boolean getIsRealRobot() {
        return isRealRobot;
    }

    public void updateTouchedGoal() {
        if (this.getRobotPosRow() == MapConstants.GOAL_ROW && this.getRobotPosCol() == MapConstants.GOAL_COL)
            this.touchedGoal = true;
    }
    
    public boolean getTouchedGoal() {
        return this.touchedGoal;
    }

//    /**
//     * Takes in a MOVEMENT and moves the robot accordingly by changing its position and direction. Sends the movement
//     * if this.realBot is set.
//     */
//    
//    public void move(MOVEMENT m, boolean sendMoveToAndroid) {
//        if (!realRobot) {
//            // Emulate real movement by pausing execution.
//            try {
//                TimeUnit.MILLISECONDS.sleep(speed);
//            } catch (InterruptedException e) {
//                System.out.println("Something went wrong in Robot.move()!");
//            }
//        }
//
//        switch (m) {
//            case FORWARD:
//                switch (robotDir) {
//                    case NORTH:
//                        posRow++;
//                        break;
//                    case EAST:
//                        posCol++;
//                        break;
//                    case SOUTH:
//                        posRow--;
//                        break;
//                    case WEST:
//                        posCol--;
//                        break;
//                }
//                break;
//            case BACKWARD:
//                switch (robotDir) {
//                    case NORTH:
//                        posRow--;
//                        break;
//                    case EAST:
//                        posCol--;
//                        break;
//                    case SOUTH:
//                        posRow++;
//                        break;
//                    case WEST:
//                        posCol++;
//                        break;
//                }
//                break;
//            case RIGHT:
//            case LEFT:
//                robotDir = findNewDirection(m);
//                break;
//            case CALIBRATE:
//                break;
//            default:
//                System.out.println("Error in Robot.move()!");
//                break;
//        }
//
//        if (realRobot) sendMovement(m, sendMoveToAndroid);
//        else System.out.println("Move: " + MOVEMENT.print(m));
//
//        updateTouchedGoal();
//    }
//
//    /**
//     * Overloaded method that calls this.move(MOVEMENT m, boolean sendMoveToAndroid = true).
//     */
//    public void move(MOVEMENT m) {
//        this.move(m, true);
//    }
//
//    /**
//     * Sends a number instead of 'F' for multiple continuous forward movements.
//     */
//    public void moveForwardMultiple(int count) {
//        if (count == 1) {
//            move(MOVEMENT.FORWARD);
//        } else {
//            CommMgr comm = CommMgr.getCommMgr();
//            if (count == 10) {
//                comm.sendMsg("0", CommMgr.INSTRUCTIONS);
//            } else if (count < 10) {
//                comm.sendMsg(Integer.toString(count), CommMgr.INSTRUCTIONS);
//            }
//
//            switch (robotDir) {
//                case NORTH:
//                    posRow += count;
//                    break;
//                case EAST:
//                    posCol += count;
//                    break;
//                case SOUTH:
//                    posRow += count;
//                    break;
//                case WEST:
//                    posCol += count;
//                    break;
//            }
//
//            comm.sendMsg(this.getRobotPosRow() + "," + this.getRobotPosCol() + "," + DIRECTION.print(this.getRobotDir()), CommMgr.BOT_POS);
//        }
//    }
//
//    /**
//     * Uses the CommMgr to send the next movement to the robot.
//     */
//    private void sendMovement(MOVEMENT m, boolean sendMoveToAndroid) {
//        CommMgr comm = CommMgr.getCommMgr();
//        comm.sendMsg(MOVEMENT.print(m) + "", CommMgr.INSTRUCTIONS);
//        if (m != MOVEMENT.CALIBRATE && sendMoveToAndroid) {
//            comm.sendMsg(this.getRobotPosRow() + "," + this.getRobotPosCol() + "," + DIRECTION.print(this.getRobotDir()), CommMgr.BOT_POS);
//        }
//    }
//   

    /**
     * Sets the sensors' position and direction values according to the robot's current position and direction.
     */
    public void setSensors() {
        switch (robotDir) {
            case NORTH:
                SRFrontOne.setSensor(this.posRow - 1, this.posCol - 1, this.robotDir);
                SRFrontTwo.setSensor(this.posRow - 1, this.posCol, this.robotDir);
                SRFrontThree.setSensor(this.posRow - 1, this.posCol + 1, this.robotDir);
                SRRightOne.setSensor(this.posRow - 1, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT));
                SRRightTwo.setSensor(this.posRow + 1, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT));
                LRLeft.setSensor(this.posRow - 1, this.posCol - 1, findNewDirection(MOVEMENT.LEFT));
                break;
            case EAST:
                SRFrontOne.setSensor(this.posRow - 1, this.posCol + 1, this.robotDir);
                SRFrontTwo.setSensor(this.posRow, this.posCol + 1, this.robotDir);
                SRFrontThree.setSensor(this.posRow + 1, this.posCol + 1, this.robotDir);
                SRRightOne.setSensor(this.posRow + 1, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT));
                SRRightTwo.setSensor(this.posRow + 1, this.posCol - 1, findNewDirection(MOVEMENT.RIGHT));
                LRLeft.setSensor(this.posRow - 1, this.posCol + 1, findNewDirection(MOVEMENT.LEFT));
                break;
            case SOUTH:
                SRFrontOne.setSensor(this.posRow + 1, this.posCol + 1, this.robotDir);
                SRFrontTwo.setSensor(this.posRow + 1, this.posCol, this.robotDir);
                SRFrontThree.setSensor(this.posRow + 1, this.posCol - 1, this.robotDir);
                SRRightOne.setSensor(this.posRow + 1, this.posCol - 1, findNewDirection(MOVEMENT.RIGHT));
                SRRightTwo.setSensor(this.posRow - 1, this.posCol - 1, findNewDirection(MOVEMENT.RIGHT));
                LRLeft.setSensor(this.posRow + 1, this.posCol + 1, findNewDirection(MOVEMENT.LEFT));
                break;
            case WEST:
                SRFrontOne.setSensor(this.posRow + 1, this.posCol - 1, this.robotDir);
                SRFrontTwo.setSensor(this.posRow, this.posCol - 1, this.robotDir);
                SRFrontThree.setSensor(this.posRow - 1, this.posCol - 1, this.robotDir);
                SRRightOne.setSensor(this.posRow - 1, this.posCol - 1, findNewDirection(MOVEMENT.RIGHT));
                SRRightTwo.setSensor(this.posRow - 1, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT));
                LRLeft.setSensor(this.posRow + 1, this.posCol - 1, findNewDirection(MOVEMENT.LEFT));
                break;
        }
    }

    /**
     * Uses the current direction of the robot and the given movement to find the new direction of the robot.
     */
    public DIRECTION findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.RIGHT) {
            return DIRECTION.getNext(robotDir);
        } else {
            return DIRECTION.getPrevious(robotDir);
        }
    }

    /**
     * Calls the .sense() method of all the attached sensors and stores the received values in an integer array.
     *
     * @return [SRFrontLeft, SRFrontCenter, SRFrontRight, SRLeft, SRRight, LRLeft]
     */
    public int[] sense(Map explorationMap, Map realMap) {
        int[] result = new int[6];

        if (!isRealRobot) {
            result[0] = SRFrontOne.sense(explorationMap, realMap);
            result[1] = SRFrontTwo.sense(explorationMap, realMap);
            result[2] = SRFrontThree.sense(explorationMap, realMap);
            result[3] = SRRightOne.sense(explorationMap, realMap);
            result[4] = SRRightTwo.sense(explorationMap, realMap);
            result[5] = LRLeft.sense(explorationMap, realMap);
        } else {
            CommMgr comm = CommMgr.getCommMgr();
            //send command to arduino to receive sensor values
            comm.sendMsg("k", "INSTR");
            String msg = comm.recvMsg();
            String[] msgArr = msg.split(",");
            
            System.out.println("Receieved sensor readings from arduino: " + msg);
            result[0] = Integer.parseInt(msgArr[0]);
            result[1] = Integer.parseInt(msgArr[1]);
            result[2] = Integer.parseInt(msgArr[2]);
            result[3] = Integer.parseInt(msgArr[3]);
            result[4] = Integer.parseInt(msgArr[4]);
            result[5] = Integer.parseInt(msgArr[5]);

//            if (msgArr[0].equals(CommMgr.SENSOR_DATA)) {
//                //result[0] = ((Math.floor Integer.parseInt(msgArr[1].split("_")[1]) + 5) / 10.0) * 10;
//                result[0] = ((Integer.parseInt(msgArr[1].split("_")[1])+5)/10) * 10;
//                result[1] = ((Integer.parseInt(msgArr[1].split("_")[1])+5)/10) * 10;
//                result[2] = ((Integer.parseInt(msgArr[1].split("_")[1])+5)/10) * 10;
//                result[3] = ((Integer.parseInt(msgArr[1].split("_")[1])+5)/10) * 10;
//                result[4] = ((Integer.parseInt(msgArr[1].split("_")[1])+5)/10) * 10;
//                result[5] = ((Integer.parseInt(msgArr[1].split("_")[1])+5)/10) * 10;
//            }

            SRFrontOne.senseReal(explorationMap, result[0]);
            SRFrontTwo.senseReal(explorationMap, result[1]);
            SRFrontThree.senseReal(explorationMap, result[2]);
            SRRightOne.senseReal(explorationMap, result[3]);
            SRRightTwo.senseReal(explorationMap, result[4]);
            LRLeft.senseReal(explorationMap, result[5]);

            String[] mapStrings = MapDescriptor.generateMapDescriptor(explorationMap);
            comm.sendMsg("M,"+mapStrings[0] + "," + mapStrings[1], CommMgr.MAP_STRINGS);
        }
//        for(int i=0;i<6;i++){
//            System.out.println("Index "+ i + ": "+result[i]);
//        }

        return result;
    }
    
    

}
