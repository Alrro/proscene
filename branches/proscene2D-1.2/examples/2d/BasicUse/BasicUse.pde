import remixlab.proscene.*;

Scene2D scene;

void setup() {  
  size(640, 360, P2D);
  scene = new Scene2D(this);
  scene.showAll();
}

void draw() {
  background(150);
  rect(0, 0, 55, 55);
}
