/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import remixlab.proscene.Camera;
import remixlab.proscene.Scene;
import processing.opengl.*;

/**
 *
 * @author samuel
 */
public class Cam1 extends PApplet {

    private volatile static Cam1 cam1;//volatile is needed so that multiple thread can reconcile the instance
    private Cam1() {
    }

    // For lazy initialization
    public static synchronized Cam1 getInstance() {
        if (cam1 == null) {
            cam1 = new Cam1();
        }
        return cam1;
    }
    static Scene scene;
    static CullingCamera cullingCamera;
    //Scene scene;

    public void setup() {
        size(640, 360, P3D);
        //size(640, 360, OPENGL);
        scene = new Scene(this);
        cullingCamera = new CullingCamera(this);
        scene.setCamera(cullingCamera);
        scene.setHelpIsDrawn(false);
        scene.setGridIsDrawn(true);
        scene.setAxisIsDrawn(true);
    }
    
    public void draw() {        
        background(0);
        scene.beginDraw();
        //cullingCamera.computeFrustumPlanesEquations();
        DrawingCommands.cmmnds(this);
        cullingCamera.computeFrustumPlanesEquations();
        scene.endDraw();	
    }
    
    public static Scene getScene() {
    	return scene;
    }
}
