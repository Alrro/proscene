package basic;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class CamRelDrawing extends PApplet {
	Scene scene;
	InteractiveFrame iFrame;

	public void setup() {
	  size(640, 360, P3D);		
	  scene = new Scene(this);
	  iFrame = new InteractiveFrame(scene);
	  iFrame.setReferenceFrame(scene.camera().frame());
	  iFrame.translate(40, 40, -180);
	  LocalConstraint constraint = new LocalConstraint();
	  constraint.setTranslationConstraintType(AxisPlaneConstraint.Type.FORBIDDEN);
	  iFrame.setConstraint(constraint);
	  scene.setInteractiveFrame(iFrame);
	  // press 'i' to switch the interaction between the camera frame and the interactive frame
	  scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
	  // press 'f' to display frame selection hints
	  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
	}

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 40);		

	  // first level: draw respect to the camera frame
	  pushMatrix();
	  scene.camera().frame().applyTransformation();
	  // second level: draw respect to the iFrame
	  pushMatrix();		
	  iFrame.applyTransformation();
	  scene.drawAxis(20);

	  // Draw a second box
	  if (scene.interactiveFrame().grabsMouse()) {
	    fill(255, 0, 0);
	    box(12, 17, 22);
	  }
	  else  if (scene.interactiveFrameIsDrawn()) {
	    fill(0, 255, 255);
	    box(12, 17, 22);
	  }
	  else {
	    fill(0, 0, 255);
	    box(10, 15, 20);
	  }	

	  popMatrix();		
	  popMatrix();
	}
}
