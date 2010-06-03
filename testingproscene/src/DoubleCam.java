/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Panel;
import processing.core.PApplet;
import processing.core.PVector;

/**
 *
 * @author nakednous
 */
public class DoubleCam extends PApplet {

    public void setup() {    	
    	PVector v = new PVector(100, 70, 130);		
		BoxNode.Root = new BoxNode(v, PVector.mult(v, -1.0f));
		BoxNode.Root.buildBoxHierarchy(4);
    	
    	Panel p = new Panel();
        Cam1.getInstance().init();
        p.add(Cam1.getInstance());
        add(p);
        
        Panel p2 = new Panel();
        Cam2.getInstance().init();
        p2.add(Cam2.getInstance());
        add(p2);        

        setSize(1100, 700);
    }
}
