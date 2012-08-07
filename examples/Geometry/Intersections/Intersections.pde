/**
 * Intersections
 * By Jacques Maire
 */

import remixlab.proscene.*;

Scene scene;
Plan plan;

void setup() {
  size (800, 640, P3D); 
  scene = new Scene(this);
  scene.setGridIsDrawn(false);
  plan=new Plan();
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, -1, -1, -1);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(255, 255, 255, -1, -1, 0);
  scene.drawAxis(80); 
  translate(-100, 100, 0);
  rotateY(PI/2);
  scene.drawAxis(80); 
  plan.draw();
}