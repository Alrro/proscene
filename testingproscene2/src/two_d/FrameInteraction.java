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
      scene = new Scene(this);
		
	  //size(640, 360, JAVA2D);
	  //scene = new Java2DScene(this);
	  
	  // A Scene has a single InteractiveFrame (null by default). We set it here.
	  scene.setInteractiveFrame(new InteractiveFrame(scene));
	  scene.interactiveFrame().translate(new Vector3D(30, 30));
	  // press 'i' to switch the interaction between the camera frame and the interactive frame
	  scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
	  // press 'f' to display frame selection hints
	  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
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
	}
	
	public void keyPressed() {
		if(key == 'x' || key == 'X') {
			println(scene.viewWindow().projectedCoordinatesOf(scene.interactiveFrame().position()));
		}
		if(key == 'u' || key == 'U') {
			scene.interactiveFrame().scale(1, -1);
		}		
		if(key == 'v' || key == 'V') {
			scene.viewWindow().flip();			
		}
		if(key == 'z' || key == 'Z') {
			print("magnitude: ");
			scene.pinhole().frame().magnitude().print();
		}
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");		
		if(scene.interactiveFrame().isInverted())
			println("scene.interactiveFrame() is inverted");
		else
			println("scene.interactiveFrame() is NOT inverted");
	}
	
	public class Java2DScene extends Scene {
		public Java2DScene(PApplet p) {
			super(p);
		}

		@Override
		public void applyTransformation(VFrame frame) {
			if( is2D() ) {
				if(renderer() instanceof RendererJava2D) {
					//if( isRightHanded() ) translate(0,-2*frame.translation().y());
					if( isRightHanded() ) scale(1,-1);		
					translate(frame.translation().x(), frame.translation().y());					
					rotate(frame.rotation().angle());					
					scale(frame.scaling().x(), frame.scaling().y());					
					//println(frame.translation().y());
				}
				else
					super.applyTransformation(frame);
			}
			else {
				translate( frame.translation().vec[0], frame.translation().vec[1], frame.translation().vec[2] );
				rotate( frame.rotation().angle(), ((Quaternion)frame.rotation()).axis().vec[0], ((Quaternion)frame.rotation()).axis().vec[1], ((Quaternion)frame.rotation()).axis().vec[2]);
				scale(frame.scaling().x(), frame.scaling().y(), frame.scaling().z());
			}
		}
	}
}
