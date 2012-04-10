package basic;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	InteractiveAvatarFrame iFrame;
	
	public void setup()	{
		//size(640, 360, P3D);
		//size(400, 400, P3D);
		size(640, 360, OPENGL);
		scene = new Scene(this);
		//scene.camera().setKind(Camera.Kind.STANDARD);
		//scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);		
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		//iFrame = new InteractiveAvatarFrame(scene);
		//scene.setInteractiveFrame(iFrame);
		scene.interactiveFrame().translate(new PVector(30, 30, 0));
		// press 'i' to switch the interaction between the camera frame and the interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setFrameSelectionHintIsDrawn(true);
	}

	public void draw() {		
		background(0);
		fill(204, 102, 0);
		box(20, 30, 40);  		
		// /**
		
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		scene.interactiveFrame().applyTransformation();//very efficient
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		
		// */
		
		// /**
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
		// */	
		
		popMatrix();	
		
		/**
		stroke(255);
		scene.beginScreenDrawing();
		PVector vec1 = scene.screenCoordinates(8,8);
		PVector vec2 = scene.screenCoordinates(width,height);
		line(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
		scene.endScreenDrawing();
		// */
	}
	
	/**
	public void keyPressed() {
		if((key == 'u')) {
			scene.setAvatar(iFrame);		
			//for (String s: scene.keys())
				//print(s);
		}		
		if (key == 'U') {
			scene.unsetAvatar();
			//for (String s: scene.keys())
				//print(s);
		}
	}
	*/
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "FrameInteraction" });
	}
}
