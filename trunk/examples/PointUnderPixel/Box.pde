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
 * implementation defines a 10x10 pixel square centered at it), the object
 * will be picked. 
 *
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */
 
import processing.core.*;
import remixlab.proscene.*;

public class Box {
  PApplet parent;
  float w, h, d;
  int myColor;
  remixlab.proscene.InteractiveFrame iFrame;
  
  Box(PApplet p, float size, int c) {
    parent = p;
    w = h = d = size;
    myColor = c;
    iFrame = new remixlab.proscene.InteractiveFrame();
  }
  
  Box(PApplet p) {
    parent = p;
    w = parent.random(0.1f, 0.4f);
    h = parent.random(0.1f, 0.4f);
    d = parent.random(0.1f, 0.4f);
    myColor = color(parent.random(0, 255), parent.random(0, 255), parent.random(0, 255));
    iFrame = new remixlab.proscene.InteractiveFrame();
    setPosition();
  }
  
  public void draw() {
    parent.pushMatrix();
    //parent.applyMatrix( glIFrame.matrix() );
    //Same as the previous commented line, but a lot more efficient:
    iFrame.applyTransformation(parent);
    parent.noStroke();
    if (iFrame.grabsMouse())
      parent.fill(255,0,0);
    else
      parent.fill(myColor);
    //Draw a box
    parent.box(w,h,d);
    parent.popMatrix();
  }
  
  public PVector getPosition() {
    return iFrame.position();
  }
  
  public void setPosition() {
    float low = -1.0f;
    float high = 1.0f;
    iFrame.setPosition(new PVector(parent.random(low, high), parent.random(low, high), parent.random(low, high)));
  }
  
  public void setPosition(PVector pos) {
    iFrame.setPosition(pos);
  }
}
