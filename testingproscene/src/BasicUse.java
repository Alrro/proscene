import processing.core.*;
import processing.opengl.*;
import proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {
	PScene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		//size(640, 360, P3D);
		//size(300, 200, OPENGL);
		//size(300, 200, P3D);
		scene = new PScene(this); 
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
	}

	public void draw() {
		background(0);
		scene.beginDraw();
		fill(204, 102, 0);
		box(0.2f, 0.3f, 0.5f);		
		scene.endDraw();
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "test.BasicUse" });
	}
}
