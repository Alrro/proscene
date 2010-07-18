/**
 * Mouse Grabber by Jean Pierre Charalambos.
 * 
 * This example illustrates the picking mechanism built-in proscene,
 * which represents one of the three interactive mechanisms found in
 * proscene (camera and interactive frame, being the other two).
 * Once you select a box it will be highlighted and you can manipulate
 * it with the mouse. Try the different mouse buttons to see what happens.
 * 
 * Press 'h' to toggle the mouse and keyboard navigation help. 
 */

import remixlab.proscene.*;

Scene scene;
Box [] boxes;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setAxisIsDrawn(false);
  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setRadius(150);
  scene.showAll();
  // create an array of boxes with random positions
  boxes = new Box[30];
  for (int i = 0; i < boxes.length; i++) {
    boxes[i] = new Box();
    boxes[i].setSize(20, 20, 20);
    boxes[i].setColor(color(0,0,255));
  }
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  for (int i = 0; i < boxes.length; i++)
    boxes[i].draw(true);
}