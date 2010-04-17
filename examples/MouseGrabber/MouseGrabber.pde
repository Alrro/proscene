/**
 * Mouse Grabber. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates the picking mechanism built-in proscene,
 * which represents one of the three interactive mechanisms found in
 * proscene (camera and interactive frame, being the other two).
 * Once you select a box it will be highlighted and you can manipulate
 * it with the mouse. Try the different mouse buttons to see what happens.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help. 
 */

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

Scene scene;
Box [] boxes;

void setup() {
  size(640, 360, OPENGL);
  scene = new Scene(this);
  scene.setGridIsDrawn(true);
  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setRadius(1.5f);
  scene.showAll();
  // create an array of boxes with random positions
  boxes = new Box[30];
  for (int i = 0; i < boxes.length; i++) {
    boxes[i] = new Box(this);
    boxes[i].setSize(0.2f, 0.2f, 0.2f);
    boxes[i].setColor(color(0,0,255));
  }
}

// Your actual scene drawing should be enclosed between the
// Scene.beginDraw() and Scene.endDraw() pair.
void draw() {
  // Should always be defined before Scene.beginDraw()
  background(0);

  scene.beginDraw();
  // Here we are in the world coordinate system.
  // Draw your scene here.
  for (int i = 0; i < boxes.length; i++)
    boxes[i].draw(true);
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power
// keyPressed() should always call Scene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
