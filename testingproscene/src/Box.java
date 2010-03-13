import processing.core.*;
import remixlab.proscene.*;

public class Box {
	PApplet parent;
	remixlab.proscene.InteractiveFrame iFrame;
	
	Box(PApplet p) {
		parent = p;
		iFrame = new remixlab.proscene.InteractiveFrame();
	}
	
	public void draw() {
		parent.pushMatrix();
		
		//parent.applyMatrix( glIFrame.pMatrix() );
		//Same as the previous commented line, but a lot more efficient:
		iFrame.applyTransformation(parent);
		
		Scene.drawAxis(0.3f);
		if (iFrame.grabsMouse())
			parent.fill(255, 0, 0);
		else
			parent.fill(0,0,255);		
		//Draw a box		
		parent.box(0.3f);
		
		parent.popMatrix();
	}
	
	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}
}
