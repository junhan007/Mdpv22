/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import javax.swing.JButton;
import javax.swing.JPanel;
/**
 *
 * @author denis
 */
public class LoadSavePanel extends JPanel {


    JButton load;
    JButton save; 
 
    public LoadSavePanel(){
        load = new JButton("Load Map");
        save = new JButton("Save Map");
        this.add(load);
        this.add(save);
    }
    
    public JButton getLoadButton() {
        return load;
    }

    public JButton getSaveButton() {
        return save;
    }
    
    
}

