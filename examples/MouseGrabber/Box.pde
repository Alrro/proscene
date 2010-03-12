/**
 * Box. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Mouse Grabber example.
 *
 * Any object that needs to be "pickable" (such as the Box), should be
 * attached to its own InteractiveFrame. That's all there is to it.
 *
 * The built-in picking proscene mechanism actually works as follows. At
 * instantiation time all InteractiveFrame objects are added to a mouse
 * grabber pool. Scene parses this pool every frame to check if the mouse
 * grabs a InteractiveFrame by projecting its origin onto the screen.
 * If the mouse position is close enough to that projection (default
 * implementation gives a 10 pixel tolerance), the object will be picked. 
 *
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

public class Box {
  PApplet parent;
  InteractiveFrame iFrame;
	
  Box(PApplet p) {
    parent = p;
    iFrame = new InteractiveFrame();
  }
	
  public void draw() {
    pushMatrix();
		
    //parent.applyMatrix( iFrame.matrix() );
    //Same as the previous commented line, but a lot more efficient:
    iFrame.applyTransformation(parent);
		
    Scene.drawAxis(0.3f);
    if (iFrame.grabsMouse())
      fill(255, 0, 0);
    else
      fill(0,0,255);	  		
    //Draw a box		
    box(0.3f);
		
    popMatrix();
  }
	
  public void setPosition(PVector pos) {
    iFrame.setPosition(pos);
  }
}
