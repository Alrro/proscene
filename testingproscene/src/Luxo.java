import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Luxo extends PApplet {
	Scene scene;
	Lamp lamp;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);
		lamp = new Lamp(this);
	}
	
	public void draw() {
		background(0);
		scene.beginDraw();
		lamp.draw();
		//draw the ground
		noStroke();
		fill(120, 120, 120);
		float nbPatches = 10;
		normal(0.0f,0.0f,1.0f);
		for (int j=0; j<nbPatches; ++j) {
			beginShape(QUAD_STRIP );
			for (int i=0; i<=nbPatches; ++i) {
				vertex((2f*(float)i/nbPatches-1.0f), (2*j/nbPatches-1.0f));
				vertex((2f*(float)i/nbPatches-1.0f), (2*(float)(j+1)/nbPatches-1.0f));
			}
			endShape();
		}
		scene.endDraw();
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "test.Luxo" });
	}
}
