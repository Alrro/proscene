import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Luxo extends PApplet {
	Scene scene;
	Lamp lamp;
	boolean visualHint;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setRadius(100);
		scene.showAll();
		scene.setHelpIsDrawn(false);
		scene.setFrameSelectionHintIsDrawn(true);
		lamp = new Lamp(this);
		//visualHint = true;
	}
	
	public void draw() {
		background(0);
		lights();
		scene.beginDraw();
		lamp.draw();
		/**
		if(visualHint)
			drawFrameVisualHint();
			*/
		//draw the ground
		noStroke();
		fill(120, 120, 120);
		float nbPatches = 100;
		normal(0.0f,0.0f,1.0f);
		for (int j=0; j<nbPatches; ++j) {
			beginShape(QUAD_STRIP);
			for (int i=0; i<=nbPatches; ++i) {
				/**
				vertex((2f*(float)i/nbPatches-1.0f), (2*j/nbPatches-1.0f));
				vertex((2f*(float)i/nbPatches-1.0f), (2*(float)(j+1)/nbPatches-1.0f));
				*/
				vertex((200*(float)i/nbPatches-100), (200*j/nbPatches-100));
				vertex((200*(float)i/nbPatches-100), (200*(float)(j+1)/nbPatches-100));
			}
			endShape();
		}
		scene.endDraw();
	}
	/**
	public void drawFrameVisualHint() {
		for (int i=0; i<4; ++i) {
			PVector center = scene.camera().projectedCoordinatesOf(lamp.frame(i).position());
			if (lamp.frame(i).grabsMouse())
				filledCircle(color(255,0,0),center,5);
			else
				filledCircle(color(0,255,0),center,5);
			}
	}
	
	public void filledCircle(int color, PVector center, float radius) {
		float x = center.x;
		float y = center.y;
		float angle, x2, y2;
		noStroke();
		fill(color);
		scene.beginScreenDrawing();
		beginShape(TRIANGLE_FAN);
		vertex(Scene.xCoord(x), Scene.yCoord(y), Scene.zCoord());
		for (angle=0.0f;angle<=TWO_PI; angle+=0.157f) {
		    x2 = x+sin(angle)*radius;
		    y2 = y+cos(angle)*radius;
		    vertex(Scene.xCoord(x2), Scene.yCoord(y2), Scene.zCoord());
		}		
		endShape();
		scene.endScreenDrawing();
	}
	*/
	
	public void keyPressed() {
		scene.defaultKeyBindings();
		/**
		if (key == 'f' || key == 'F') {
			visualHint = !visualHint;
		}
		*/
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Luxo" });
	}
}
