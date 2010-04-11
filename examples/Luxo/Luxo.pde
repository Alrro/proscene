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
  scene.setFrameSelectionHintIsDrawn(true);
  lamp = new Lamp(this);
}

void draw() {
  background(0);
  lights();
  scene.beginDraw();
  lamp.draw();
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

void keyPressed() {
  scene.defaultKeyBindings();
}
