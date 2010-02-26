public class Caja {
	PApplet parent;
	PSInteractiveFrame psIFrame;
	
	Caja(PApplet p) {
		parent = p;
		psIFrame = new PSInteractiveFrame();
	}
	
	public void draw(PVector v) {
		setOrientation(v);
		parent.pushMatrix();		
		psIFrame.applyTransformation(parent);
		PScene.drawAxis(0.3f);

		if (psIFrame.grabsMouse()) {
			parent.fill(0, 255, 0);
			parent.box(0.35f);
		}
		else {
			parent.fill(0,255,255);
			parent.box(0.3f);
		}			
		parent.popMatrix();
	}
	
	public void setPosition(PVector pos) {
		psIFrame.setPosition(pos);
	}
	
	public void setOrientation(PVector v) {
		PVector to = PVector.sub(v, psIFrame.position()); 
		psIFrame.setOrientation(new PSQuaternion(new PVector(0,1,0), to));
	}
}
