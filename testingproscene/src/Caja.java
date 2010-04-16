import processing.core.*;
import remixlab.proscene.*;

public class Caja {
	PApplet parent;
	remixlab.proscene.InteractiveFrame iFrame;
	
	Caja(PApplet p) {
		parent = p;
		iFrame = new remixlab.proscene.InteractiveFrame();
	}
	
	public void draw(PVector v) {
		setOrientation(v);
		parent.pushMatrix();		
		iFrame.applyTransformation(parent);
		Scene.drawAxis(0.3f);

		if (iFrame.grabsMouse()) {
			parent.fill(255, 0, 255);
			parent.box(0.35f);
		}
		else {
			parent.fill(0,255,255);
			parent.box(0.3f);
		}			
		parent.popMatrix();
	}
	
	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}
	
	public void setOrientation(PVector v) {
		PVector to = PVector.sub(v, iFrame.position()); 
		iFrame.setOrientation(new Quaternion(new PVector(0,1,0), to));
	}
}
