/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Robot;

import Map.Map;
import Robot.RobotConstants.DIRECTION;
/**
 *
 * @author denis
 */
public class Sensor {
    private final int minRange;
    private final int maxRange;
    private int sensorPosRow;
    private int sensorPosCol;
    private DIRECTION sensorDir;
    
    private final String id;
    
    public Sensor(int minRange, int maxRange, int row, int col, DIRECTION dir, String id) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
        this.id = id;
    }

    public void setSensor(int row, int col, DIRECTION dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }
    
    /**
     * Returns the number of cells to the nearest detected obstacle or -1 if no obstacle is detected.
     */
    public int sense(Map exploredMap, Map realMap) {
        switch (sensorDir) {
            case NORTH:
                return getSensorVal(exploredMap, realMap, -1, 0);
            case EAST:
                return getSensorVal(exploredMap, realMap, 0, 1);
            case SOUTH:
                return getSensorVal(exploredMap, realMap, 1, 0);
            case WEST:
                return getSensorVal(exploredMap, realMap, 0, -1);
        }
        return -1;
    }

    /**
     * Sets the appropriate obstacle cell in the map and returns the row or column value of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    private int getSensorVal(Map exploredMap, Map realMap, int rowInc, int colInc) {
        // Check if starting point is valid for sensors with lowerRange > 1.
//        if (minRange > 1) {
//            for (int i = 0; i < this.minRange; i++) {
//                int row = this.sensorPosRow + (rowInc * i);
//                int col = this.sensorPosCol + (colInc * i);
//
//                if (!exploredMap.checkValidCoordinates(row, col)) return i;
//                if (realMap.getGrid(row, col).getIsObstacle()){
//                    exploredMap.setObstacleCell(row, col, true);
//                    return i;
//                }
//                
//                exploredMap.getGrid(row, col).setIsExplored(true);
//            }
//        }

        // Check if anything is detected by the sensor and return that value.
        for (int i = 1; i <= this.maxRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.checkValidCoordinates(row, col)) return i;

            exploredMap.getGrid(row, col).setIsExplored(true);
            System.out.println("set grid is explored - row: " + row + ", col: " + col);

            if (realMap.getGrid(row, col).getIsObstacle()) {
                exploredMap.setObstacleCell(row, col, true);
                return i;
            }
        }

        // Else, return -1.
        return -1;
    }

    /**
     * Uses the sensor direction and given value from the actual sensor to update the map.
     */
    public void senseReal(Map exploredMap, int sensorVal) {
        switch (sensorDir) {
            case NORTH:
                processSensorVal(exploredMap, sensorVal, -1, 0);
                break;
            case EAST:
                processSensorVal(exploredMap, sensorVal, 0, 1);
                break;
            case SOUTH:
                processSensorVal(exploredMap, sensorVal, 1, 0);
                break;
            case WEST:
                processSensorVal(exploredMap, sensorVal, 0, -1);
                break;
        }
    }

    /**
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    private void processSensorVal(Map exploredMap, int sensorVal, int rowInc, int colInc) {
        if (sensorVal == 0) return;  // return value for LR sensor if obstacle before lowerRange

        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
//        for (int i = 1; i < this.minRange; i++) {
//            int row = this.sensorPosRow + (rowInc * i);
//            int col = this.sensorPosCol + (colInc * i);
//
//            if (!exploredMap.checkValidCoordinates(row, col)) return;
//            if (exploredMap.getGrid(row, col).getIsObstacle()) return;
//        }

        // Update map according to sensor's value.
        for (int i = 1; i <= this.maxRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.checkValidCoordinates(row, col)) continue;

            exploredMap.getGrid(row, col).setIsExplored(true);

            if (sensorVal == i) {
                exploredMap.setObstacleCell(row, col, true);
                break;
            }

            // Override previous obstacle value if front sensors detect no obstacle.
//            if (exploredMap.getGrid(row, col).getIsObstacle()) {
//                if (id.equals("SRF1") || id.equals("SRF2") || id.equals("SRF3")) {
//                    exploredMap.setObstacleCell(row, col, false);
//                } else {
//                    break;
//                }
//            }
        }
    }
}
