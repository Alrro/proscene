import processing.core.*;
import proscene.*;

public class Box {
	PApplet parent;
	PSInteractiveFrame psIFrame;
	
	Box(PApplet p) {
		parent = p;
		psIFrame = new PSInteractiveFrame();
	}
	
	public void draw() {
		parent.pushMatrix();
		
		//parent.applyMatrix( glIFrame.pMatrix() );
		//Same as the previous commented line, but a lot more efficient:
		psIFrame.applyTransformation(parent);
		
		PScene.drawAxis(0.3f);
		if (psIFrame.grabsMouse())
			parent.fill(255, 0, 0);
		else
			parent.fill(0,0,255);		
		//Draw a box		
		parent.box(0.3f);
		
		parent.popMatrix();
	}
	
	public void setPosition(PVector pos) {
		psIFrame.setPosition(pos);
	}

}
