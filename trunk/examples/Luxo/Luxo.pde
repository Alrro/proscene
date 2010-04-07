/**
 * Luxo. 
 * by Jean Pierre Charalambos.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

Scene scene;
Lamp lamp;
boolean visualHint;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setHelpIsDrawn(false);
  lamp = new Lamp(this);
  visualHint = true;
}

void draw() {
  background(0);
  lights();
  scene.beginDraw();
  lamp.draw();
  if(visualHint)
    drawFrameVisualHint();
  //draw the ground
  noStroke();
  fill(120, 120, 120);
  float nbPatches = 100;
  normal(0.0f,0.0f,1.0f);
  for (int j=0; j<nbPatches; ++j) {
  beginShape(QUAD_STRIP );
  for (int i=0; i<=nbPatches; ++i) {
    vertex((2f*(float)i/nbPatches-1.0f), (2*j/nbPatches-1.0f));
    vertex((2f*(float)i/nbPatches-1.0f), (2*(float)(j+1)/nbPatches-1.0f));
    }
  endShape();
  }
  scene.endDraw();
}

void drawFrameVisualHint() {
  for (int i=0; i<4; ++i) {
    PVector center = scene.camera().projectedCoordinatesOf(lamp.frame(i).position());
    if (lamp.frame(i).grabsMouse())
      filledCircle(color(255,0,0),center,10);
    else
      filledCircle(color(0,255,0),center,10);
  }
}

void filledCircle(int c, PVector center, float radius) {
  float x = center.x;
  float y = center.y;
  float angle, x2, y2;
  noStroke();
  fill(c);
  scene.beginScreenDrawing();
  beginShape(TRIANGLE_FAN);
  vertex(Scene.xCoord(x), Scene.yCoord(y), Scene.zCoord());
  for (angle=0.0f;angle<=TWO_PI;angle+=+=0.157f) {
    x2 = x+sin(angle)*radius;
    y2 = y+cos(angle)*radius;
    vertex(Scene.xCoord(x2), Scene.yCoord(y2), Scene.zCoord());
  }
  endShape();
  scene.endScreenDrawing();
}

void keyPressed() {
  scene.defaultKeyBindings();
  if (key == 'f' || key == 'F') {
    visualHint = !visualHint;
  }
}
