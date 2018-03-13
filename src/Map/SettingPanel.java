/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author junha
 */
public class SettingPanel extends JPanel{
    JTextArea settings;
    JLabel label; 
        
    public SettingPanel(String labelstr){
        settings = new JTextArea();
        settings.setPreferredSize(new Dimension(50,20));
        label = new JLabel(labelstr);
        this.add(label);
        this.add(settings);
    }
    public int getSettings(){
        //int val;
        System.out.println(this.settings.getText());
        if(this.settings.getText().equals("")){
            if(this.label.getText() == "Delay Speed"){
                return 100;
            } else if(this.label.getText() == "Time Limit"){
                return 3600;
            } else if(this.label.getText() == "Coverage (grid)"){
                return 300;
            }
        }
        return Integer.parseInt(this.settings.getText());
    }
    
}