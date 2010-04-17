import java.awt.Point;

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
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);		
		scene.setRadius(scene.radius() * 1.5f);
		scene.showAll();
		index = 0;
		points = new Point [LINE_ITEMS];		
		boxes = new Box[50];
		for (int i = 0; i < boxes.length; i++)
			boxes[i] = new Box(this);
	}

	public void draw() {
		//set camera stuff, always necessary:
		background(0);
				
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.
		
		//3D drawing
		for (int i = 0; i < boxes.length; i++)			
			boxes[i].draw();
		
		//2D drawing
		scene.beginScreenDrawing();
		strokeWeight(8);
		stroke(183,67,158,127);
		noFill();
		beginShape();
		for (int i=0; i<index; i++)
			vertex(Scene.xCoord( points[i].x ), Scene.yCoord( points[i].y ), Scene.zCoord());
		endShape();
		strokeWeight(1);		
		scene.endScreenDrawing();
	
		scene.endDraw();
	}
	
	public void keyPressed() {	
		if(key == 'y' && mousePressed) {
			scene.enableMouseHandling(false);
			if (index < LINE_ITEMS ) {
				points[index] = new Point (mouseX, mouseY);
				index++;
			} else
				index = 0;			
		}
		else {
			scene.enableMouseHandling(true);
			if (key == 'x')
				index = 0;
			else
				scene.defaultKeyBindings();
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "ScreenDrawing" });
	}
}
