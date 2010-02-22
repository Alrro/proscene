public class Box {
	PApplet parent;
	PSInteractiveFrame psIFrame;
	
	Box(PApplet p) {
		parent = p;
		psIFrame = new PSInteractiveFrame();
	}
	
	public void draw() {
		pushMatrix();
		
		//parent.applyMatrix( glIFrame.pMatrix() );
		//Same as the previous commented line, but a lot more efficient:
		psIFrame.applyTransformation(parent);
		
		PScene.drawAxis(0.3f);
		if (psIFrame.grabsMouse())
			fill(255, 0, 0);
		else
			fill(0,0,255);		
		//Draw a box		
		box(0.3f);
		
		popMatrix();
	}
	
	public void setPosition(PVector pos) {
		psIFrame.setPosition(pos);
	}
}
