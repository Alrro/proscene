import processing.core.*;
import remixlab.proscene.*;

public class Box {
	PApplet parent;
	float w, h, d;
	int myColor;
	remixlab.proscene.InteractiveFrame iFrame;
	
	Box(PApplet p, float size, int c) {
		parent = p;
		w = h = d = size;
		myColor = c;
		iFrame = new remixlab.proscene.InteractiveFrame();
	}
	
	Box(PApplet p) {
		parent = p;
		w = parent.random(0.1f, 0.4f);
		h = parent.random(0.1f, 0.4f);
		d = parent.random(0.1f, 0.4f);
		myColor = parent.color(parent.random(0, 255), parent.random(0, 255), parent.random(0, 255));
		iFrame = new remixlab.proscene.InteractiveFrame();
		setPosition();
	}
	
	public void draw() {
		parent.pushMatrix();
		
		//parent.applyMatrix( glIFrame.matrix() );
		//Same as the previous commented line, but a lot more efficient:
		iFrame.applyTransformation(parent);
		
		//Scene.drawAxis(0.3f);
		parent.noStroke();
		if (iFrame.grabsMouse())
			parent.fill(255,0,0);
		else
			parent.fill(myColor);		
		//Draw a box		
		parent.box(w,h,d);
		
		parent.popMatrix();
	}
	
	public PVector getPosition() {
		return iFrame.position();
	}	
	
	public void setPosition() {
		float low = -1.0f;
		float high = 1.0f;
		iFrame.setPosition(new PVector(parent.random(low, high), parent.random(low, high), parent.random(low, high)));
	}
	
	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}
}
