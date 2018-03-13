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
public class ClearMapPanel extends JPanel {

    JButton clear;
    JButton stop;

    public ClearMapPanel() {
        clear = new JButton("Clear Map");
        stop = new JButton("Stop");
        this.add(clear);
        this.add(stop);

    }

    public JButton getClearButton() {
        return clear;
    }

    public JButton getStopButton() {
        return stop;
    }

}

