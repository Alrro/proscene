/**
 * Basic Use. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates a direct approach to using proscene
 * by Scene proper instantiation.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;

void setup() {
  size(640, 360, P3D);
  // Scene instantiation
  scene = new Scene(this);
  scene.setGridIsDrawn(true);
  scene.setAxisIsDrawn(true);
}

// Your actual scene drawing should be enclosed between the
// Scene.beginDraw() and Scene.endDraw() pair.
void draw() {
  // Should always be defined before Scene.beginDraw()
  background(0);
  scene.beginDraw();
  // Actual scene
  fill(204, 102, 0);
  box(20, 30, 50);
  scene.endDraw();
}