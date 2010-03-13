import processing.core.*;
import remixlab.proscene.*;

public class Esfera {
	PApplet parent;
	remixlab.proscene.InteractiveFrame iFrame;
	
	Esfera(PApplet p) {
		parent = p;
		iFrame = new remixlab.proscene.InteractiveFrame();
	}
	
	public void draw() {
		parent.pushMatrix();
		iFrame.applyTransformation(parent);
		
		Scene.drawAxis(0.3f);
		if (iFrame.grabsMouse()) {
			parent.fill(255, 0, 0);
			parent.sphere(0.15f);
		}
		else {
			parent.fill(0,0,255);
			parent.sphere(0.1f);
		}			
		parent.popMatrix();
	}
	
	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}
	
	public PVector getPosition() {
		return iFrame.position();
	}
}
