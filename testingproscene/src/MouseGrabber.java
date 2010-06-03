import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class MouseGrabber extends PApplet {
	Scene scene;
	Box [] boxes;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);		
		scene.setGridIsDrawn(true);		
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(1.5f);		
		scene.showAll();
		
		boxes = new Box[30];
		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = new Box(this);
			boxes[i].setSize(0.2f, 0.2f, 0.2f);
			boxes[i].setColor(color(0,0,255));
		}
	}

	public void draw() {
		//set camera stuff, always necessary:
		background(0);
				
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.
		//background(0);
		for (int i = 0; i < boxes.length; i++) {
			boxes[i].draw(true);
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
