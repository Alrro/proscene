/**
 * <b>Cajas Orientadas</b> by Jean Pierre Charalambos.
 * <p>
 * This example illustrates some basic Frame properties, particularly how to orient them.<br>
 * Select and move the sphere (holding the right mouse button pressed) to see how the<br>
 * boxes will immediately be oriented towards it.<br> You can also pick and move the boxes<br>
 * and still they will be oriented towards the sphere.
 * <p>
 * This example illustrates some functionality which was needed by the
 * <a href="http://cajas.sourceforge.net/">Cajas</a> project.
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;
Box [] cajas;
Sphere esfera;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setAxisIsDrawn(false);
  scene.setRadius(160);
  scene.showAll();
  
  esfera = new Sphere();
  esfera.setPosition(new PVector(0, 140, 0));
  esfera.setColor(color(0,0,255));
  
  // create an array of boxes with random positions, sizes and colors 
  cajas = new Box[30];
  for (int i = 0; i < cajas.length; i++)
    cajas[i] = new Box();
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  esfera.draw();
  for (int i = 0; i < cajas.length; i++) {
    // orient the boxes according to the sphere position
    cajas[i].setOrientation(esfera.getPosition());
    cajas[i].draw(true);
  }
}