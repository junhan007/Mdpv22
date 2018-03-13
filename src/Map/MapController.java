/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import Robot.Robot;
import static Robot.RobotConstants.ROBOT_SIZE;
import Robot.RobotConstants;
//import Robot.ExploreController;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import static Map.MapConstants.MAP_COLS;
import static Map.MapConstants.MAP_ROWS;
import Robot.ExploreController;
import Algorithm.ExplorationAlgo;
import Algorithm.FastestPathAlgo;
import Algorithm.mdpMain;
import Robot.RobotConstants.DIRECTION;
import Utils.MapDescriptor;
import java.io.File;
import java.util.Stack;
import javax.swing.JFileChooser;

/**
 *
 * @author junha
 */
public class MapController implements MouseListener, ActionListener {

    MapPanel mapPanel;
    MapFrame mapFrame;
    ControlPanel cpanel;
    Robot m_robot;
    Map map;
    ExploreController r_control;
    FastestPathAlgo fpath_control;
    private JFileChooser fc;

    public MapController(MapFrame mapFrame, Robot m_robot) {
        this.mapFrame = mapFrame;
        this.mapPanel = mapFrame.getMapPanel();
        this.mapPanel.addMouseListener(this);
        this.cpanel = mapFrame.getControlPanel();
        this.m_robot = m_robot;
        this.cpanel.getClearMapPanel().getClearButton().addActionListener(this);
        this.cpanel.getClearMapPanel().getStopButton().addActionListener(this);
        this.cpanel.getAlgoPanel().getFastestPathButton().addActionListener(this);
        this.cpanel.getAlgoPanel().getExploreButton().addActionListener(this);
        this.cpanel.getLoadSavePanel().getLoadButton().addActionListener(this);
        this.cpanel.getLoadSavePanel().getSaveButton().addActionListener(this);
        this.cpanel.getModePanel().getPhyButton().addActionListener(this);
        this.cpanel.getModePanel().getSimuButton().addActionListener(this);
        this.fc = new JFileChooser();
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public void setMapPanel(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        Point eventpoint;
        Point coordpoint;

        //BUTTON1 is left click, BUTTON 3 is right click
        if (me.getButton() == MouseEvent.BUTTON1) {
            eventpoint = me.getPoint();
            coordpoint = conversion(eventpoint);
            if (!((coordpoint.x < 3 && coordpoint.y > 16) || (coordpoint.x > 11 && coordpoint.y < 3))) {
                if (!mapPanel.getMap().getGrid(coordpoint.y, coordpoint.x).getIsWayPoint()) {
                    if (mapPanel.getMap().getGrid(coordpoint.y, coordpoint.x).getIsObstacle()) {
                        mapPanel.getMap().setObstacleCell(coordpoint.y, coordpoint.x, false);
                    } else if (!mapPanel.getMap().getGrid(coordpoint.y, coordpoint.x).getIsObstacle()) {
                        mapPanel.getMap().setObstacleCell(coordpoint.y, coordpoint.x, true);
                    }
                }
            }
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            eventpoint = me.getPoint();
            coordpoint = conversion(eventpoint);
            if (!((coordpoint.x < 3 && coordpoint.y > 16) || (coordpoint.x > 11 && coordpoint.y < 3))) {
                if (!mapPanel.getMap().getGrid(coordpoint.y, coordpoint.x).getIsObstacle()) {
                    if (checkWayPointExist()) {
                        mapPanel.getMap().getGrid(coordpoint.y, coordpoint.x).setIsWayPoint(false);
                    } else if (!checkWayPointExist()) {

                        mapPanel.getMap().getGrid(coordpoint.y, coordpoint.x).setIsWayPoint(true);
                    }
                }
            }
        }

        mapPanel.repaint();
    }

    public Point conversion(Point eventpoint) {
        Point pixelpoint = eventpoint;
        Point coordpoint = new Point();
        coordpoint.x = (int) ((pixelpoint.getX()) / 41);
        coordpoint.y = (int) ((pixelpoint.getY()) / 41);
        return coordpoint;

    }

    @Override
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ae.getSource();

        if (ae.getSource() == cpanel.getAlgoPanel().getExploreButton()) {
            initExploration();

        }
        if (ae.getSource() == cpanel.getAlgoPanel().getFastestPathButton()) {
            int row;
            int col;
            if (checkWayPointExist()) {
                this.fpath_control = new FastestPathAlgo(m_robot, mapFrame, m_robot.getRobotPosRow(), m_robot.getRobotPosCol(), m_robot.getRobotDir());
                for (int x = 0; x < MAP_COLS; x++) {
                    for (int y = 0; y < MAP_ROWS; y++) {
                        if (mapPanel.getMap().getGrid(y, x).getIsWayPoint()) {
                            row = y;
                            col = x;
                            Stack<Grid> pathToWaypoint = fpath_control.runFastestPathAtoB(m_robot.getRobotPosRow(), m_robot.getRobotPosCol(), row, col);

                            Stack<DIRECTION> WaypointStack = fpath_control.executeStringStack(m_robot, pathToWaypoint);
                            DIRECTION temp = WaypointStack.pop();
                            
                            this.fpath_control = new FastestPathAlgo(m_robot, mapFrame, row, col, temp);

                            Stack<Grid> pathToGoal = fpath_control.runFastestPathAtoB(row, col, 1, 13);
                            pathToGoal.pop();
                            pathToGoal.addAll(pathToWaypoint);
                            fpath_control.printFastestPath(pathToGoal);
                            fpath_control.QueuetoString(fpath_control.executeString(m_robot, pathToGoal));
                            fpath_control.executePath(m_robot, pathToGoal);

                        }
                    }
                }

            } else {
                this.fpath_control = new FastestPathAlgo(m_robot, mapFrame);
                fpath_control.runFastestPath(1, 13);
            }
        }
        if (ae.getSource() == cpanel.getClearMapPanel().getClearButton()) {
            resetMap();
        }

        if (ae.getSource() == cpanel.getClearMapPanel().getStopButton()) {
            fpath_control.stopFastPath();
            m_robot.setRobotPos(RobotConstants.DEFAULT_START_ROW, RobotConstants.DEFAULT_START_COL);
            m_robot.setRobotDir(RobotConstants.DEFAULT_START_DIR);
            mapPanel.repaint();
        }
        if (ae.getSource() == cpanel.getLoadSavePanel().getLoadButton()) {
            int returnVal = fc.showOpenDialog(cpanel.getLoadSavePanel());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                MapDescriptor md = new MapDescriptor();
                map = md.loadMapFromDisk(file);
                loadMap(map);

            }
        }
        if (ae.getSource() == cpanel.getLoadSavePanel().getSaveButton()) {
            int returnVal = fc.showSaveDialog(cpanel.getLoadSavePanel());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
            }
        }
    }

//    public void resetMap() {
//        for (int y = 0; y < MAP_ROWS; y++) {
//            for (int x = 0; x < MAP_COLS; x++) {
//                //mapPanel.getMap().getGrid(y, x).resetGrid();
//                mapPanel.setMap(new Map());
//            }
//        }
//        mapPanel.repaint();
//    }
    public void resetMap() {
        mdpMain m = new mdpMain();
        m.redisplay();
    }

    public void loadMap(Map map) {
        mdpMain m = new mdpMain();
        m.loadMap(map);
    }
    
    public void initExploration(){
        m_robot.setSpeed(cpanel.getSpeedPanel().getSettings());
        m_robot.setTimeLimit(cpanel.getTimePanel().getSettings());
        m_robot.setCoverage(cpanel.getCoveragePanel().getSettings());
        mdpMain m = new mdpMain();
        m.startExploration(mapPanel);
    }

    /*
    public void SetStartPoint(){
        for (int y = 17 ; y < MAP_ROWS; y++){
            for (int x = 0 ; x < 3 ; x++){
                mapPanel.getMap().getGrid(x, y).setIsStartPoint(true);
            }
        }
    }
    
    
    public void SetEndPoint(){
        for (int y = 0 ; y < 3 ; y++){
            for (int x = 12 ; x < 15 ; x++){
                mapPanel.getMap().getGrid(x, y).setIsEndPoint(true);
            }
        }
    }  
     */
    public void Robotlocation(Robot robot) {
        for (int y = 0; y < ROBOT_SIZE; y++) {
            for (int x = 0; x < ROBOT_SIZE; x++) {
                //mapPanel.getMap().getGrid(x, y)).setIsVisiting(true);
                mapPanel.getMap().getGrid(x, y).setIsExplored(true);
            }

        }

    }

    public boolean checkWayPointExist() {
        for (int y = 0; y < MAP_ROWS; y++) {
            for (int x = 0; x < MAP_COLS; x++) {
                if (mapPanel.getMap().getGrid(y, x).getIsWayPoint()) {
                    return true;
                }
            }
        }
        return false;
    }
}
