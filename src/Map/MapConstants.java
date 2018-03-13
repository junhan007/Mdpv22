/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import java.awt.Color;

/**
 *
 * @author denis
 */
public class MapConstants {
    
    // constants for map dimensions
    public static final int MAP_SIZE = 300;
    
    public static final int MAP_ROWS = 20;
    public static final int MAP_COLS = 15;
    
    public static final int START_ROW = 18;
    public static final int START_COL = 1;
    
    public static final int GOAL_ROW = 1;
    public static final int GOAL_COL = 13;
    
    // constants for graphics
    public static final Color C_GRID_LINE = Color.GRAY;
    public static final int GRID_LINE_WEIGHT = 2;

    public static final Color C_START = Color.BLUE;
    public static final Color C_GOAL = Color.GREEN;
    public static final Color C_UNEXPLORED = Color.LIGHT_GRAY;
    public static final Color C_FREE = Color.WHITE;
    public static final Color C_OBSTACLE = Color.BLACK;
    
    public static final int CELL_SIZE = 30;
    
    /*
    public static final int MAP_H = 600;
    public static final int MAP_X_OFFSET = 120;
*/
}
