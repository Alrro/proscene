package geom;
import processing.core.*;
import remixlab.proscene.*;

public class Box {
	Scene scene;
	PGraphics pg;
	InteractiveFrame iFrame;
	float w, h, d;
	int c;
	
	public Box(Scene scn) {
		scene = scn;
		pg = scn.renderer();
		iFrame = new InteractiveFrame(scn);
		setSize();
		setColor();		
		setPosition();
	}
	
	public void draw() {
		draw(false);
	}
	
	public void draw(boolean drawAxis) {
		pg.pushMatrix();
		
		//parent.applyMatrix( glIFrame.matrix() );
		//Same as the previous commented line, but a lot more efficient:
		iFrame.applyTransformation();
		
		if(drawAxis)
			//DrawingUtils.drawAxis(parent, PApplet.max(w,h,d)*1.3f);
			scene.drawAxis(PApplet.max(w,h,d)*1.3f);
		pg.noStroke();
		if (iFrame.grabsMouse())
			pg.fill(255,0,0);
		else
			pg.fill(getColor());
		//Draw a box		
		pg.box(w,h,d);
		
		pg.popMatrix();
	}
	
	public void setSize() {		
		w = scene.parent.random(10, 40);
		h = scene.parent.random(10, 40);
		d = scene.parent.random(10, 40);
	}
	
	public void setSize(float myW, float myH, float myD) {
		w=myW; h=myH; d=myD;
	}	
	
	public int getColor() {
		return c;
	}
	
	public void setColor() {
		c = scene.parent.color(scene.parent.random(0, 255), scene.parent.random(0, 255), scene.parent.random(0, 255));
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
		iFrame.setPosition(new PVector(scene.parent.random(low, high), scene.parent.random(low, high), scene.parent.random(low, high)));
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
