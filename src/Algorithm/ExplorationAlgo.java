/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithm;

import Map.Grid;
import Map.Map;
import Map.MapConstants;
import Map.MapFrame;
import Map.MapPanel;
import Robot.ExploreController;
import Robot.Robot;
import Robot.RobotConstants;
import Robot.RobotConstants.DIRECTION;
import Robot.RobotConstants.MOVEMENT;
import Utils.CommMgr;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.SwingUtilities;
/**
 *
 * @author denis
 */
public class ExplorationAlgo {
    private final MapPanel exploredMap;
    private final Map realMap;
    private final Robot robot;
    //private MapPanel mapPanel;
    private ExploreController exploreController;
    private final int coverageLimit;
    private final int timeLimit;
    private int areaExplored;
    private long startTime;
    private long endTime;
    private int lastCalibrate;
    private boolean calibrationMode;
    
    private Timer timer;
    private Queue<TimerTask> tasks;
    
    private MOVEMENT prevMove;
    private final MapFrame mapFrame;
    private int calCount = 0;
    //private Boolean justCalibrated = false;
    
    public ExplorationAlgo(MapFrame mapFrame, Map realMap, Robot robot, int coverageLimit, int timeLimit){
        this.mapFrame = mapFrame;
        this.exploredMap = mapFrame.getMapPanel();
        this.realMap = realMap;
        this.robot = robot;
        this.coverageLimit = coverageLimit;
        this.timeLimit = timeLimit;
        
        this.timer = new Timer();
        this.tasks = new LinkedList<>();
        
        this.exploreController = new ExploreController(robot, mapFrame);
        this.exploreController.setFpathStart(false);
    }
    
    /**
     * Main method that is called to start the exploration.
     */
    public void runExploration() {
        
        if (robot.getIsRealRobot()) {
            System.out.println("Starting calibration...");
            //exploreController.move(MOVEMENT.CAL_COR, false);
            CommMgr.getCommMgr().sendMsg("o", "INSTR");
            if(CommMgr.getCommMgr().recvMsg().equals("Y")){
                CommMgr.getCommMgr().sendMsg("g", "INSTR");
                if(CommMgr.getCommMgr().recvMsg().equals("Y")){
                    exploreController.move(MOVEMENT.LEFT);
                }
            }
        }

        System.out.println("Starting exploration...");
        
        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);

//        if (robot.getIsRealRobot()) {
//            CommMgr.getCommMgr().sendMsg(null, CommMgr.BOT_START);
//        }

        areaExplored = calculateAreaExplored();
        System.out.println("Explored Area: " + areaExplored);

//        Thread thread = new Thread(this::evaluate);
//        thread.start();
        //explorationLoop(robot.getRobotPosRow(), robot.getRobotPosCol());
        evaluate();
    
    }
    
    private void evaluate() {
        // Sense
        senseAndRepaint();

//        if (!justCalibrated){
            if (robot.getIsRealRobot()){
                if (canCalibrateOnCorner(robot.getRobotDir())){
                    System.out.println("Can calibrate on the corner");
                    moveBot(MOVEMENT.CAL_COR);
                    //if(comm.recvMsg().equals("Y")){
                    calCount = 0;
                    //justCalibrated = true;
                    moveBot(MOVEMENT.LEFT);
                } else if (canCalibrateUsingSide(robot.getRobotDir()) && calCount >= 3){
                    System.out.println("Can reposition using side");
                    moveBot(MOVEMENT.CAL_SIDE);
                    calCount = 0;
                    //justCalibrated = true;
                }
                else{
                    calCount++;
                }
            }
 //       }

        areaExplored = calculateAreaExplored();
        System.out.println("Area explored: " + areaExplored);

        // Check for termination condition
//        if(areaExplored == 300){
//            System.out.println("Finished exploring all grids");
//            //goHome();
//            return;
//        }

        if(areaExplored > coverageLimit || System.currentTimeMillis() > endTime ){
            System.out.println("Exceeded time or area");
            //goHome();
            return;
        }

        if (robot.getRobotPosRow() == MapConstants.START_ROW && robot.getRobotPosCol() == MapConstants.START_COL) {
            if (areaExplored >= 100) {
                System.out.println("Went back to start pos");
                //goHome();
                return;
            }
        }
        // Schedule movement
        nextMove();
        
        
    }
    

    /**
     * Determines the next move for the robot and executes it accordingly.
     */
    private void nextMove() {        
        if(prevMove != null && (prevMove == MOVEMENT.RIGHT || prevMove == MOVEMENT.LEFT)){
           if (lookForward()) moveBot(MOVEMENT.FORWARD);
           senseAndRepaint();
        }
        
        if (lookRight()) {
            prevMove = MOVEMENT.RIGHT;
            if(robot.getIsRealRobot()){
                moveBot(MOVEMENT.RIGHT);
            } else{
                scheduleMoveBot(MOVEMENT.RIGHT);
            }
        } else if (lookForward()) {
            prevMove = MOVEMENT.FORWARD;
            if(robot.getIsRealRobot()){
                moveBot(MOVEMENT.FORWARD);
            } else{
                scheduleMoveBot(MOVEMENT.FORWARD);
            }
        } else if (lookLeft()) {
            prevMove = MOVEMENT.LEFT;
            if(robot.getIsRealRobot()){
                moveBot(MOVEMENT.LEFT);
            } else{
                scheduleMoveBot(MOVEMENT.LEFT);
            }
        } else {
            prevMove = MOVEMENT.RIGHT;
            if(robot.getIsRealRobot()){
                moveBot(MOVEMENT.RIGHT);
            } else{
                scheduleMoveBot(MOVEMENT.RIGHT);
            }
        }
        if(robot.getIsRealRobot()){
            evaluate();
        }
    }

    /**
     * Returns true if the right side of the robot is free to move into.
     */
    private boolean lookRight() {
        switch (robot.getRobotDir()) {
            case NORTH:
                return eastFree();
            case EAST:
                return southFree();
            case SOUTH:
                return westFree();
            case WEST:
                return northFree();
        }
        return false;
    }

    /**
     * Returns true if the robot is free to move forward.
     */
    private boolean lookForward() {
        switch (robot.getRobotDir()) {
            case NORTH:
                return northFree();
            case EAST:
                return eastFree();
            case SOUTH:
                return southFree();
            case WEST:
                return westFree();
        }
        return false;
    }

    /**
     * * Returns true if the left side of the robot is free to move into.
     */
    private boolean lookLeft() {
        switch (robot.getRobotDir()) {
            case NORTH:
                return westFree();
            case EAST:
                return northFree();
            case SOUTH:
                return eastFree();
            case WEST:
                return southFree();
        }
        return false;
    }

    /**
     * Returns true if the robot can move to the north cell.
     */
    private boolean northFree() {
        int robotRow = robot.getRobotPosRow();
        int robotCol = robot.getRobotPosCol();
        return (isExploredNotObstacle(robotRow - 1, robotCol - 1) && isExploredAndFree(robotRow - 1, robotCol) && isExploredNotObstacle(robotRow - 1, robotCol + 1));
    }

    /**
     * Returns true if the robot can move to the east cell.
     */
    private boolean eastFree() {
        int robotRow = robot.getRobotPosRow();
        int robotCol = robot.getRobotPosCol();
        return (isExploredNotObstacle(robotRow - 1, robotCol + 1) && isExploredAndFree(robotRow, robotCol + 1) && isExploredNotObstacle(robotRow + 1, robotCol + 1));
    }

    /**
     * Returns true if the robot can move to the south cell.
     */
    private boolean southFree() {
        int robotRow = robot.getRobotPosRow();
        int robotCol = robot.getRobotPosCol();
        return (isExploredNotObstacle(robotRow + 1, robotCol - 1) && isExploredAndFree(robotRow + 1, robotCol) && isExploredNotObstacle(robotRow + 1, robotCol + 1));
    }

    /**
     * Returns true if the robot can move to the west cell.
     */
    private boolean westFree() {
        int robotRow = robot.getRobotPosRow();
        int robotCol = robot.getRobotPosCol();
        return (isExploredNotObstacle(robotRow - 1, robotCol - 1) && isExploredAndFree(robotRow, robotCol - 1) && isExploredNotObstacle(robotRow + 1, robotCol - 1));
    }

    /**
     * Returns the robot to START after exploration and points the bot northwards.
     */
    private void goHome() {
        System.out.println("in goHome()");
        if (!robot.getTouchedGoal() && coverageLimit == 300 && timeLimit == 3600) {
            System.out.println("1");
            FastestPathAlgo goToGoal = new FastestPathAlgo(robot, mapFrame);
            goToGoal.runFastestPath(MapConstants.GOAL_ROW, MapConstants.GOAL_COL);
        }
        if(!(robot.getRobotPosRow() == RobotConstants.DEFAULT_START_ROW && robot.getRobotPosCol() == RobotConstants.DEFAULT_START_COL)){
            System.out.println("2");
            FastestPathAlgo returnToStart = new FastestPathAlgo(robot, mapFrame);
            returnToStart.runFastestPath(MapConstants.START_ROW, MapConstants.START_COL);
        }

        System.out.println("Exploration complete!");
        areaExplored = calculateAreaExplored();
        System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

//        if (robot.getIsRealRobot()) {
//            turnBotDirection(DIRECTION.WEST);
//            moveBot(MOVEMENT.CAL_SIDE);
//            turnBotDirection(DIRECTION.SOUTH);
//            moveBot(MOVEMENT.CAL_SIDE);
//            turnBotDirection(DIRECTION.WEST);
//            moveBot(MOVEMENT.CAL_SIDE);
//        }
    }

    /**
     * Returns true for cells that are explored and not obstacles.
     */
    private boolean isExploredNotObstacle(int r, int c) {
        //exploredMap.getMap().printMap();
        if (exploredMap.getMap().checkValidCoordinates(r, c)) {
            Grid tmp = exploredMap.getMap().getGrid(r, c);
            return (tmp.getIsExplored() && !tmp.getIsObstacle());
        }
        return false;
    }

    /**
     * Returns true for cells that are explored, not virtual walls and not obstacles.
     */
    private boolean isExploredAndFree(int r, int c) {
        //exploredMap.getMap().printMap();
        if (exploredMap.getMap().checkValidCoordinates(r, c)) {
            Grid b = exploredMap.getMap().getGrid(r, c);
            return (b.getIsExplored() && !b.getIsVirtualWall() && !b.getIsObstacle());
        }
        return false;
    }

    /**
     * Returns the number of cells explored in the grid.
     */
    private int calculateAreaExplored() {
        int result = 0;
        for (int r = 0; r < MapConstants.MAP_ROWS; r++) {
            for (int c = 0; c < MapConstants.MAP_COLS; c++) {
                if (exploredMap.getMap().getGrid(r, c).getIsExplored()) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Moves the bot, repaints the map and calls senseAndRepaint().
     */
    public void moveBot(MOVEMENT m) {
        
        System.out.println("Movement scheduled: " + m.print(m));        
        exploreController.move(m);
        exploredMap.repaint();
        //SwingUtilities.invokeLater(exploredMap::repaint);
        System.out.println("Repainted robot position");
        
//        if(m == MOVEMENT.CAL_SIDE || m == MOVEMENT.CAL_COR){
//            return;
//        }
                
//        if (robot.getIsRealRobot()){
//            if (canCalibrateOnCorner(robot.getRobotDir())){
//                System.out.println("Can calibrate on the corner");
//                moveBot(MOVEMENT.CAL_COR);
//                //if(comm.recvMsg().equals("Y")){
//                calCount = 0;
//                moveBot(MOVEMENT.LEFT);
//            } else if (canCalibrateUsingSide(robot.getRobotDir()) && calCount >= 3){
//                System.out.println("Can reposition using side");
//                moveBot(MOVEMENT.CAL_SIDE);
//                calCount = 0;
//            }
//            else{
//                calCount++;
//            }
//        }
    }
    
    public void scheduleMoveBot(MOVEMENT m){
        schedule(() -> {
            moveBot(m);
        });
    }

    /**
     * Sets the bot's sensors, processes the sensor data and repaints the map.
     */
    private void senseAndRepaint() {
        robot.setSensors();

        robot.sense(exploredMap.getMap(), realMap);
        
        exploredMap.repaint();
        //SwingUtilities.invokeLater(exploredMap::repaint);

    }

    /**
     * Checks if the robot can calibrate at its current position given a direction.
     */
//    private boolean canCalibrateOnTheSpot(DIRECTION botDir) {
//        int row = robot.getRobotPosRow();
//        int col = robot.getRobotPosCol();
//
//        switch (botDir) {
//            case NORTH:
//                return exploredMap.getMap().getIsObstacleOrWall(row - 2, col - 1) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col + 1);
//            case EAST:
//                return exploredMap.getMap().getIsObstacleOrWall(row - 1, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row + 1, col + 2);
//            case SOUTH:
//                return exploredMap.getMap().getIsObstacleOrWall(row + 2, col - 1) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col + 1);
//            case WEST:
//                return exploredMap.getMap().getIsObstacleOrWall(row + 1, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row - 1, col - 2);
//        }
//
//        return false;
//    }
    
    private boolean canCalibrateOnCorner(DIRECTION botDir) {
        int row = robot.getRobotPosRow();
        int col = robot.getRobotPosCol();

        switch (botDir) {
            case NORTH:
                return exploredMap.getMap().getIsObstacleOrWall(row - 2, col - 1) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col + 1) && exploredMap.getMap().getIsObstacleOrWall(row - 1, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row + 1, col + 2);
            case EAST:
                return exploredMap.getMap().getIsObstacleOrWall(row - 1, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row + 1, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col + 1) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col)&& exploredMap.getMap().getIsObstacleOrWall(row + 2, col - 1);
            case SOUTH:
                return exploredMap.getMap().getIsObstacleOrWall(row + 2, col + 1) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col - 1) && exploredMap.getMap().getIsObstacleOrWall(row + 1, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row - 1, col - 2);
            case WEST:
                return exploredMap.getMap().getIsObstacleOrWall(row + 1, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row - 1, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col - 1) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col + 1);
        }
        System.out.println("Cannot calibrate on corner");
        return false;
    }
    
    private boolean canCalibrateUsingFront(DIRECTION botDir) {
        int row = robot.getRobotPosRow();
        int col = robot.getRobotPosCol();

        switch (botDir) {
            case NORTH:
                return exploredMap.getMap().getIsObstacleOrWall(row - 2, col - 1) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col + 1);
            case EAST:
                return exploredMap.getMap().getIsObstacleOrWall(row - 1, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row + 1, col + 2);
            case SOUTH:
                return exploredMap.getMap().getIsObstacleOrWall(row + 2, col + 1) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col - 1);
            case WEST:
                return exploredMap.getMap().getIsObstacleOrWall(row + 1, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row - 1, col - 2);
        }
        System.out.println("Cannot calibrate on the spot using right side");
        return false;
    }
    
        private boolean canCalibrateUsingSide(DIRECTION botDir) {
        int row = robot.getRobotPosRow();
        int col = robot.getRobotPosCol();

        switch (botDir) {
            case NORTH:
                return exploredMap.getMap().getIsObstacleOrWall(row - 1, col + 2) && exploredMap.getMap().getIsObstacleOrWall(row, col + 2) &&  exploredMap.getMap().getIsObstacleOrWall(row + 1, col + 2);
            case EAST:
                return exploredMap.getMap().getIsObstacleOrWall(row + 2, col + 1) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col) && exploredMap.getMap().getIsObstacleOrWall(row + 2, col - 1);
            case SOUTH:
                return exploredMap.getMap().getIsObstacleOrWall(row + 1, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row, col - 2) && exploredMap.getMap().getIsObstacleOrWall(row - 1, col - 2);
            case WEST:
                return exploredMap.getMap().getIsObstacleOrWall(row - 2, col - 1) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col) && exploredMap.getMap().getIsObstacleOrWall(row - 2, col + 1);
        }
        System.out.println("Cannot calibrate on the spot using right side");
        return false;
    }

    /**
     * Returns a possible direction for robot calibration or null, otherwise.
     */
    private DIRECTION getCalibrationDirection() {
        DIRECTION origDir = robot.getRobotDir();
        DIRECTION dirToCheck;

        dirToCheck = DIRECTION.getNext(origDir);                    // right turn
        if (canCalibrateUsingSide(dirToCheck) || canCalibrateUsingFront(dirToCheck)) return dirToCheck;

        dirToCheck = DIRECTION.getPrevious(origDir);                // left turn
        if (canCalibrateUsingSide(dirToCheck) || canCalibrateUsingFront(dirToCheck)) return dirToCheck;

        dirToCheck = DIRECTION.getPrevious(dirToCheck);             // u turn
        if (canCalibrateUsingSide(dirToCheck) || canCalibrateUsingFront(dirToCheck)) return dirToCheck;

        return null;
    }

    /**
     * Turns the bot in the needed direction and sends the CALIBRATE movement. Once calibrated, the bot is turned back
     * to its original direction.
     */
    private void calibrateBot(DIRECTION targetDir) {
        DIRECTION origDir = robot.getRobotDir();

        turnBotDirection(targetDir);
        moveBot(MOVEMENT.CAL_SIDE);
        turnBotDirection(origDir);
    }

    /**
     * Turns the robot to the required direction.
     */
    private void turnBotDirection(DIRECTION targetDir) {
        int numOfTurn = Math.abs(robot.getRobotDir().ordinal() - targetDir.ordinal());
        if (numOfTurn > 2) numOfTurn = numOfTurn % 2;
        System.out.println("Number of turns to turn to North: "+ numOfTurn);

        if (numOfTurn == 1) {
            if (DIRECTION.getNext(robot.getRobotDir()) == targetDir) {
                moveBot(MOVEMENT.RIGHT);
            } else {
                moveBot(MOVEMENT.LEFT);
            }
        } else if (numOfTurn == 2) {
            moveBot(MOVEMENT.RIGHT);
            moveBot(MOVEMENT.RIGHT);
        }
    }
    
    private class RunnableTask extends TimerTask {

        private final Runnable runnable;

        public RunnableTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            TimerTask task = tasks.poll();
            runnable.run();
            evaluate();
        }

    }
    
    private void schedule(Runnable run) {
        tasks.offer(new RunnableTask(run));
        
        if(tasks.size() == 1)
            timer.schedule(tasks.peek(), 100);
    }
}
