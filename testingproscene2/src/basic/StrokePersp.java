package basic;

import processing.core.*;
import processing.opengl.*;

public class StrokePersp extends PApplet {
	PGraphics3D pg;	
	
	public void setup() {
		size(640, 360, P3D);		
		pg = (PGraphics3D) g;		
	}
	
	public void draw() {		
		background(0);
		//ortho(0, width, 0, height, -10, 10);
		ortho(-width/2, width/2, -height/2, height/2, -10, 10);
		//translate(100, 100, 0);
		//rotateX(-PI/6);
		//rotateY(PI/3);
		drawAxis();	
	}
	
	public void drawAxis() {
		drawAxis(100);
	}
	
	public void drawAxis(float length) {
		final float charWidth = length / 40.0f;
		final float charHeight = length / 30.0f;
		final float charShift = 1.04f * length;

		// pg3d().noLights();

		pg3d().pushStyle();
		
		pg3d().beginShape(LINES);		
		pg3d().strokeWeight(2);
		// The X
		pg3d().stroke(200, 0, 0);
		pg3d().vertex(charShift, charWidth, -charHeight);
		pg3d().vertex(charShift, -charWidth, charHeight);
		pg3d().vertex(charShift, -charWidth, -charHeight);
		pg3d().vertex(charShift, charWidth, charHeight);
		// The Y
		pg3d().stroke(0, 200, 0);
		pg3d().vertex(charWidth, charShift, charHeight);
		pg3d().vertex(0.0f, charShift, 0.0f);
		pg3d().vertex(-charWidth, charShift, charHeight);
		pg3d().vertex(0.0f, charShift, 0.0f);
		pg3d().vertex(0.0f, charShift, 0.0f);
		pg3d().vertex(0.0f, charShift, -charHeight);
		// The Z
		pg3d().stroke(0, 100, 200);
		
		//left_handed
		pg3d().vertex(-charWidth, -charHeight, charShift);
		pg3d().vertex(charWidth, -charHeight, charShift);
		pg3d().vertex(charWidth, -charHeight, charShift);
		pg3d().vertex(-charWidth, charHeight, charShift);
		pg3d().vertex(-charWidth, charHeight, charShift);
		pg3d().vertex(charWidth, charHeight, charShift);
	  //right_handed coordinate system should go like this:
		//pg3d().vertex(-charWidth, charHeight, charShift);
		//pg3d().vertex(charWidth, charHeight, charShift);
		//pg3d().vertex(charWidth, charHeight, charShift);
		//pg3d().vertex(-charWidth, -charHeight, charShift);
		//pg3d().vertex(-charWidth, -charHeight, charShift);
		//pg3d().vertex(charWidth, -charHeight, charShift);
		
		pg3d().endShape();
		
	  /**
		// Z axis
		pg3d().noStroke();
		pg3d().fill(0, 100, 200);
		drawArrow(length, 0.01f * length);

		// X Axis
		pg3d().fill(200, 0, 0);
		pg3d().pushMatrix();
		pg3d().rotateY(HALF_PI);
		drawArrow(length, 0.01f * length);
		pg3d().popMatrix();

		// Y Axis
		pg3d().fill(0, 200, 0);
		pg3d().pushMatrix();
		pg3d().rotateX(-HALF_PI);
		drawArrow(length, 0.01f * length);
		pg3d().popMatrix();
		// */
		
	  // X Axis
		pg3d().stroke(200, 0, 0);
		pg3d().line(0, 0, 0, length, 0, 0);
	  // Y Axis
		pg3d().stroke(0, 200, 0);		
		pg3d().line(0, 0, 0, 0, length, 0);
		// Z Axis
		pg3d().stroke(0, 100, 200);
		pg3d().line(0, 0, 0, 0, 0, length);		

		pg3d().popStyle();
	}

	public PGraphics3D pg3d() {
		return pg;
	}
	
	
}
