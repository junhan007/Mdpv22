/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Robot;

import java.awt.Color;

/**
 *
 * @author denis
 */
public class RobotConstants {
    
    // Robot size
    public static final int ROBOT_SIZE = 3;
        
    public static final int MOVE_COST = 1;
    public static final int TURN_COST = 20;
    
    public static final int SPEED = 100;
    
    //public static final DIRECTION START_DIR = DIRECTION.EAST;
    
    public static final int SHORT_SENSOR_MIN = 1;
    public static final int SHORT_SENSOR_MAX = 2;
    
    public static final int LONG_SENSOR_MIN = 1;
    public static final int LONG_SENSOR_MAX = 4;
    
    public static final int INFINITE_COST = 9999;
    
    public enum DIRECTION {
        NORTH, EAST, SOUTH, WEST;

        public static DIRECTION getNext(DIRECTION curDirection) {
            return values()[(curDirection.ordinal() + 1) % values().length];
        }

        public static DIRECTION getPrevious(DIRECTION curDirection) {
            return values()[(curDirection.ordinal() + values().length - 1) % values().length];
        }

        public static char print(DIRECTION d) {
            switch (d) {
                case NORTH:
                    return 'N';
                case EAST:
                    return 'E';
                case SOUTH:
                    return 'S';
                case WEST:
                    return 'W';
                default:
                    return 'X';
            }
        }
    }
        
    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT, LEFT, CAL_COR, CAL_SIDE , ERROR;

        public static char print(MOVEMENT m) {
            switch (m) {
                case FORWARD:
                    return 'w';
                case BACKWARD:
                    return 's';
                case RIGHT:
                    return 'd';
                case LEFT:
                    return 'a';
                case CAL_COR:
                    return 'g';
                case CAL_SIDE:
                    return 'h';
                case ERROR:
                default:
                    return 'e';
            }
        }
    }
    
    // constants for graphics
    // For rendering the robot in the robot map
    public static final Color C_ROBOT_OUTLINE = new Color(0, 0, 0, 220);
    public static final Color C_ROBOT = new Color(0, 205, 255, 160);
    public static final Color C_ROBOT_FRONT = new Color(0, 46, 155, 220);

    // For rendering the robot path in the robot map
    public static final Color C_EXPLORE_PATH = Color.RED;
    public static final Color C_SHORTEST_PATH = Color.ORANGE;
    public static final int PATH_THICKNESS = 4;

    public static final Color C_SENSOR = Color.DARK_GRAY;
    public static final Color C_SENSOR_BEAM_OUTER = new Color(220, 0, 0, 160);
    public static final Color C_SENSOR_BEAM_INNER = new Color(255, 0, 0, 190);

    // Robot Default Configuration
    public static final int DEFAULT_START_ROW = 18; // Changed to 1 based on ROBOT_SIZE
    public static final int DEFAULT_START_COL = 1;
    public static final DIRECTION DEFAULT_START_DIR = DIRECTION.SOUTH;
    public static final DIRECTION DEFAULT_START_SP_DIR = DIRECTION.NORTH;

    // Robot Exploration Configuration
    public static final int DEFAULT_STEPS_PER_SECOND = 5;
    public static final int DEFAULT_COVERAGE_LIMIT = 50;
    public static final int DEFAULT_TIME_LIMIT = 360;
    
    /*
    public static final Color C_ROBOT = Color.RED;
    public static final Color C_ROBOT_DIR = Color.WHITE;


    public static final int ROBOT_W = 70;
    public static final int ROBOT_H = 70;

    public static final int ROBOT_X_OFFSET = 10;
    public static final int ROBOT_Y_OFFSET = 20;

    public static final int ROBOT_DIR_W = 10;
    public static final int ROBOT_DIR_H = 10;
*/
    
}
