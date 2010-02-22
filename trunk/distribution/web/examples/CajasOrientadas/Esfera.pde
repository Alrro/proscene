public class Esfera {
	PApplet parent;
	PSInteractiveFrame psIFrame;
	
	Esfera(PApplet p) {
		parent = p;
		psIFrame = new PSInteractiveFrame();
	}
	
	public void draw() {
		pushMatrix();
		psIFrame.applyTransformation(parent);
		
		PScene.drawAxis(0.3f);
		if (psIFrame.grabsMouse()) {
			fill(255, 0, 0);
			sphere(0.15f);
		}
		else {
			fill(0,0,255);
			sphere(0.1f);
		}
		popMatrix();
	}
	
	public void setPosition(PVector pos) {
		psIFrame.setPosition(pos);
	}
	
	public PVector getPosition() {
		return psIFrame.position();
	}
}
