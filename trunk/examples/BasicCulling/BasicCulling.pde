import processing.opengl.*;
import remixlab.proscene.*;

PImage b;
Scene scene;

void setup() {
  size(640, 360, P3D);
  //size(640, 360, OPENGL);
  scene = new Scene(this);
  scene.enableFrustumEquationsUpdate();
  //b = loadImage("pierre.png");
  //scene.background(b);
  frame.setResizable(true);
}

void draw() {
  //scene.background(b);
  noStroke();
  if( scene.camera().sphereIsVisible(new PVector(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
    fill(255, 0, 0);
  else
    fill(0, 255, 0);
  sphere(40);
}