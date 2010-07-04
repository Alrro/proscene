import remixlab.proscene.*;

PImage jean;
Scene scene;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.enableFrustumEquationsUpdate();
  jean = loadImage("pierre.png");
  scene.background(jean);
  frame.setResizable(true);
}

void draw() {
  noStroke();
  if( scene.camera().sphereIsVisible(new PVector(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
    fill(255, 0, 0);
  else
    fill(0, 255, 0);
  sphere(40);
}