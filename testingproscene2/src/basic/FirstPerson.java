package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF0Action;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.dandelion.constraint.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.tersehandling.core.EventConstants;
import remixlab.tersehandling.core.EventGrabberTuple;
import remixlab.tersehandling.core.Grabbable;
import remixlab.tersehandling.generic.event.GenericDOF2Event;
import remixlab.tersehandling.generic.event.GenericKeyboardEvent;

public class FirstPerson extends PApplet {
	Scene scene;
	boolean focusIFrame;
	boolean firstPerson = true;
	InteractiveFrame iFrame;
	
	public void setup()	{
		size(640, 360, P3D);		
		scene = new Scene(this);	
		iFrame = new InteractiveFrame(scene);
		iFrame.translate(new Vec(30, 30, 0));
		
		scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.MOVE_FORWARD);
		scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_CENTER, DOF2Action.LOOK_AROUND);
		scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_RIGHT, DOF2Action.MOVE_BACKWARD);
		scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_LEFT, DOF2Action.ROLL);
		scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_CENTER, DOF2Action.DRIVE);
		
		scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.MOVE_FORWARD);
		scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_CENTER, DOF2Action.LOOK_AROUND);
		scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_RIGHT, DOF2Action.MOVE_BACKWARD);
		scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_LEFT, DOF2Action.ROLL);
		scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_CENTER, DOF2Action.DRIVE);
	}

	public void draw() {
		background(0);
		fill(204, 102, 0);
		box(20, 30, 40);		
		
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(iFrame.matrix()) is possible but inefficient 
		iFrame.applyTransformation();//very efficient
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		
		// Draw a second box
		if (focusIFrame) {
			fill(0, 255, 255);
			box(12, 17, 22);
		}
		else if (iFrame.grabsAgent(scene.prosceneMouse)) {
			fill(255, 0, 0);
			box(12, 17, 22);
		}
		else {
			fill(0,0,255);
			box(10, 15, 20);
		}	
		
		popMatrix();
	}
	
	public void keyPressed() {
		if( key == 'i') {
			if( focusIFrame ) {
				scene.prosceneMouse.setDefaultGrabber(scene.pinhole().frame());
				scene.prosceneMouse.enableTracking();
			} else {
				scene.prosceneMouse.setDefaultGrabber(iFrame);
				scene.prosceneMouse.disableTracking();
			}
			focusIFrame = !focusIFrame;
		}
		if( key == ' ') {
			if ( firstPerson ) {
				//copy & paste from MouseAgent:
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.ROTATE);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_CENTER, DOF2Action.ZOOM);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_RIGHT, DOF2Action.TRANSLATE);	
				
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_LEFT, DOF2Action.ZOOM_ON_REGION);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_CENTER, DOF2Action.SCREEN_TRANSLATE);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_RIGHT, DOF2Action.SCREEN_ROTATE);
				
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.ROTATE);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_CENTER, DOF2Action.ZOOM);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_RIGHT, DOF2Action.TRANSLATE);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_CENTER, DOF2Action.SCREEN_TRANSLATE);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_RIGHT, DOF2Action.SCREEN_ROTATE);
			}
			else {
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.MOVE_FORWARD);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_CENTER, DOF2Action.LOOK_AROUND);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_RIGHT, DOF2Action.MOVE_BACKWARD);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_LEFT, DOF2Action.ROLL);
				scene.prosceneMouse.cameraProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_CENTER, DOF2Action.DRIVE);
				
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.MOVE_FORWARD);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_CENTER, DOF2Action.LOOK_AROUND);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_RIGHT, DOF2Action.MOVE_BACKWARD);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_LEFT, DOF2Action.ROLL);
				scene.prosceneMouse.frameProfile().setBinding(EventConstants.TH_SHIFT, EventConstants.TH_CENTER, DOF2Action.DRIVE);			
			}
			firstPerson = !firstPerson;
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.FirstPerson" });
	}
}
