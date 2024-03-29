/**
 * Ceva Sonia
 * by Jacques Maire (http://www.alcys.com/)
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;
Scene scene;
Ceva ceva;
PImage abeille;

void setup() {
  size(640, 640, P3D);
  scene =new Scene(this);
  scene.setRadius(500);
  scene.camera().setPosition(new PVector(0, 0, 800));
  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  ceva=new Ceva();
  abeille = loadImage("sonia.gif");
}

void draw() {
  background(255, 200, 0);
  ceva.cevaDraw();
}

