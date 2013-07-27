package vfcgl;
import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.proscene.*;

public class AABox {
	Scene scene;
	GeomFrame frame;
	float w, h, d, halfW, halfH, halfD, radius;
	int r, g, b;

	AABox(Scene scn) {
		scene = scn;
		frame = new GeomFrame();
		setSize();
		setPosition();
		setColor();
	}

	public void draw(Scene scn, Camera camera) {
		scene = scn;
		draw(scn.parent, camera, false, false);
		// bypass it if you don't want vfc
		// drawCube(parent, true, false);
	}
	
	public void draw(PApplet parent, Camera camera, boolean drawBoundingVolumes) {
		draw(parent, camera, drawBoundingVolumes, false);
		// bypass it if you don't want vfc
		// drawCube(parent, drawBoundingVolumes, false);
	}
	
	public void draw(PApplet parent, Camera camera,	boolean drawBoundingVolumes, boolean drawAxis) {
		switch (camera.sphereIsVisible(frame.position(), radius)) {
		case VISIBLE:
			drawCube(parent, drawBoundingVolumes, drawAxis);
			break;
		case SEMIVISIBLE:
			Vec BBCorner1 = new Vec(frame.position().x() - this.halfW, frame.position().y() + this.halfH, frame.position().z() - this.halfD);
			Vec BBCorner2 = new Vec(frame.position().x() + this.halfW, frame.position().y() - this.halfH, frame.position().z() + this.halfD);
			switch (camera.aaBoxIsVisible(BBCorner1, BBCorner2)) {
			case VISIBLE:
			case SEMIVISIBLE:
				drawCube(parent, drawBoundingVolumes, drawAxis);
				break;
			case INVISIBLE:
				break;
			}
			break;
		case INVISIBLE:
			break;
		}
	}

	public void drawCube(PApplet parent, boolean drawBoundingVolumes, boolean drawAxis) {
		TestVFCGL.renderedCubes++;
		parent.pushMatrix();
		frame.applyTransformation(scene);
		if (drawAxis)
			scene.drawAxis(PApplet.max(w, h, d) * 1.3f);
		parent.noStroke();
		parent.fill(r, g, b);
		parent.box(w, h, d);

		if (drawBoundingVolumes) {		
			// bounding spheres
			parent.noFill();
			parent.stroke(0, 255, 0);
			parent.sphere(radius);
			// bounding boxes
			parent.stroke(255, 0, 0);
			parent.box(1.01f * w, 1.01f * h, 1.01f * d);
		}
		parent.popMatrix();
	}

	public void setSize() {
		halfW = 5f + (float) (15 * Math.random());
		w = 2 * halfW;
		halfH = 5f + (float) (15 * Math.random());
		h = 2 * halfH;
		halfD = 5f + (float) (15 * Math.random());
		d = 2 * halfD;
		setRadius();
	}

	public void setSize(float myW, float myH, float myD) {
		w = myW;
		halfW = w / 2;
		h = myH;
		halfH = h / 2;
		d = myD;
		halfD = d / 2;
		setRadius();
	}

	protected void setRadius() {
		radius = (float) Math.sqrt(halfW * halfW + halfH * halfH + halfD
				* halfD);
	}

	public Vec getPosition() {
		return frame.position();
	}

	public void setPosition() {
		frame.setPosition(new Vec((float) (2 * TestVFCGL.volSize * Math.random()) - TestVFCGL.volSize,
				                      (float) (2 * TestVFCGL.volSize * Math.random()) - TestVFCGL.volSize, 
				                      (float) (2 * TestVFCGL.volSize * Math.random()) - TestVFCGL.volSize));
	}

	public void setPosition(Vec pos) {
		frame.setPosition(pos);
	}

	public Quat getOrientation() {
		return (Quat)frame.orientation();
	}

	public void setColor() {
		r = (int) (255 * Math.random());
		g = (int) (255 * Math.random());
		b = (int) (255 * Math.random());
	}
}
