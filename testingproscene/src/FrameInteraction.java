import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this); 
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);		
		scene.setInteractiveFrame(new InteractiveFrame());
		scene.interactiveFrame().translate(new PVector(0.3f, 0.3f, 0));
	}

	public void draw() {
		background(0);
		// set camera stuff, always necessary:
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.		
		fill(204, 102, 0);
		box(0.2f, 0.3f, 0.4f);
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		scene.interactiveFrame().applyTransformation(this);//very efficient
		// Draw an axis using the Scene static function
		Scene.drawAxis(0.7f);
		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			fill(255, 0, 0);
			box(0.12f, 0.17f, 0.22f);
		}
		else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(0.12f, 0.17f, 0.22f);
		}
		else {
			fill(0,0,255);
			box(0.1f, 0.15f, 0.2f);
		}		
		popMatrix();
		scene.endDraw();
	}	
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "test.FrameInteraction" });
	}
}
