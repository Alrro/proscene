public class Caja {
	PApplet parent;
	PSInteractiveFrame psIFrame;
	
	Caja(PApplet p) {
		parent = p;
		psIFrame = new PSInteractiveFrame();
	}
	
	public void draw(PVector v) {
		setOrientation(v);
		pushMatrix();		
		psIFrame.applyTransformation(parent);
		PScene.drawAxis(0.3f);
		fill(0,255,255);		
		//Draw a box
		box(0.3f);		
		popMatrix();
	}
	
	public void setPosition(PVector pos) {
		psIFrame.setPosition(pos);
	}
	
	public void setOrientation(PVector v) {
		PVector to = PVector.sub(v, psIFrame.position()); 
		psIFrame.setOrientation(new PSQuaternion(new PVector(0,1,0), to));
	}
}
