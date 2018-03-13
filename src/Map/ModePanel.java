/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author denis
 */
public class ModePanel extends JPanel {
   
   JRadioButton phy = null;
   JRadioButton simu = null; 
   ButtonGroup bg;
   
 public ModePanel() {
     
     
    phy = new JRadioButton("Physical");
    simu = new JRadioButton("Simulation"); 
    bg = new ButtonGroup();
    bg.add(phy);
    bg.add(simu);
    this.add(phy);
    this.add(simu);
     
     
 }

    public JRadioButton getPhyButton() {
        return phy;
    }

    public JRadioButton getSimuButton() {
        return simu;
    }
    
    public Boolean getMode(){
        if(phy.isSelected()){
            return false;
        }
        return true;
    }
}

