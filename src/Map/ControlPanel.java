/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import static Map.MapConstants.MAP_COLS;
import static Map.MapConstants.MAP_ROWS;

/**
 *
 * @author denis
 */
public class ControlPanel extends JPanel{
    
   ModePanel mode;
   AlgoPanel algo;
   MdfPanel mdf1;
   MdfPanel mdf2;
   ClearMapPanel exit;
   LoadSavePanel load;
   SettingPanel speed;
   SettingPanel time;
   SettingPanel coverage;
   
    public ControlPanel(){
        mode = new ModePanel(); 
        algo = new AlgoPanel();
        mdf1 = new MdfPanel("MDF1");
        mdf2 = new MdfPanel("MDF2"); 
        exit = new ClearMapPanel();
        load = new LoadSavePanel();
        speed = new SettingPanel("Delay Speed");
        time = new SettingPanel("Time Limit");
        coverage = new SettingPanel("Coverage (grid)");
        
        BoxLayout box = new BoxLayout(this,BoxLayout.Y_AXIS); 
        this.setLayout(box); 
        this.add(mode);
        this.add(speed);
        this.add(time);
        this.add(coverage);
        this.add(algo);
        this.add(load);
        this.add(mdf1);
        this.add(mdf2);
        this.add(exit);

    }

    public SettingPanel getSpeedPanel() {
        return speed;
    }

    public SettingPanel getTimePanel() {
        return time;
    }

    public SettingPanel getCoveragePanel() {
        return coverage;
    }

       public ModePanel getModePanel() {
           return mode;
       }

       public AlgoPanel getAlgoPanel() {
           return algo;
       }

       public MdfPanel getMdf1Panel() {
           return mdf1;
       }

       public MdfPanel getMdf2Panel() {
           return mdf2;
       }

       public ClearMapPanel getClearMapPanel() {
           return exit;
       }

       public LoadSavePanel getLoadSavePanel() {
           return load;
       }
       
       public void setMdf1(String mdf){
           mdf1.setMdf(mdf);
       }
       
       public void setMdf2(String mdf){
           mdf2.setMdf(mdf);
       }
    
}
