/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import processing.core.PApplet;
import processing.core.PVector;
import remixlab.proscene.Camera;
import remixlab.proscene.DrawingUtils;
import remixlab.proscene.Scene;
import processing.opengl.*;
/**
 *
 * @author samuel
 */
public class Cam2 extends PApplet {
   
    private volatile static Cam2 cam2;//volatile is needed so that multiple thread can reconcile the instance

    private Cam2() {
    }

    // For lazy initialization
    public static synchronized Cam2 getInstance() {
        if (cam2 == null) {
            cam2 = new Cam2();
        }
        return cam2;
    }
    
    boolean drawCam1 = false;
    
    static Scene scene2;
    //Scene scene2;

    public void setup() {
        this.size(640, 360, P3D);
    	//this.size(640, 360, OPENGL);
        scene2 = new Scene(this);
        scene2.setRadius(1000);
        scene2.showAll();
        scene2.setGridIsDrawn(true);
        scene2.setAxisIsDrawn(true);
        scene2.setHelpIsDrawn(false);
        drawCam1 = true;
    }

    public void draw() {        
        background(0);
        scene2.beginDraw();
        DrawingCommands.cmmnds(this);
        if (drawCam1)
        	DrawingUtils.drawCamera(this, Cam1.getScene().camera());
        scene2.endDraw();       
    }
    
    public static Scene getScene() {
    	return scene2;
    }

    public void keyPressed() {
        scene2.defaultKeyBindings();
        if (key == 'x')
        	drawCam1 = !drawCam1;
    }
}
