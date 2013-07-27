package basic;
import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Moire extends PApplet  {
Scene scene;
Vec camPos;
	
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
			camPos = scene.camera().position();
			//vertex(camPos.x(), camPos.y(), 391);
			//println(camPos);
			vertex(camPos.x(), camPos.y(), camPos.z());			
			vertex(100*cos(angle), 100*sin(angle), 0);			
			
			stroke(55, 204, 55);
			// World Coordinates are infered from the camera, and seem to be immobile in the screen.
			Vec tmp = scene.camera().worldCoordinatesOf(new Vec(30*cos(angle), 30*sin(angle), -200));			
			vertex(tmp.x(), tmp.y(), tmp.z());
			vertex(100*cos(angle), 100*sin(angle), 0);
			
			stroke(55, 55, 204);
		    // These lines are defined in the world coordinate system and will move with the camera.
		    vertex(150*cos(angle), 150*sin(angle), -100);
		    vertex(100*cos(angle), 100*sin(angle), 0);		    
		}	
		endShape();
	}
	
	public void keyPressed() {
		if( key == 'x' || key == 'X' ) {
			println(camPos);
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Moire" });
	}
}
