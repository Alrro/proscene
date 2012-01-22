package geom;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class Box {
	Scene scene;
	InteractiveFrame iFrame;
	float w, h, d;
	int c;
	
	public Box(Scene scn) {
		scene = scn;
		iFrame = new InteractiveFrame(scn);
		setSize();
		setColor();		
		setPosition();
	}
	
	public void draw() {
		draw(false);
	}
	
	public void draw(boolean drawAxis) {
		scene.pg3d.pushMatrix();
		
		/**
		PMatrix3D pM3d =  new PMatrix3D();
		float [] m = new float [16];
		Matrix3D m3d = iFrame.matrix();
		m = m3d.getTransposed(m);
		pM3d.set(m);
		scene.pg3d.applyMatrix(pM3d);
		*/				
		//Same as the previous commented lines, but a lot more efficient:
		iFrame.applyTransformation();
		
		if(drawAxis)
			//DrawingUtils.drawAxis(parent, PApplet.max(w,h,d)*1.3f);
			scene.drawAxis(PApplet.max(w,h,d)*1.3f);
		scene.pg3d.noStroke();
		if (iFrame.grabsMouse())
			scene.pg3d.fill(255,0,0);
		else
			scene.pg3d.fill(getColor());
		//Draw a box		
		scene.pg3d.box(w,h,d);
		
		scene.pg3d.popMatrix();
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
	
	public Vector3D getPosition() {
		return iFrame.position();
	}	
	
	public void setPosition() {
		float low = -100;
		float high = 100;
		iFrame.setPosition(new Vector3D(scene.parent.random(low, high), scene.parent.random(low, high), scene.parent.random(low, high)));
	}
	
	public void setPosition(Vector3D pos) {
		iFrame.setPosition(pos);
	}
	
	public Quaternion getOrientation() {
		return iFrame.orientation();
	}
	
	public void setOrientation(Vector3D v) {
		Vector3D to = Vector3D.sub(v, iFrame.position()); 
		iFrame.setOrientation(new Quaternion(new Vector3D(0,1,0), to));
	}
}
