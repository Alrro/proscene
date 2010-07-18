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
		scene.setRadius(150);		
		scene.showAll();
		
		boxes = new Box[30];
		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = new Box(scene);
			boxes[i].setSize(20, 20, 20);
			boxes[i].setColor(color(0,0,255));
		}
	}

	public void draw() {
		for (int i = 0; i < boxes.length; i++) {
			boxes[i].draw(true);
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "MouseGrabber" });
	}
}
