package basic;
import processing.core.PApplet;
import processing.core.PVector;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Moire extends PApplet  {
Scene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		//size(640, 360, P3D);
		scene = new Scene(this);
		scene.setAxisIsDrawn(true);
		scene.setRadius(150);		
		scene.showAll();
	}

	public void draw() {		
		background(0);
		final float nbLines = 50.0f;
		
		beginShape(LINES);		
		for (float i=0; i<nbLines; ++i) {
			float angle = 2.0f*PI*i/nbLines;
			
			stroke(204, 51, 51);
			// These lines will never be seen as they are always aligned with the viewing direction.
			vertex(scene.camera().position().x, scene.camera().position().y, scene.camera().position().z);
			vertex(100*cos(angle), 100*sin(angle), 0);			
			
			stroke(55, 204, 55);
			// World Coordinates are infered from the camera, and seem to be immobile in the screen.
			PVector tmp = scene.camera().worldCoordinatesOf(new PVector(30*cos(angle), 30*sin(angle), -200));			
			vertex(tmp.x, tmp.y, tmp.z);
			vertex(100*cos(angle), 100*sin(angle), 0);
			
			stroke(55, 55, 204);
		    // These lines are defined in the world coordinate system and will move with the camera.
		    vertex(150*cos(angle), 150*sin(angle), -100);
		    vertex(100*cos(angle), 100*sin(angle), 0);		    
		}	
		endShape();
	}	
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Moire" });
	}
}
