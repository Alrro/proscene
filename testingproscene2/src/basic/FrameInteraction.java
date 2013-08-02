package basic;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	boolean focusIFrame;
	InteractiveAvatarFrame iFrame;
	
	public void setup()	{
		size(640, 360, P3D);		
		scene = new Scene(this);	
		iFrame = new InteractiveAvatarFrame(scene);
		iFrame.translate(new Vec(30, 30, 0));
		scene.setJavaTimers();
		//scene.prosceneMouse.setAsThirdPersonBindings();
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
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.FrameInteraction" });
	}
}
