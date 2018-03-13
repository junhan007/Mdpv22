/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;


/**
 *
 * @author denis
 */
public class AlgoPanel extends JPanel {
    JButton fast;
    JButton explore;
    
    public AlgoPanel(){
        fast = new JButton("Fastest Path");
        explore = new JButton("Exploration");


        this.add(fast);
        this.add(explore);
        System.out.println("test");
    }

        public JButton getFastestPathButton() {
            return fast;
        }

        public JButton getExploreButton() {
            return explore;
        }
            
}