package basic;

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
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  //box(20, 30, 50);
	  
	  wVec = scene.camera().unprojectedCoordinatesOf(screenPos);
	  //cVec = scene.camera().cameraCoordinatesOf(wVec);
	  
	  iFrame.translate(wVec);
	  
	  pushMatrix();	 
	  //fill(104, 20, 130);	  
	  iFrame.applyTransformation();
	  scene.drawAxis(20);
	  box(20, 30, 50);	  
	  popMatrix();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
