import processing.core.*;
import proscene.*;

public class Esfera {
	PApplet parent;
	PSInteractiveFrame psIFrame;
	
	Esfera(PApplet p) {
		parent = p;
		psIFrame = new PSInteractiveFrame();
	}
	
	public void draw() {
		parent.pushMatrix();
		psIFrame.applyTransformation(parent);
		
		PScene.drawAxis(0.3f);
		if (psIFrame.grabsMouse()) {
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
		psIFrame.setPosition(pos);
	}
	
	public PVector getPosition() {
		return psIFrame.position();
	}
}
