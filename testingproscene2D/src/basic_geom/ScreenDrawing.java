package basic_geom;
import geom.Box;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class ScreenDrawing extends PApplet {
	Scene scene;
	Box [] boxes;
	Point[] points;
	int LINE_ITEMS = 500;
	int index;
	PFont font;
	boolean onScreen = false;
	boolean additionalInstructions = false;
	
	public void setup()	{
		size(640, 360, P3D);
		font = createFont("Arial", 16);
		textFont(font, 16);
		scene = new Scene(this);
		scene.setRadius(150);
		scene.showAll();
		index = 0;
		points = new Point [LINE_ITEMS];		
		boxes = new Box[50];
		for (int i = 0; i < boxes.length; i++)
			boxes[i] = new Box(scene);
	}

	public void draw() {	
		background(0);
		  // A. 3D drawing
		  for (int i = 0; i < boxes.length; i++)
		    boxes[i].draw();
		    
		  // B. 2D drawing on top of the 3d scene 
		  // All screen drawing should be enclosed between Scene.beginScreenDrawing() and
		  // Scene.endScreenDrawing(). Then you can just begin drawing your screen shapes
		  // (defined between beginShape() and endShape()).
		  scene.beginScreenDrawing();
		  pushStyle();
		  strokeWeight(8);
		  stroke(183,67,158,127);
		  noFill();
		  beginShape();
		  for (int i = 0; i < points.length; i++)    
		    vertex((float) ((Point) points[i]).x, (float) ((Point) points[i]).y, 1);
		  endShape();  
		  popStyle();
		  scene.endScreenDrawing();
		  
		  // C. Render text instructions.
		  scene.beginScreenDrawing();
		  if(onScreen)
		    text("Press 'x' handle 3d scene", 5, 17);
		  else
		    text("Press 'x' to begin screen drawing", 5, 17);
		  if(additionalInstructions)
		    text("Press 'y' to clear screen", 5, 35);
		  scene.endScreenDrawing();  
	}
	
	// /**
	public void keyPressed() {
		if((key == 't') || (key == 'T')) {
			scene.toggleMouseHandling();			
		}
		if (key == 'x')
			index = 0;
	}
	// */
	
	public void mouseDragged() {
		if(!scene.mouseIsHandled()) {			
			if (index < LINE_ITEMS ) {
				points[index] = new Point (mouseX, mouseY);
				index++;
			} else
				index = 0;			
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "ScreenDrawing" });
	}
}
