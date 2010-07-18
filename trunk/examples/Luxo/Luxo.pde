/**
 * <b>Luxo</b> by Jean Pierre Charalambos. 
 * <p>
 * A more complex example that combines InteractiveFrames, selection and constraints.
 * <p>
 * This example displays a famous luxo lamp (Pixar) that can be interactively<br>
 * manipulated with the mouse. It illustrates the use of several InteractiveFrames
 * in the same scene.
 * <p>
 * Click on a frame visual hint to select a part of the lamp, and then move it with<br>
 * the mouse.
 * <p>
 * Press 'f' to toggle the drawing of the frames' visual hints.
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;
Lamp lamp;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setAxisIsDrawn(false);
  scene.setGridIsDrawn(false);
  scene.setHelpIsDrawn(false);
  scene.setFrameSelectionHintIsDrawn(true);
  lamp = new Lamp();
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  lights();
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
}