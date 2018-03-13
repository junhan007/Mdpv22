/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithm;

import static Robot.RobotConstants.INFINITE_COST;
import static Robot.RobotConstants.DEFAULT_START_COL;
import static Robot.RobotConstants.DEFAULT_START_ROW;
import static Robot.RobotConstants.DEFAULT_START_DIR;
import Robot.RobotConstants.DIRECTION;
import Robot.RobotConstants.MOVEMENT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import Map.Grid;
import Map.MapConstants;
import static Map.MapConstants.MAP_COLS;
import static Map.MapConstants.MAP_ROWS;
import Map.MapPanel;
import Robot.Robot;
import Robot.RobotConstants;
import Algorithm.ExplorationAlgo;
import Map.MapFrame;
import Robot.ExploreController;
import Utils.CommMgr;

/**
 *
 * @author junha
 */
public class FastestPathAlgo {

    private ArrayList<Grid> toVisit;
    private ArrayList<Grid> visited;
    private HashMap<Grid, Grid> parents;
    private Grid current;
    private Grid[] neighbors;
    private DIRECTION direction;
    private double[][] gCosts;
    private Robot robot;
    private Grid[][] explored_grid;
    private MapPanel panel;
    private int loopCount;
    Timer timer;
    Queue<TimerTask> tasks;
    boolean FastestMode;
    boolean WaypointReached = false;
    ExploreController ec;
    
    String movements = new String();
    String convert = new String();
    
   

    public void setFastestMode(boolean FastestMode) {
        this.FastestMode = FastestMode;
    }

    public FastestPathAlgo(Robot robot, MapFrame mapFrame) {

        initObject(robot, mapFrame.getMapPanel());
        this.timer = new Timer();
        this.tasks = new LinkedList<>();
        this.FastestMode = true;
        this.ec = new ExploreController(robot, mapFrame);
        this.ec.setFpathStart(true);
    }

    public FastestPathAlgo(Robot robot, MapFrame mapFrame, int startRow, int startCol, DIRECTION enddir) {
        initObject(robot, mapFrame.getMapPanel(), startRow, startCol, enddir);
        this.timer = new Timer();
        this.tasks = new LinkedList<>();
        this.FastestMode = true;
        this.ec = new ExploreController(robot, mapFrame);
        this.ec.setFpathStart(true);
    }

    private void initObject(Robot robot, MapPanel panel) {
        this.robot = robot;
        this.explored_grid = panel.getMap().getAllGrid();
        this.panel = panel;
        this.toVisit = new ArrayList<>();
        this.visited = new ArrayList<>();
        this.parents = new HashMap<>();
        this.neighbors = new Grid[4];
        this.current = panel.getMap().getGrid(robot.getRobotPosRow(), robot.getRobotPosCol());

        this.direction = robot.getRobotDir();
        this.gCosts = new double[MAP_ROWS][MAP_COLS];

        for (int y = 0; y < MAP_ROWS; y++) {
            for (int x = 0; x < MAP_COLS; x++) {
                Grid grid = panel.getMap().getGrid(y, x);
                if (!canBeVisited(grid)) {
                    gCosts[y][x] = INFINITE_COST;
                } else {
                    gCosts[y][x] = 0;
                }
            }
        }
        toVisit.add(current);

        gCosts[robot.getRobotPosRow()][robot.getRobotPosCol()] = 0;
        this.loopCount = 0;

    }

    private void initObject(Robot robot, MapPanel panel, int startRow, int startCol, DIRECTION enddir) {
        this.robot = robot;
        this.explored_grid = panel.getMap().getAllGrid();
        this.panel = panel;
        this.toVisit = new ArrayList<>();
        this.visited = new ArrayList<>();
        this.parents = new HashMap<>();
        this.neighbors = new Grid[4];
        this.current = panel.getMap().getGrid(startRow, startCol);

        this.direction = enddir;
        this.gCosts = new double[MAP_ROWS][MAP_COLS];

        for (int y = 0; y < MAP_ROWS; y++) {
            for (int x = 0; x < MAP_COLS; x++) {
                Grid grid = panel.getMap().getGrid(y, x);
                if (!canBeVisited(grid)) {
                    gCosts[y][x] = INFINITE_COST;
                } else {
                    gCosts[y][x] = 0;
                }
            }
        }
        toVisit.add(current);

        gCosts[robot.getRobotPosRow()][robot.getRobotPosCol()] = 0;
        this.loopCount = 0;

    }

    private boolean canBeVisited(Grid c) {
        return (c.getIsExplored() && !c.getIsObstacle() && !c.getIsVirtualWall());
    }

    private Grid minimumCostCell(int goalRow, int getCol) {
        int size = toVisit.size();
        double minCost = INFINITE_COST;
        Grid result = null;

        for (int i = size - 1; i >= 0; i--) {
            double gCost = gCosts[toVisit.get(i).getRow()][toVisit.get(i).getCol()];
            double cost = gCost + costH(toVisit.get(i), goalRow, getCol);
            if (cost < minCost) {
                minCost = cost;
                result = toVisit.get(i);
            }

        }
        return result;
    }

    private double costH(Grid b, int goalRow, int goalCol) {
        // Heuristic: The no. of moves will be equal to the difference in the row and column values.
        double movementCost = (Math.abs(goalCol - b.getCol()) + Math.abs(goalRow - b.getRow())) * RobotConstants.MOVE_COST;

        if (movementCost == 0) {
            return 0;
        }

        // Heuristic: If b is not in the same row or column, one turn will be needed.
        double turnCost = 0;
        if (goalCol - b.getCol() != 0 || goalRow - b.getRow() != 0) {
            turnCost = RobotConstants.TURN_COST;
        }

        return movementCost + turnCost;
    }

    private DIRECTION getTargetDir(int botR, int botC, DIRECTION botDir, Grid target) {
        if (botC - target.getCol() > 0) {
            return DIRECTION.WEST;
        } else if (target.getCol() - botC > 0) {
            return DIRECTION.EAST;
        } else {
            if (botR - target.getRow() > 0) {
                return DIRECTION.NORTH;
            } else if (target.getRow() - botR > 0) {
                return DIRECTION.SOUTH;
            } else {
                return botDir;
            }
        }
    }

    private double getTurnCost(DIRECTION a, DIRECTION b) {
        int numOfTurn = Math.abs(a.ordinal() - b.ordinal());
        if (numOfTurn > 2) {
            numOfTurn = numOfTurn % 2;
        }
        return (numOfTurn * RobotConstants.TURN_COST);
    }

    public String runFastestPath(int goalRow, int goalCol) {
        System.out.println("Calculating fastest path from (" + current.getRow() + ", " + current.getCol() + ") to goal (" + goalRow + ", " + goalCol + ")...");

        Stack<Grid> path;
        do {
            loopCount++;
            current = minimumCostCell(goalRow, goalCol);

            if (parents.containsKey(current)) {
                direction = getTargetDir(parents.get(current).getRow(), parents.get(current).getCol(), direction, current);

            }
            visited.add(current);
            toVisit.remove(current);

            if (visited.contains(explored_grid[goalRow][goalCol])) {
                System.out.print("Goal Visited , Path Found");
                path = getPath(goalRow, goalCol);
                printFastestPath(path);
                QueuetoString(executeString(robot, path));
                executePath(robot, path);
                return null;
            }

            if (panel.getMap().checkValidCoordinates(1 + current.getRow(), current.getCol())) {
                neighbors[0] = panel.getMap().getGrid(current.getRow() + 1, current.getCol());
                if (!canBeVisited(neighbors[0])) {
                    neighbors[0] = null;
                }
            }
            if (panel.getMap().checkValidCoordinates(current.getRow() - 1, current.getCol())) {
                neighbors[1] = panel.getMap().getGrid(current.getRow() - 1, current.getCol());
                if (!canBeVisited(neighbors[1])) {
                    neighbors[1] = null;
                }
            }
            if (panel.getMap().checkValidCoordinates(current.getRow(), current.getCol() - 1)) {
                neighbors[2] = panel.getMap().getGrid(current.getRow(), current.getCol() - 1);
                if (!canBeVisited(neighbors[2])) {
                    neighbors[2] = null;
                }
            }
            if (panel.getMap().checkValidCoordinates(current.getRow(), current.getCol() + 1)) {
                neighbors[3] = panel.getMap().getGrid(current.getRow(), current.getCol() + 1);
                if (!canBeVisited(neighbors[3])) {
                    neighbors[3] = null;
                }
            }

            for (int i = 0; i < 4; i++) {
                if (neighbors[i] != null) {
                    if (visited.contains(neighbors[i])) {
                        continue;
                    }
                    if (!(toVisit.contains(neighbors[i]))) {
                        parents.put(neighbors[i], current);
                        gCosts[neighbors[i].getRow()][neighbors[i].getCol()] = gCosts[neighbors[i].getRow()][neighbors[i].getCol()] + costG(current, neighbors[i], direction);
                        toVisit.add(neighbors[i]);
                    } else {
                        double currentGScore = gCosts[neighbors[i].getRow()][neighbors[i].getCol()];
                        double newGScore = gCosts[current.getRow()][current.getCol()] + costG(current, neighbors[i], direction);
                        if (newGScore < currentGScore) {
                            gCosts[neighbors[i].getRow()][neighbors[i].getCol()] = newGScore;
                            parents.put(neighbors[i], current);

                        }

                    }
                }

            }
        } while (!toVisit.isEmpty());
        System.out.println("Path Not Found");
        return null;

    }

    
    
    
    
    public Stack runFastestPathAtoB(int startRow, int startCol, int goalRow, int goalCol) {
        System.out.println("Calculating fastest path from (" + startRow + ", " + startCol + ") to goal (" + goalRow + ", " + goalCol + ")...");

        this.current = panel.getMap().getGrid(startRow, startCol);
        Stack<Grid> path;
        do {
            loopCount++;
            current = minimumCostCell(goalRow, goalCol);

            if (parents.containsKey(current)) {
                direction = getTargetDir(parents.get(current).getRow(), parents.get(current).getCol(), direction, current);

            }
            //visited shows the path i take
            visited.add(current);
            toVisit.remove(current);

            if (visited.contains(explored_grid[goalRow][goalCol])) {
                System.out.print(" Path Found");
                System.out.println(goalRow);
                System.out.println(goalCol);
                path = getPath(goalRow, goalCol);   
                return path;

            }
            if (panel.getMap().checkValidCoordinates(1 + current.getRow(), current.getCol())) {
                neighbors[0] = panel.getMap().getGrid(current.getRow() + 1, current.getCol());
                if (!canBeVisited(neighbors[0])) {
                    neighbors[0] = null;
                }
            }
            if (panel.getMap().checkValidCoordinates(current.getRow() - 1, current.getCol())) {
                neighbors[1] = panel.getMap().getGrid(current.getRow() - 1, current.getCol());
                if (!canBeVisited(neighbors[1])) {
                    neighbors[1] = null;
                }
            }
            if (panel.getMap().checkValidCoordinates(current.getRow(), current.getCol() - 1)) {
                neighbors[2] = panel.getMap().getGrid(current.getRow(), current.getCol() - 1);
                if (!canBeVisited(neighbors[2])) {
                    neighbors[2] = null;
                }
            }
            if (panel.getMap().checkValidCoordinates(current.getRow(), current.getCol() + 1)) {
                neighbors[3] = panel.getMap().getGrid(current.getRow(), current.getCol() + 1);
                if (!canBeVisited(neighbors[3])) {
                    neighbors[3] = null;
                }
            }

            for (int i = 0; i < 4; i++) {
                if (neighbors[i] != null) {
                    if (visited.contains(neighbors[i])) {
                        continue;
                    }
                    if (!(toVisit.contains(neighbors[i]))) {
                        parents.put(neighbors[i], current);
                        gCosts[neighbors[i].getRow()][neighbors[i].getCol()] = gCosts[neighbors[i].getRow()][neighbors[i].getCol()] + costG(current, neighbors[i], direction);
                        toVisit.add(neighbors[i]);
                    } else {
                        double currentGScore = gCosts[neighbors[i].getRow()][neighbors[i].getCol()];
                        double newGScore = gCosts[current.getRow()][current.getCol()] + costG(current, neighbors[i], direction);
                        if (newGScore < currentGScore) {
                            gCosts[neighbors[i].getRow()][neighbors[i].getCol()] = newGScore;
                            parents.put(neighbors[i], current);

                        }

                    }
                }

            }
        } while (!toVisit.isEmpty());
        System.out.println("Path Not Found");
        return null;
    }

    private Stack<Grid> getPath(int getColummn, int getRow) {
        Stack<Grid> actualPath = new Stack<>();
        Grid temp = panel.getMap().getGrid(getColummn, getRow);

        while (true) {
            actualPath.push(temp);
            temp = parents.get(temp);
            if (temp == null) {
                break;
            }
        }
        return actualPath;
    }

    public void executePath(Robot robot, Stack<Grid> path) {
        boolean initial = true;
        Grid temp = path.pop();
        //printFastestPath(path);
        //while (FastestMode) {
            while (temp != null) {
                rotateToFace(temp);
                if(initial) {
                    initial = false; 
                }
                else {
//                    rotateToFace(temp);
                    scheduleMovement(MOVEMENT.FORWARD);
                    //schedule(this::moveForward);
                }
                
                if(path.size() > 0)
                    temp = path.pop();
                else 
                    temp = null;
            }
//            rotateToFace(temp);
//            schedule(this::moveForward);
//            break;
//        }
    }
    
    private void scheduleMovement(MOVEMENT m){
        schedule(() -> {
            ec.move(m);
            panel.repaint();
        });
    }

    /**
     * Calculate the actual cost of moving from Cell a to Cell b (assuming both
     * are neighbors).
     */
    private double costG(Grid a, Grid b, DIRECTION aDir) {
        double moveCost = RobotConstants.MOVE_COST; // one movement to neighbor

        double turnCost;
        DIRECTION targetDir = getTargetDir(a.getRow(), a.getCol(), aDir, b);
        turnCost = getTurnCost(aDir, targetDir);

        return moveCost + turnCost;
    }

    public void printFastestPath(Stack<Grid> path) {
        System.out.println("\nLooped " + loopCount + " times.");
        System.out.println("The number of steps is: " + (path.size() - 1) + "\n");

        Stack<Grid> pathForPrint = (Stack<Grid>) path.clone();
        Grid temp;
        System.out.println("Path");
        while (!pathForPrint.empty()) {
            temp = pathForPrint.pop();
            if (!pathForPrint.isEmpty()) {
                System.out.print("(" + temp.getRow() + ", " + temp.getCol() + ") --> ");
            } else {
                System.out.print("(" + temp.getRow() + ", " + temp.getCol() + ")");
            }
        }

        System.out.println("\n");
    }

    public void printGCosts() {
        for (int i = 0; i < MapConstants.MAP_ROWS; i++) {
            for (int j = 0; j < MapConstants.MAP_COLS; j++) {
                System.out.print(gCosts[MapConstants.MAP_ROWS - 1 - i][j]);
                System.out.print(";");
            }
            System.out.println("\n");
        }
    }

   


    

    public void rotateToOrientation(DIRECTION TOSET) {
        switch (robot.getRobotDir()) {
            case NORTH: {
                switch (TOSET) {
                    case NORTH:
                        break;
                    case EAST: {
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                    case SOUTH: {
                        ec.move(MOVEMENT.RIGHT);
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                    case WEST: {
                        ec.move(MOVEMENT.LEFT);;
                        break;
                    }
                }
                break;
            }
            case EAST: {
                switch (TOSET) {
                    case NORTH: {
                        ec.move(MOVEMENT.LEFT);
                        break;
                    }
                    case EAST: {
                        break;
                    }
                    case SOUTH: {
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                    case WEST: {
                        ec.move(MOVEMENT.RIGHT);
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                }
                break;
            }
            case SOUTH: {
                switch (TOSET) {
                    case NORTH: {
                        ec.move(MOVEMENT.RIGHT);
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                    case EAST: {
                        ec.move(MOVEMENT.LEFT);
                        break;
                    }
                    case SOUTH: {
                        break;
                    }
                    case WEST: {
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                }
                break;
            }
            case WEST: {
                switch (TOSET) {
                    case NORTH: {
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                    case EAST: {
                        ec.move(MOVEMENT.RIGHT);
                        ec.move(MOVEMENT.RIGHT);
                        break;
                    }
                    case SOUTH: {
                        ec.move(MOVEMENT.LEFT);
                        break;
                    }
                    case WEST: {
                        break;
                    }
                }
                break;
            }

        }

    }

    //simplicity for virutal // actual requires 2 parameters(robot orientation and direction) 
    public void rotateToFace(Grid grid) {
        schedule(() -> {
            rotateToOrientation(getTargetDir(robot.getRobotPosRow(), robot.getRobotPosCol(), robot.getRobotDir(), grid));
            panel.repaint();
        });
    }



    private void schedule(Runnable run) {
        tasks.offer(new RunnableTask(run));

        if (tasks.size() == 1) {
            timer.schedule(tasks.peek(), 100);
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

            if (tasks.size() > 0) {
                timer.schedule(tasks.peek(), 100);
            }
        }

    }

    public void stopFastPath() {
        tasks.clear();
        setFastestMode(false);
    }
    
    public Queue<DIRECTION> executeString(Robot robot, Stack<Grid> path) {
        boolean initial = true;
        Stack<Grid> pathForString = (Stack<Grid>) path.clone();

        Grid temp = pathForString.pop();
        Grid next = pathForString.peek();
        DIRECTION fakerobotDir = DEFAULT_START_DIR;
        Queue<DIRECTION> StringPath = new LinkedList<>();
        StringPath.offer(DEFAULT_START_DIR);

        while (temp != null) {
//            System.out.println("temp Col " + temp.getCol());
//            System.out.println("Next Col " + next.getCol());
//            System.out.println("temp Row " + temp.getRow());
//            System.out.println("Next Row " + next.getRow());
            if (temp.getCol() - next.getCol() == 1) {
                fakerobotDir = DIRECTION.WEST;
         //       movements += " " + DIRECTION.WEST;
                StringPath.offer(fakerobotDir);

            } else if (temp.getCol() - next.getCol() == -1) {
                fakerobotDir = DIRECTION.EAST;
            //    movements += " " + DIRECTION.EAST;
                StringPath.offer(fakerobotDir);
            } else if (temp.getRow() - next.getRow() == 1) {
                fakerobotDir = DIRECTION.NORTH;
          //      movements += " " + DIRECTION.NORTH;
                StringPath.offer(fakerobotDir);
            } else if (temp.getRow() - next.getRow() == -1) {
                fakerobotDir = DIRECTION.SOUTH;
         //       movements += " " + DIRECTION.SOUTH;
                StringPath.offer(fakerobotDir);
            }

            // rotateToFace(temp);
            if (initial) {
                initial = false;
            } else {

            }
            if (pathForString.size() > 1) {
                temp = pathForString.pop();
                next = pathForString.peek();
            } else {
                temp = null;
            }
        }
        return StringPath;
    //    System.out.println(movements);
    }
    
    public void QueuetoString (Queue<DIRECTION> path ){
        DIRECTION from = path.poll();
        DIRECTION next = path.peek();
         while (from != null) {
            //South
            OrientationToString(from, next);
          

            // rotateToFace(temp);
            if (path.size() > 1) {
                from = path.poll();
                next = path.peek();
            } else {
                from = null;
            }
        }
        convert = movements.toString();
        convert = convert.replaceAll("wwwww", "q").replaceAll("www", "W");
        CommMgr comm = CommMgr.getCommMgr();
        comm.sendMsg(convert, "INSTR");
        System.out.println(convert);
        System.out.println(movements);
        
    }
    
    public void OrientationToString(DIRECTION FROM, DIRECTION TOSET) {
        switch (FROM) {
            case NORTH: {
                switch (TOSET) {
                    case NORTH:
                        movements += "w";
                        break;
                    case EAST: {
                        movements += "d";
                        movements += "w";
                        break;
                    }
                    case SOUTH: {
                        movements += "dd";
                        movements += "w";
                        break;
                    }
                    case WEST: {
                        movements += "a";
                        movements += "w";
                        break;
                    }
                }
                break;
            }
            case EAST: {
                switch (TOSET) {
                    case NORTH: {
                        movements += "a";
                        movements += "w";
                        break;
                    }
                    case EAST: {
                        movements += "w";
                        break;
                    }
                    case SOUTH: {
                        movements += "d";
                        movements += "w";
                        break;
                    }
                    case WEST: {
                        movements += "dd";
                        movements += "w";
                        break;
                    }
                }
                break;
            }
            case SOUTH: {
                switch (TOSET) {
                    case NORTH: {
                        movements += "dd";
                        movements += "w";
                        break;
                    }
                    case EAST: {
                        movements += "a";
                        movements += "w";
                        break;
                    }
                    case SOUTH: {
                        movements += "w";
                        break;
                    }
                    case WEST: {
                        movements += "d";
                        movements += "w";
                        break;
                    }
                }
                break;
            }
            case WEST: {
                switch (TOSET) {
                    case NORTH: {
                        movements += "d";
                        movements += "w";
                        break;
                    }
                    case EAST: {
                        movements += "dd";
                        movements += "w";
                        break;
                    }
                    case SOUTH: {
                        movements += "a";
                        movements += "w";
                        break;
                    }
                    case WEST: {
                        movements += "w";
                        break;
                    }
                }
                break;
            }

        }

    }
    
    public Stack<DIRECTION> executeStringStack (Robot robot, Stack<Grid> path) {
        boolean initial = true;
        Stack<Grid> pathForString = (Stack<Grid>) path.clone();

        Grid temp = pathForString.pop();
        Grid next = pathForString.peek();
        DIRECTION fakerobotDir = DEFAULT_START_DIR;
        Stack<DIRECTION> StringPath = new Stack<>();
        StringPath.push(DEFAULT_START_DIR);

        while (temp != null) {
//            System.out.println("temp Col " + temp.getCol());
//            System.out.println("Next Col " + next.getCol());
//            System.out.println("temp Row " + temp.getRow());
//            System.out.println("Next Row " + next.getRow());
            if (temp.getCol() - next.getCol() == 1) {
                fakerobotDir = DIRECTION.WEST;
         //       movements += " " + DIRECTION.WEST;
                StringPath.push(fakerobotDir);

            } else if (temp.getCol() - next.getCol() == -1) {
                fakerobotDir = DIRECTION.EAST;
            //    movements += " " + DIRECTION.EAST;
                StringPath.push(fakerobotDir);
            } else if (temp.getRow() - next.getRow() == 1) {
                fakerobotDir = DIRECTION.NORTH;
          //      movements += " " + DIRECTION.NORTH;
                StringPath.push(fakerobotDir);
            } else if (temp.getRow() - next.getRow() == -1) {
                fakerobotDir = DIRECTION.SOUTH;
         //       movements += " " + DIRECTION.SOUTH;
                StringPath.push(fakerobotDir);
            }
            // rotateToFace(temp);
            if (initial) {
                initial = false;
            } else {

            }
            if (pathForString.size() > 1) {
                temp = pathForString.pop();
                next = pathForString.peek();
            } else {
                temp = null;
            }
        }
        return StringPath;
    //    System.out.println(movements);
    }

}

//SetObstacleCell()
//Instantiate fcontrol
