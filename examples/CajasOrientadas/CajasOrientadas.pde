/**
 * Cajas Orientadas. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates some basic Frame properties, particularly how
 * to orient them. Select and move the sphere (holding the right mouse button
 * pressed) to see how the boxes will immediately be oriented towards it.
 * You can also pick and move the boxes and still they will be oriented
 * towards the sphere.
 *
 * This example illustrates some functionality which was needed by the
 * http://cajas.sourceforge.net/ project.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;
Box [] cajas;
Sphere esfera;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setGridIsDrawn(true);
  scene.setRadius(160);
  scene.showAll();
  
  esfera = new Sphere(this);
  esfera.setPosition(new PVector(0, 140, 0));
  esfera.setColor(color(0,0,255));
  
  // create an array of boxes with random positions, sizes and colors 
  cajas = new Box[30];
  for (int i = 0; i < cajas.length; i++)
    cajas[i] = new Box(this);
}

// Your actual scene drawing should be enclosed between the
// Scene.beginDraw() and Scene.endDraw() pair.
void draw() {
  // Should always be defined before Scene.beginDraw()
  background(0);

  scene.beginDraw();
  // Here we are in the world coordinate system.
  // Draw your scene here.
  esfera.draw();
  for (int i = 0; i < cajas.length; i++) {
    // orient the boxes according to the sphere position
    cajas[i].setOrientation(esfera.getPosition());
    cajas[i].draw(true);
  }
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power
// keyPressed() should always call Scene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
