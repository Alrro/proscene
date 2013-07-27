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
		if(key == 'x') iFrame.scale(-1, 1, 1);
		//if(key == 'X') scene.camera().frame().scale(-1, 1, 1);
		if(key == 'y') iFrame.scale(1, -1, 1);
		//if(key == 'Y') scene.camera().frame().scale(1, -1, 1);
		if(key == 'z') iFrame.scale(1, 1, -1);
		//if(key == 'Z') scene.camera().frame().scale(1, 1, -1);
		
		if(key == 'v' || key == 'V') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();			
		}			
		
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");		
		if(iFrame.isInverted())
			println("iFrame is inverted");
		else
			println("iFrame is NOT inverted");
		
		/**
		if(scene.camera().frame().isInverted())
			println("scene.camera().frame() is inverted");
		else
			println("scene.camera().frame() is NOT inverted");
		*/
		
		if( key == 'u' || key == 'U') {
			print("cam pos: ");
			scene.camera().position().print();
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.FrameInteraction" });
	}
}
