import processing.core.*;
import remixlab.proscene.*;

public class Box {
	PApplet parent;
	InteractiveFrame iFrame;
	float w, h, d;
	int c;
	
	Box(Scene scn) {
		parent = scn.parent;
		iFrame = new InteractiveFrame(scn);
		setSize();
		setColor();		
		setPosition();
	}
	
	public void draw() {
		draw(false);
	}
	
	public void draw(boolean drawAxis) {
		parent.pushMatrix();
		
		//parent.applyMatrix( glIFrame.matrix() );
		//Same as the previous commented line, but a lot more efficient:
		iFrame.applyTransformation(parent);
		
		if(drawAxis)
			DrawingUtils.drawAxis(parent, PApplet.max(w,h,d)*1.3f);
		parent.noStroke();
		if (iFrame.grabsMouse())
			parent.fill(255,0,0);
		else
			parent.fill(getColor());
		//Draw a box		
		parent.box(w,h,d);
		
		parent.popMatrix();
	}
	
	public void setSize() {
		w = parent.random(10, 40);
		h = parent.random(10, 40);
		d = parent.random(10, 40);
	}
	
	public void setSize(float myW, float myH, float myD) {
		w=myW; h=myH; d=myD;
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
	
	public PVector getPosition() {
		return iFrame.position();
	}	
	
	public void setPosition() {
		float low = -100;
		float high = 100;
		iFrame.setPosition(new PVector(parent.random(low, high), parent.random(low, high), parent.random(low, high)));
	}
	
	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}
	
	public Quaternion getOrientation() {
		return iFrame.orientation();
	}
	
	public void setOrientation(PVector v) {
		PVector to = PVector.sub(v, iFrame.position()); 
		iFrame.setOrientation(new Quaternion(new PVector(0,1,0), to));
	}
}
