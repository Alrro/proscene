/**
 * Esfera. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Cajas Orientadas example.
 *
 * Any object that needs to be "pickable" (such the Box), should be attached
 * it to its own PSInteractiveFrame. That's all there is to it.
 *
 * The built-in picking proscene mechanism actually works as follows.
 * At instantiation time all PSInteractiveFrame objects are added to a mouse
 * grabber pool. PScene parses this pool to check in the mouse grabs a frame:
 * the PSInteractiveFrame origin is projected onto the screen. If
 * the mouse position is close enough to that projection (default
 * implementation gives a 10 pixel tolerance), the object will be picked. 
 *
 * Override PSInteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

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
	
	// We orient the Caja according to the Esfera's position that
	// should be defined in v.
	public void setOrientation(PVector v) {
		PVector to = PVector.sub(v, psIFrame.position()); 
		psIFrame.setOrientation(new PSQuaternion(new PVector(0,1,0), to));
	}
}
