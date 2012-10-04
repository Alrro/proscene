package two_d;

import processing.core.*;
import processing.opengl.*;

public class TwoScenes extends PApplet {
	PFont font;
	PGraphics canvas1, canvas2;
	String renderer = P2D;
	// if opengl is not supported comment the prev line and uncomment this:
	//String renderer = JAVA2D;
	//dim
	int w = 640;
	int h = 720; 
	
	public void setup() {
		size(w, h, renderer);
		canvas1 = createGraphics(width, height/2, renderer);
		canvas2 = createGraphics(width, height/2, renderer);
		font = createFont("Arial", 12);
		textFont(font, 12);
	}

	public void draw() {
		background(0);
		canvas1.beginDraw();
		canvas1.background(0);
		// call scene off-screen rendering on canvas 1
		drawScene(canvas1);
		canvas1.endDraw();
		// draw canvas onto screen
		image(canvas1, 0, 0);

		canvas2.beginDraw();
		canvas2.background(0);
		// call scene off-screen rendering on canvas 1
		drawScene(canvas2);
		canvas2.endDraw();
		// draw canvas onto screen
		image(canvas2, 0, 360);
	}

	void drawScene(PGraphics pg) {
		// draw world coord sys axes
		drawAxis(pg, 1);
		// draw a rect in the world coord sys
		pg.fill(0, 255, 255);
		pg.rect(0, 0, 40, 10, 5);
		// define a local coordinate system
		pg.pushMatrix();
		pg.translate(150, 120);
		pg.rotate(QUARTER_PI / 2);
		// draw a second rect respect to the local coordinate system
		drawAxis(pg, 0.4f);
		pg.fill(255, 0, 255);
		pg.rect(0, 0, 40, 10, 5);
		// define a second local coor sys define respect to the other local sys
		pg.pushMatrix();
		pg.translate(100, 100);
		pg.rotate(-QUARTER_PI);
		// draw a third rect respect to the local coordinate system
		drawAxis(pg, 0.4f);
		pg.fill(255, 255, 0);
		pg.rect(0, 0, 40, 10, 5);
		pg.popMatrix();
		pg.popMatrix();
		// draw a triangle respect to the world coord sys
		pg.fill(0, 255, 0);
		pg.triangle(30, 75, 58, 20, 86, 75);
	}

	void drawAxis(PGraphics pg) {
		drawAxis(pg, 1);
	}

	void drawAxis(PGraphics pg, float s) {
		// X-Axis
		pg.strokeWeight(2);
		pg.stroke(0, 255, 0);
		pg.fill(0, 255, 0);
		pg.line(0, 0, 100 * s, 0);
		pg.text("X", 100 * s + 5, 0);
		// Y-Axis
		pg.stroke(0, 0, 255);
		pg.fill(0, 0, 255);
		pg.line(0, 0, 0, 100 * s);
		pg.text("Y", 0, 100 * s + 15);
	}
}
