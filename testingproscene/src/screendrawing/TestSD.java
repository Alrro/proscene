package screendrawing;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

public class TestSD extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;
	PGraphicsOpenGL pgl;
	float zC = 0;

	public void setup() {
		size(640, 360, OPENGL);
		scene = new Scene(this);
		scene.setRadius(200);
		scene.showAll();
		pgl = (PGraphicsOpenGL) g;
		noStroke();
		fill(204);
	}

	public void draw() {
		background(80);		

		box(160);
		stroke(200, 100, 10);
		
		/**
		// 1. Screen drawing using proscene
		scene.beginScreenDrawing();		
		PVector p1 = scene.coords(new Point(10, 10));
		PVector p2 = scene.coords(new Point(width - 10, height - 10));
		PVector p3 = scene.coords(new Point(10, height - 10));
		PVector p4 = scene.coords(new Point(width - 10, 10));		
		scene.renderer().line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
		scene.renderer().line(p3.x, p3.y, p3.z, p4.x, p4.y, p4.z);		
		//scene.renderer().line(10, 10, zC, width - 10, height - 10, zC);
		//scene.renderer().line(10, height - 10, zC, width - 10, 10, zC);
		scene.endScreenDrawing();
		// */
		
		// /**
		// 2. "External" screen drawing
		beginScreenDrawing();
		line(10, 10, zC, width-10, height-10, zC);
		endScreenDrawing();
		
		beginScreenDrawing();
		line(10, height-10, zC, width-10, 10, zC);
		endScreenDrawing();
		// */
	}

	public void beginScreenDrawing() {
		pgl.hint(DISABLE_DEPTH_TEST);
		pgl.pushProjection();
		pgl.ortho(-width / 2, width / 2, -height / 2, height / 2, -10, 10);
		pgl.pushMatrix();
		pgl.camera();
		zC = 0.0f;
	}

	public void endScreenDrawing() {
		pgl.popProjection();
		pgl.popMatrix();
		hint(ENABLE_DEPTH_TEST);
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "screendrawing.TestSD" });
	}
}
