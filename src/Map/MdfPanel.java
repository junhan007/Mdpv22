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
public class MdfPanel extends JPanel{
    JTextArea mdf;
    JLabel label; 
    
    public MdfPanel(String labelstr){
        mdf = new JTextArea();
        mdf.setPreferredSize(new Dimension(180,30));
        label = new JLabel(labelstr);
        this.add(label);
        this.add(mdf);
    }
    
    public String getMdf(){
        return this.mdf.getText();
    }
    
    public void setMdf(String mdf){
        this.mdf.setText(mdf);
    }
  
    
    
}