/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import Robot.Robot;
import Robot.RobotConstants;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author denis
 */
public class Map extends JPanel{
    
    private final Grid[][] grid;
    //private final Robot robot;
    
    /**
     * Initialises a Map object with a grid of Cell objects.
     */
    public Map() {
        //this.robot = robot;

        grid = new Grid[MapConstants.MAP_ROWS][MapConstants.MAP_COLS];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                grid[row][col] = new Grid(row, col);

                // Set the virtual walls of the arena
                if (row == 0 || col == 0 || row == MapConstants.MAP_ROWS - 1 || col == MapConstants.MAP_COLS - 1) {
                    grid[row][col].setIsVirtualWall(true);
                }
            }
        }
    }
    
    public Grid[][] getAllGrid(){
        return grid;
    }
    
    /**
     * Returns true if the row and column values are valid.
     */
    public boolean checkValidCoordinates(int row, int col) {
        return row >= 0 && col >= 0 && row < MapConstants.MAP_ROWS && col < MapConstants.MAP_COLS;
    }

    /**
     * Returns true if the row and column values are in the start zone.
     */
    /*
    private boolean inStartZone(int row, int col) {
        return row >= 0 && row <= 2 && col >= 0 && col <= 2;
    }
    */
    
    public boolean inStartZone(int row, int col) {
        return (row <= MapConstants.START_ROW + 1 && row >= MapConstants.START_ROW - 1 && col <= MapConstants.START_COL + 1 && col >= MapConstants.START_COL - 1);
    }
    /**
     * Returns true if the row and column values are in the goal zone.
     */
    public boolean inGoalZone(int row, int col) {
        return (row <= MapConstants.GOAL_ROW + 1 && row >= MapConstants.GOAL_ROW - 1 && col <= MapConstants.GOAL_COL + 1 && col >= MapConstants.GOAL_COL - 1);
    }

    /**
     * Returns a particular cell in the grid.
     */
    public Grid getGrid(int row, int col) {
        return grid[row][col];
    }

    /**
     * Returns true if a cell is an obstacle.
     */
    public boolean isObstacleCell(int row, int col) {
        return grid[row][col].getIsObstacle();
    }

    /**
     * Returns true if a cell is a virtual wall.
     */
    public boolean isVirtualWallCell(int row, int col) {
        return grid[row][col].getIsVirtualWall();
    }
    
     /**
     * Sets all cells in the grid to an explored state.
     */
    public void setAllExplored() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                grid[row][col].setIsExplored(true);
            }
        }
    }

    /**
     * Sets all cells in the grid to an unexplored state except for the START & GOAL zone.
     */
    public void setAllUnexplored() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (inStartZone(row, col) || inGoalZone(row, col)) {
                    grid[row][col].setIsExplored(true);
                } else {
                    grid[row][col].setIsExplored(false);
                }
            }
        }
    }
    
    /**
     * Sets a cell as an obstacle and the surrounding cells as virtual walls or resets the cell and surrounding
     * virtual walls.
     */
    public void setObstacleCell(int row, int col, boolean isObstacle) {
        if (isObstacle && (inStartZone(row, col) || inGoalZone(row, col)))
            return;

        grid[row][col].setIsObstacle(isObstacle);
        
        if(checkValidCoordinates(row - 1, col - 1)){
            grid[row - 1][col - 1].setIsVirtualWall(isObstacle);
        }
        if(checkValidCoordinates(row - 1, col)){
            grid[row - 1][col].setIsVirtualWall(isObstacle);
        }
        if(checkValidCoordinates(row - 1, col + 1)){
            grid[row - 1][col + 1].setIsVirtualWall(isObstacle);
        }
        if(checkValidCoordinates(row, col - 1)){
            grid[row][col - 1].setIsVirtualWall(isObstacle);
        }
        if(checkValidCoordinates(row, col + 1)){
            grid[row][col + 1].setIsVirtualWall(isObstacle);
        }
        if(checkValidCoordinates(row + 1, col - 1)){
            grid[row + 1][col - 1].setIsVirtualWall(isObstacle);
        }
        if(checkValidCoordinates(row + 1, col)){
            grid[row + 1][col].setIsVirtualWall(isObstacle);
        }
        if(checkValidCoordinates(row + 1, col + 1)){
            grid[row + 1][col + 1].setIsVirtualWall(isObstacle);
        }


//        if (row >= 1) {
//            grid[row - 1][col].setIsVirtualWall(isObstacle);            // bottom cell
//
//            if (col < MapConstants.MAP_COLS - 1) {
//                grid[row - 1][col + 1].setIsVirtualWall(isObstacle);    // bottom-right cell
//            }
//
//            if (col >= 1) {
//                grid[row - 1][col - 1].setIsVirtualWall(isObstacle);    // bottom-left cell
//            }
//        }
//
//        if (row < MapConstants.MAP_ROWS - 1) {
//            grid[row + 1][col].setIsVirtualWall(isObstacle);            // top cell
//
//            if (col < MapConstants.MAP_COLS - 1) {
//                grid[row + 1][col + 1].setIsVirtualWall(isObstacle);    // top-right cell
//            }
//
//            if (col >= 1) {
//                grid[row + 1][col - 1].setIsVirtualWall(isObstacle);    // top-left cell
//            }
//        }
//
//        if (col >= 1) {
//            grid[row][col - 1].setIsVirtualWall(isObstacle);            // left cell
//        }
//
//        if (col < MapConstants.MAP_COLS - 1) {
//            grid[row][col + 1].setIsVirtualWall(isObstacle);            // right cell
//        }
    }

    /**
     * Returns true if the given cell is out of bounds or an obstacle.
     */
    public boolean getIsObstacleOrWall(int row, int col) {
        return !checkValidCoordinates(row, col) || getGrid(row, col).getIsObstacle();
    }
    
    /*
    public boolean getIsEndPoint(int row, int col){
        return (grid[row][col].getRow() == MapConstants.GOAL_ROW & grid[row][col].getCol() == MapConstants.GOAL_COL);
    }

    public boolean getIsStartPoint(int row, int col){
        return (grid[row][col].getRow() == MapConstants.START_ROW && grid[row][col].getCol() == MapConstants.START_COL);
    }
    
 
*/
    
    public void printMap(){
        for(int i = 0; i<MapConstants.MAP_ROWS; i++){
            for(int j = 0; j<MapConstants.MAP_COLS; j++){
                if(grid[i][j].getIsObstacle()){
                    System.out.print("1");
                } else{
                System.out.print("0");
                }
            }
            System.out.println();
        }
    }
}
