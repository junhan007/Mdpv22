/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import Robot.Robot;
//import Robot.ExploreController;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.GridLayout;

/**
 *
 * @author junha
 */
public class MapFrame  {
    
    private JPanel maincontent;
    private JFrame frame; 
    private MapPanel mapPanel;
    //private Map map;
    //private Robot robot;
    private ControlPanel control;

    
    public MapFrame(MapPanel mapPanel){
        frame = new JFrame();
        //this.robot = robot;
        this.mapPanel = mapPanel;
        
        //mapPanel = new MapPanel(map, robot);
        control = new ControlPanel(); 
        maincontent = new JPanel();      
        maincontent.add(mapPanel);
        maincontent.add(control);
        frame.setContentPane(maincontent);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Map Simulator");
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        
    }
    public MapPanel getMapPanel(){
        return mapPanel; 
    }
    
    public void setMapPanel(MapPanel mapPanel){
        this.mapPanel = mapPanel;
    }

    public ControlPanel getControlPanel() {
        return control;
    }
    

  
}

