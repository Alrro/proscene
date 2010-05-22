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

import remixlab.proscene.*;

Scene scene;

void setup() {  
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setGridIsDrawn(true);
  scene.setAxisIsDrawn(true);
  // A Scene has a single InteractiveFrame (null by default). We set
  // it here.
  scene.setInteractiveFrame(new InteractiveFrame());
  scene.interactiveFrame().translate(new PVector(30, 30, 0));
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
  box(20, 20, 40);
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(scene.interactiveFrame().matrix()) is handy but inefficient 
  scene.interactiveFrame().applyTransformation(this); //optimum
  // Draw an axis using the Scene static function
  scene.drawAxis(70);
  // Draw a second box attached to the interactive frame
  if (scene.interactiveFrame().grabsMouse()) {
    fill(255, 0, 0);
    box(12, 17, 22);
  }
  else if (scene.interactiveFrameIsDrawn()) {
    fill(0, 255, 255);
    box(12, 17, 22);
  }
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  }		
  popMatrix();
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power
// keyPressed() should always call Scene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
