/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import Map.MapConstants;
import Map.Map;
import Robot.Robot;

/**
 *
 * @author denis
 */
public class MapPanel extends JPanel{
    
    Map map;
    Robot robot;
    
    public MapPanel(Map map, Robot robot) {
        this.map = map;
        this.robot = robot;
        
    }
    
    public Map getMap(){
        return map;
    }
    
    public void setMap(Map map){
        this.map = map;
        this.repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int Size = 41;

        for (int col = 0; col < MapConstants.MAP_COLS + 1; col++) {
            g.drawLine(col * Size, 0, col * Size, this.getHeight());
        };
        for (int row = 0; row < MapConstants.MAP_ROWS + 1; row++) {
            g.drawLine(0, row * Size, this.getWidth(), row * Size);
        };

    }

    @Override
    public Dimension getPreferredSize() {
        int Size = 41;
        return new Dimension(Size * MapConstants.MAP_COLS + 1, Size * MapConstants.MAP_ROWS + 1);
    }

    ;
   
    @Override
    protected void paintChildren(Graphics g) {

        paintEnd(g);
        paintStart(g);
        paintObstacle(g);
        paintWaypoint(g);
        paintExplored(g);
        paintUnexplored(g);
        paintRobot(g);
        paintOrientation(g);
    }

    public void paintRobot(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval((robot.getRobotPosCol()-1) * 41 , 41 * (robot.getRobotPosRow() -1) , 41 * 3 - 5, 41 * 3 - 5);

    }

    public void paintOrientation(Graphics g) {
        g.setColor(Color.RED);
        switch (robot.getRobotDir()) {
            case NORTH: {
                g.fillOval((robot.getRobotPosCol() ) * 41 + 12, (robot.getRobotPosRow() -1)* 41 + 12, 20, 20);
                break;
            }
            case SOUTH: {
                g.fillOval((robot.getRobotPosCol() ) * 41 + 12 , (robot.getRobotPosRow() +1)* 41 + 12, 20, 20);
                break;
            }
             case EAST: {
                g.fillOval((robot.getRobotPosCol() + 1) * 41 + 12, (robot.getRobotPosRow()) * 41 + 12, 20, 20);
                break;
            }
            case WEST: {
                g.fillOval((robot.getRobotPosCol()-1) * 41 + 12, (robot.getRobotPosRow() )* 41 + 12, 20, 20);
                break;
            }             
        }
        
    }
    private void paintEnd(Graphics g) {

        g.setColor(Color.MAGENTA);
        for (int row = 0; row < MapConstants.MAP_ROWS; row++) {
            for (int col = 0; col < MapConstants.MAP_COLS; col++) {
                if (map.inGoalZone(row,col)) {
                    g.fillRect(col * 41 + 1, row * 41 + 1, 40, 40);

                }
            }
        }

    }

    private void paintStart(Graphics g) {

        g.setColor(Color.ORANGE);
        for (int row = 0; row < MapConstants.MAP_ROWS; row++) {
            for (int col = 0; col < MapConstants.MAP_COLS; col++) {
                if (map.inStartZone(row,col)) {
                    g.fillRect(col * 41 + 1, row * 41 + 1, 40, 40);

                }
            }
        }

    }

    /*
    public Grid[][] getAllGrid() {
        return grid; 
    }
   
    public MdpGrid getEndPoint(){
        return grid[12][0];
    }
    
    public MdpGrid getStartpoint(){
        return grid[0][17];
    }
    */
    
    public void paintObstacle(Graphics g) {

        g.setColor(Color.BLACK);
        for (int row = 0; row < MapConstants.MAP_ROWS; row++) {
            for (int col = 0; col < MapConstants.MAP_COLS; col++) {
                if (map.getGrid(row,col).getIsObstacle()) {
                    g.fillRect(col * 41 + 1, row * 41 + 1, 40, 40);

                }
            }
        }

    }

    public void paintWaypoint(Graphics g) {

        g.setColor(Color.RED);
        for (int row = 0; row < MapConstants.MAP_ROWS; row++) {
            for (int col = 0; col < MapConstants.MAP_COLS; col++) {
                if (map.getGrid(row, col).getIsWayPoint()) {
                    g.fillOval(col * 41 + 1, row * 41 + 1, 40, 40);

                }
            }
        }

    }
    public void paintUnexplored(Graphics g) {

        g.setColor(Color.GRAY);
        for (int row = 0; row < MapConstants.MAP_ROWS; row++) {
            for (int col = 0; col < MapConstants.MAP_COLS; col++) {
                if (!map.inGoalZone(row, col) && !map.inStartZone(row, col) && !map.getGrid(row, col).getIsObstacle() && !map.getGrid(row, col).getIsWayPoint() && !map.getGrid(row, col).getIsExplored()) {
                    g.fillRect(col * 41 + 1, row * 41 + 1, 40, 40);

                }
            }
        }

    }
    public void paintExplored(Graphics g) {

        g.setColor(Color.WHITE);
        for (int row = 0; row < MapConstants.MAP_ROWS; row++) {
            for (int col = 0; col < MapConstants.MAP_COLS; col++) {
                if (!map.inGoalZone(row,col) && !map.inStartZone(row,col) && !map.getGrid(row, col).getIsObstacle() && !map.getGrid(row, col).getIsWayPoint() && map.getGrid(row, col).getIsExplored()) {
                    g.fillRect(col * 41 + 1, row * 41 + 1, 40, 40);
                }
            }
        }

    }
    
    /*
    public void createUnexploredGrid(){
        this.grid = new MdpGrid[MAP_COLS][MAP_ROWS];
        
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int column = 0; column < MapConstants.MAP_COLS; column++) {
                this.grid[column][row] = new MdpGrid(column, row);
                this.grid[column][row].setIsExplored(false);
            }
        }       
    }  
*/
}
