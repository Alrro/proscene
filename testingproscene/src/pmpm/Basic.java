package pmpm;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Basic extends PApplet {	
	Scene scene;
	PVector wVec, cVec;
	PVector screenPos = new PVector(500, 100, 0.5f);
	InteractiveFrame iFrame;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
	  iFrame = new InteractiveFrame(scene);
	  iFrame.translate(10,15,10);
	  
	  //scene.unregisterCameraProfile("WHEELED_ARCBALL");
	  scene.registerCameraProfile( new CameraProfile(scene, "CAD_ARCBALL", CameraProfile.Mode.CAD_ARCBALL) );
	  scene.setCurrentCameraProfile("CAD_ARCBALL");
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);	  
	  pushMatrix();	 
	  fill(104, 20, 130);	  
	  iFrame.applyTransformation();
	  scene.drawAxis(20);
	  box(20, 30, 50);	  
	  popMatrix();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
