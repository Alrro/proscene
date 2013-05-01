package gnutopic;
import processing.core.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.constraint.*;
import remixlab.proscene.*;

public class SpecializedSphere {
	Scene scene;
	PApplet parent;
	CircledInteractiveFrame iFrame;
	float r;
	int c;
	
	public SpecializedSphere(Scene scn) {
		scene = scn;
		parent = scn.parent;
		float radius = 10;
		setRadius(radius);
		iFrame = new CircledInteractiveFrame(scn, radius);		
	}
	
	public void draw() {
		draw(true);
	}
	
	public void draw(boolean drawAxis) {
		parent.pushMatrix();
		iFrame.applyTransformation();
		
		if(drawAxis)
			//DrawingUtils.drawAxis(parent, radius()*1.3f);
		     scene.drawAxis(radius()*1.3f);
		if (iFrame.grabsDevice()) {
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
	
	public void setPosition(Vector3D pos) {
		iFrame.setPosition(pos);
	}
	
	public Vector3D getPosition() {
		return iFrame.position();
	}
}
