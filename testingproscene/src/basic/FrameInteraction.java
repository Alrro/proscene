package basic;
import processing.core.*;
import processing.event.Event;
//import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	//InteractiveAvatarFrame iFrame;
	
	public void setup()	{	
		size(640, 360, P3D);
		scene = new Scene(this);	
		
		scene.registerCameraProfile( new CameraProfile(scene, "CAD_ARCBALL", CameraProfile.Mode.CAD) );
		
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
		//scene.camera().setStandardZNear(1);
		scene.camera().setStandardZNear(1f);
		
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		//iFrame = new InteractiveAvatarFrame(scene);
		//scene.setInteractiveFrame(iFrame);
		scene.interactiveFrame().translate(new PVector(60, 30, 10));
		// press 'i' to switch the interaction between the camera frame and the interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setFrameSelectionHintIsDrawn(true);
		
		// /**
		scene.camera().frame().setSpinningFriction(0.5f);
		scene.camera().frame().setTossingFriction(0.5f);
		// */
		
		// /**
		scene.interactiveFrame().setSpinningFriction(0.5f);
		scene.interactiveFrame().setTossingFriction(0.5f);
		// */				
	}

	public void draw() {	
		background(0);
		fill(204, 102, 0);
		box(20, 30, 40);		
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		scene.interactiveFrame().applyTransformation();//very efficient
		// Draw an axis using the Scene static function
		scene.drawAxis(20);				
		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			fill(255, 0, 0);
			box(12, 17, 22);
		}
		else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(12, 17, 22);
		}
		else {
			fill(0,0,255);
			box(10, 15, 20);
		}			
		popMatrix();		
	}
		
	public void keyPressed() {
		if(key == 't' || key == 'T') {
			if( scene.camera().isAttachedToP5Camera() ) {
				scene.camera().detachFromP5Camera();
				println("cam matrices detached");
			}
			else {
				scene.camera().attachToP5Camera();
				println("cam matrices attached");
			}
		}
		if(key == 'u' || key == 'U') {
			if( scene.isRightHanded() ) {
				scene.setLeftHanded();
				println("left handed set");
			}
			else {
				scene.setRightHanded();
				println("right handed set");
			}
		}
		// /**
		if (key == 'x' || key == 'X')
			scene.camera().frame().setCADAxis(new PVector(1, 0, 0));
		if (key == 'y' || key == 'Y')
		    scene.camera().frame().setCADAxis(new PVector(0, 1, 0));
		if (key == 'z' || key == 'Z')
		    scene.camera().frame().setCADAxis(new PVector(0, 0, 1));
		// */
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.FrameInteraction" });
	}
}
