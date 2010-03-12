/**
 * Esfera. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Cajas Orientadas example.
 *
 * Any object that needs to be "pickable" (such as the Esfera), should be
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

public class Esfera {
  PApplet parent;
  InteractiveFrame iFrame;
	
  Esfera(PApplet p) {
    parent = p;
    iFrame = new InteractiveFrame();
  }
	
  public void draw() {
    pushMatrix();
    //parent.applyMatrix( iFrame.matrix() );
    //Same as the previous commented line, but a lot more efficient:
    iFrame.applyTransformation(parent);
		
    Scene.drawAxis(0.3f);
    if (iFrame.grabsMouse()) {
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
    iFrame.setPosition(pos);
  }
	
  // We need to retrieve the Esfera's position for the Cajas to orient towards it. 
  public PVector getPosition() {
    return iFrame.position();
  }
}
