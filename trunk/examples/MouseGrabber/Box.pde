/**
 * Box. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Mouse Grabber example.
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
