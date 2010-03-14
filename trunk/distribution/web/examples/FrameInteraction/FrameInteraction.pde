/**
 * Frame Interaction. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates the three interactive frame mechanisms
 * built-in proscene: Camera, InteractiveFrame and MouseGrabber.
 *
 * Press 'i' to switch the interaction between the camera frame and
 * the interactive frame. You can also manipulate the interactive frame
 * by picking the blue box passing the mouse next its axis origin.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

Scene scene;

void setup() {  
  size(640, 360, OPENGL);
  scene = new Scene(this); 
  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setGridIsDrawn(true);
  scene.setAxisIsDrawn(true);
  // A Scene has a single InteractiveFrame (null by default). We set
  // it here.
  scene.setInteractiveFrame(new InteractiveFrame());
  scene.interactiveFrame().translate(new PVector(0.3f, 0.3f, 0));
}

// Your actual scene drawing should be enclosed between the
// Scene.beginDraw() and Scene.endDraw() pair.
void draw() {
  // Should always be defined before Scene.beginDraw()
  background(0);
  scene.beginDraw();
  // Here we are in the world coordinate system.
  // Draw your scene here.
  fill(204, 102, 0);
  box(0.2f, 0.3f, 0.4f);
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(scene.interactiveFrame().matrix()) is handy but inefficient 
  scene.interactiveFrame().applyTransformation(this); //optimum
  // Draw an axis using the Scene static function
  Scene.drawAxis(0.7f);
  // Draw a second box attached to the interactive frame
  if (scene.interactiveFrame().grabsMouse()) {
    fill(255, 0, 0);
    box(0.12f, 0.17f, 0.22f);
  }
  else if (scene.interactiveFrameIsDrawn()) {
    fill(0, 255, 255);
    box(0.12f, 0.17f, 0.22f);
  }
  else {
    fill(0, 0, 255);
    box(0.1f, 0.15f, 0.2f);
  }		
  popMatrix();
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power this
// method should always call Scene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
