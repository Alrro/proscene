package two_d;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class FrameInteraction extends PApplet {	
	Scene scene;

	public void setup() {
	  size(640, 360, P2D);
	  //size(640, 360, JAVA2D);
      scene = new Scene(this);	  
	  // A Scene has a single InteractiveFrame (null by default). We set it here.
	  scene.setInteractiveFrame(new InteractiveFrame(scene));
	  scene.interactiveFrame().translate(new Vector3D(30, 30));
	  // press 'i' to switch the interaction between the camera frame and the interactive frame
	  scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
	  // press 'f' to display frame selection hints
	  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
	  scene.setFrameSelectionHintIsDrawn(true);
	}

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  rect(0, 0, 55, 55);
	  // Save the current model view matrix
	  pushMatrix();
	  // Multiply matrix to get in the frame coordinate system.
	  // applyMatrix(scene.interactiveFrame().matrix()) is handy but inefficient	  
	  scene.interactiveFrame().applyTransformation();//optimum
	  // Draw an axis using the Scene static function
	  scene.drawAxis(40);
	  // Draw a second box attached to the interactive frame
	  if (scene.interactiveFrame().grabsMouse()) {
	    fill(255, 0, 0);
	    rect(0, 0, 35, 35);
	  }
	  else if (scene.interactiveFrameIsDrawn()) {
	    fill(0, 255, 255);
	    rect(0, 0, 35, 35);
	  }
	  else {
	    fill(0, 0, 255);
	    rect(0, 0, 30, 30);
	  }    
	  popMatrix();
	  
	  scene.beginScreenDrawing();
	  text("Hello world", 5, 17);
	  scene.endScreenDrawing();
	}
	
	public void keyPressed() {
		if(key == 'x') 
			scene.interactiveFrame().scale(-1, 1);
		if(key == 'X') 
			scene.viewWindow().frame().scale(-1, 1);
		if(key == 'y') 
			scene.interactiveFrame().scale(1, -1);
		if(key == 'Y') 
			scene.viewWindow().frame().scale(1, -1);
		
		/**
		if(key == 't' || key == 'T')
			scene.interactiveFrame().scale(-1, 1);		
		if(key == 'u' || key == 'U') 
			scene.viewWindow().frame().scale(1, -1);
		*/		
		
		if(key == 'v' || key == 'V') 
			scene.viewWindow().flip();			
		
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");		
		if(scene.interactiveFrame().isInverted())
			println("scene.interactiveFrame() is inverted");
		else
			println("scene.interactiveFrame() is NOT inverted");
		if(scene.viewWindow().frame().isInverted())
			println("scene.viewWindow().frame() is inverted");
		else
			println("scene.viewWindow().frame() is NOT inverted");
	}	
}
