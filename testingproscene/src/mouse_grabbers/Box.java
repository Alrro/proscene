package mouse_grabbers;

import processing.core.*;
import remixlab.proscene.*;

public class Box {
	InteractiveFrame iFrame;
	float w, h, d;
	int c;
	Scene scene;

	Box(Scene scn) {
		scene = scn;
		iFrame = new InteractiveFrame(scene);
		setSize();
		setColor();
		setPosition();
	}

	// don't draw local axis
	public void draw() {
		draw(false);
	}

	public void draw(boolean drawAxis) {
		scene.parent.pushMatrix();
		scene.parent.pushStyle();
		// Multiply matrix to get in the frame coordinate system.
		// scene.parent.applyMatrix(iFrame.matrix()) is handy but inefficient
		iFrame.applyTransformation(); // optimum
		if (drawAxis)
			scene.drawAxis(PApplet.max(w, h, d) * 1.3f);
		scene.parent.noStroke();
		if (iFrame.grabsMouse())
			scene.parent.fill(255, 0, 0);
		else
			scene.parent.fill(getColor());
		// Draw a box
		scene.parent.box(w, h, d);
		scene.parent.popStyle();
		scene.parent.popMatrix();
	}

	// sets size randomly
	public void setSize() {
		w = scene.parent.random(10, 40);
		h = scene.parent.random(10, 40);
		d = scene.parent.random(10, 40);
	}

	public void setSize(float myW, float myH, float myD) {
		w = myW;
		h = myH;
		d = myD;
	}

	public int getColor() {
		return c;
	}

	// sets color randomly
	public void setColor() {
		c = scene.parent.color(scene.parent.random(0, 255), scene.parent.random(0, 255), scene.parent.random(0, 255));
	}

	public void setColor(int myC) {
		c = myC;
	}

	public PVector getPosition() {
		return iFrame.position();
	}

	// sets position randomly
	public void setPosition() {
		float low = -100;
		float high = 100;
		iFrame.setPosition(new PVector(scene.parent.random(low, high), scene.parent.random(low, high),
				scene.parent.random(low, high)));
	}

	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}

	public Quaternion getOrientation() {
		return iFrame.orientation();
	}

	public void setOrientation(PVector v) {
		PVector to = PVector.sub(v, iFrame.position());
		iFrame.setOrientation(new Quaternion(new PVector(0, 1, 0), to));
	}
}