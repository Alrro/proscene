import processing.core.PApplet;
import processing.core.PVector;
import proscene.*;

@SuppressWarnings("serial")
public class Moire extends PApplet  {
PScene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		//size(640, 360, P3D);
		scene = new PScene(this);
		scene.setAxisIsDrawn(true);
		scene.setSceneRadius(1.5f);		
		scene.showEntireScene();
	}

	public void draw() {
		background(0);
		scene.beginDraw();
		
		final float nbLines = 50.0f;
		
		beginShape(LINES);		
		for (float i=0; i<nbLines; ++i) {
			float angle = 2.0f*PI*i/nbLines;
			
			stroke(204, 51, 51);
			// These lines will never be seen as they are always aligned with the viewing direction.
			vertex(scene.camera().position().x, scene.camera().position().y, scene.camera().position().z);
			vertex(cos(angle), sin(angle), 0.0f);			
			
			stroke(55, 204, 55);
			// World Coordinates are infered from the camera, and seem to be immobile in the screen.
			PVector tmp = scene.camera().worldCoordinatesOf(new PVector(.3f*cos(angle), .3f*sin(angle), -2.0f));			
			vertex(tmp.x, tmp.y, tmp.z);
			vertex(cos(angle), sin(angle), 0.0f);
			
			stroke(55, 55, 204);
		    // These lines are defined in the world coordinate system and will move with the camera.
		    vertex(1.5f*cos(angle), 1.5f*sin(angle), -1.0f);
		    vertex(cos(angle), sin(angle), 0.0f);		    
		}	
		endShape();

		scene.endDraw();
	}	
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "test.Moire" });
	}
}
