import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {
	Scene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this); 
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
