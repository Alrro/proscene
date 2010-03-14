import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class MovableFrame extends PApplet {
	Scene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this); 
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(scene.radius()*1.3f);
		scene.showAll();
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);		
		scene.setInteractiveFrame(new InteractiveFrame());
		scene.interactiveFrame().translate(new PVector(0.2f, 0.2f, 0));
		scene.setDrawInteractiveFrame(true);
	}

	public void draw() {
		background(0);
		//set camera stuff, always necessary:
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.
		//background(0);
		fill(204, 102, 0);
		box(0.2f, 0.3f, 0.5f);
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		//applyMatrix( scene.interactiveFrame().pMatrix() );
		//Same as the previous commented line, but a lot more efficient:
		scene.interactiveFrame().applyTransformation(this);
		// Draw an axis using the GLScene static function
		Scene.drawAxis();
		// Draw a second box
		fill(255, 0, 0);
		box(0.1f, 0.15f, 0.25f);
		popMatrix();
		scene.endDraw();
	}	
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "test.MovableFrame" });
	}
}
