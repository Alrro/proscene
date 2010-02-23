import processing.core.*;
import processing.opengl.*;
import proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class InteractiveFrame extends PApplet {
	PScene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new PScene(this); 
		scene.setCameraType(PSCamera.Type.ORTHOGRAPHIC);
		scene.setSceneRadius(scene.sceneRadius()*1.3f);
		scene.showEntireScene();
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		scene.setInteractiveFrame(new PSInteractiveFrame());
		scene.interactiveFrame().translate(new PVector(0.2f, 0.2f, 0));
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
		PScene.drawAxis();
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
		PApplet.main(new String[] { "--present", "test.InteractiveFrame" });
	}
}
