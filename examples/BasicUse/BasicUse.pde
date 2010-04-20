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
import processing.opengl.*;

Scene scene;

void setup() {
  size(640, 360, OPENGL);
  // Scene instantiation
  scene = new Scene(this);
  scene.setRadius(100);
  scene.showAll();
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

// To take full advantage of proscene 3d navigation power
// keyPressed() should always call Scene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
