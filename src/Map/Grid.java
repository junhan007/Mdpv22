/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

/**
 *
 * @author denis
 */
public class Grid {
    private final int row;
    private final int col;
    private boolean isObstacle;
    private boolean isExplored = true;
    private boolean isVirtualWall;
    private boolean isWayPoint;
    
    public Grid(int row, int col){
        this.row = row;
        this.col = col;
    }
    
    public int getRow(){
        return this.row;
    }
    
    public int getCol(){
        return this.col;
    }
    
    public void setIsObstacle(boolean isObstacle){
        this.isObstacle = isObstacle;
    }
    
    public boolean getIsObstacle(){
        return this.isObstacle;
    }
    
    public void setIsExplored(boolean isExplored){
        this.isExplored = isExplored;
    }
    
    public boolean getIsExplored(){
        return this.isExplored;
    }
    
    public void setIsVirtualWall(boolean isVirtualWall){
        this.isVirtualWall = isVirtualWall;
    }
    
    public boolean getIsVirtualWall(){
        return this.isVirtualWall;
    }   
    
    public void setIsWayPoint(boolean isWayPoint){
        this.isWayPoint = isWayPoint;
    }
    
    public boolean getIsWayPoint(){
        return this.isWayPoint;
    }

    public void resetGrid() {
        isExplored = true;
        isObstacle = false;
        isWayPoint = false;
    }    
}
