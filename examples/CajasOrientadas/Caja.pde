/**
 * Caja.
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Cajas Orientadas example.
 *
 * Any object that needs to be "pickable" (such as the Caja), should be
 * attached to its own InteractiveFrame. That's all there is to it.
 *
 * The built-in picking proscene mechanism actually works as follows. At
 * instantiation time all InteractiveFrame objects are added to a mouse
 * grabber pool. Scene parses this pool every frame to check if the mouse
 * grabs a InteractiveFrame by projecting its origin onto the screen.
 * If the mouse position is close enough to that projection (default
 * implementation defines a 10x10 pixel square centered at it), the object
 * will be picked. 
 *
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

public class Caja {
  PApplet parent;
  InteractiveFrame iFrame;

  Caja(PApplet p) {
    parent = p;
    iFrame = new InteractiveFrame();
  }

  public void draw(PVector v) {
    setOrientation(v);
    parent.pushMatrix();
    // Multiply matrix to get in the frame coordinate system.
    // parent.applyMatrix(iFrame.matrix()) is handy but inefficient
    iFrame.applyTransformation(parent); //optimum
    Scene.drawAxis(0.3f);

    if (iFrame.grabsMouse()) {
      parent.fill(255, 0, 255);
      parent.box(0.35f);
    }
    else {
      parent.fill(0,255,255);
      parent.box(0.3f);
    }
    parent.popMatrix();
  }

  public void setPosition(PVector pos) {
    iFrame.setPosition(pos);
  }

  // We orient the Caja's y axis according to the Esfera's position that
  // should be defined in v.
  public void setOrientation(PVector v) {
    PVector to = PVector.sub(v, iFrame.position()); 
    iFrame.setOrientation(new Quaternion(new PVector(0,1,0), to));
  }
}
