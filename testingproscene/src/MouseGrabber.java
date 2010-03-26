import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class MouseGrabber extends PApplet {
	Scene scene;
	Box [] boxes;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);		
		scene.setGridIsDrawn(true);		
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(2);		
		scene.showAll();
		
		boxes = new Box[6];
		for (int i = 0; i < 6; i++) {
			boxes[i] = new Box(this);
			boxes[i].setPosition(new PVector((-1.0f + (i*0.4f )), 0.0f, 0.0f));
		}
	}

	public void draw() {
		//set camera stuff, always necessary:
		background(0);
				
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.
		//background(0);		
		for (int i = 0; i < 6; i++) {
			boxes[i].draw();
		}		    
		scene.endDraw();
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "MouseGrabber" });
	}
}
