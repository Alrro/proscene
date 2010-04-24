/**
 * Luxo. 
 * by Jean Pierre Charalambos.  
 *
 * A more complex example that combines InteractiveFrames, selection and constraints.
 * 
 * This example displays a famous luxo lamp (Pixar) that can be interactively
 * manipulated with the mouse. It illustrates the use of several InteractiveFrames
 * in the same scene.
 *
 * Click on a frame visual hint to select a part of the lamp, and then move it with
 * the mouse.
 *
 * Press 'f' to toggle the drawing of the frames' visual hints.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

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

// Your actual scene drawing should be enclosed between the
// Scene.beginDraw() and Scene.endDraw() pair.
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
    vertex((200*(float)i/nbPatches-100), (200*j/nbPatches-100));
    vertex((200*(float)i/nbPatches-100), (200*(float)(j+1)/nbPatches-100));
    }
  endShape();
  }
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power
// keyPressed() should always call Scene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
