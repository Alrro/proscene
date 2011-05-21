package geom;
import processing.core.*;
import remixlab.proscene.*;

public class Sphere {
	Scene scene;
	PApplet parent;
	InteractiveFrame iFrame;
	float r;
	int c;
	
	public Sphere(Scene scn) {
		scene = scn;
		parent = scn.parent;
		iFrame = new InteractiveFrame(scn);
		setRadius(10);
	}
	
	public void draw() {
		draw(true);
	}
	
	public void draw(boolean drawAxis) {
		parent.pushMatrix();
		iFrame.applyTransformation(parent);
		
		if(drawAxis)
			//DrawingUtils.drawAxis(parent, radius()*1.3f);
		     scene.drawAxis(radius()*1.3f);
		if (iFrame.grabsMouse()) {
			parent.fill(255, 0, 0);
			parent.sphere(radius()*1.2f);
		}
		else {
			parent.fill(getColor());
			parent.sphere(radius());
		}		
		parent.popMatrix();
	}
	
	public float radius() {
		return r;
	}
	
	public void setRadius(float myR) {
		r = myR;
	}
	
	public int getColor() {
		return c;
	}
	
	public void setColor() {
		c = parent.color(parent.random(0, 255), parent.random(0, 255), parent.random(0, 255));
	}
	
	public void setColor(int myC) {
		c = myC;
	}
	
	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}
	
	public PVector getPosition() {
		return iFrame.position();
	}
}
