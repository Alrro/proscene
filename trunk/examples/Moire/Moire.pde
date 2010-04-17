/**
 * Moire. 
 * by Jean Pierre Charalambos.
 *
 * This example illustrates the camera and world coordinate systems relationship.
 * Three sets of lines (red, green, blue) are drawn. They have common end points,
 * located on a circle in the XY plane. However, they have different starting points
 * defined as follows: 1. The red lines start at the camera position and will hence
 * always be aligned with the viewing direction, making them invisible, unless you
 * change the camera projection to ORTHOGRAPHIC (press 'e' to do it); 2. The green
 * lines starting points are determined from the camera coordinate system and will
 * hence always be fixed on the screen; and, 3. The blue lines starting points are
 * defined in the world coordinate system, and will hence move in camera motion.
 *
 * A nice Moire pattern can be obtained when rotating the camera.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

Scene scene;

void setup() {
  size(640, 360, OPENGL);
  scene = new Scene(this);
  scene.setAxisIsDrawn(true);
  scene.setRadius(1.5f);
  scene.showAll();
}

// Your actual scene drawing should be enclosed between the
// Scene.beginDraw() and Scene.endDraw() pair.
void draw() {
  // Should always be defined before Scene.beginDraw()
  background(0);
  scene.beginDraw();

  final float nbLines = 50.0f;

  beginShape(LINES);
  for (float i=0; i<nbLines; ++i) {
    float angle = 2.0f*PI*i/nbLines;

    stroke(204, 51, 51);
    // These lines will never be seen as they are always aligned with the viewing direction.
    // They can only be seen if you change the camera projection to ORTHOGRAPHIC.
    vertex(scene.camera().position().x, scene.camera().position().y, scene.camera().position().z);
    vertex(cos(angle), sin(angle), 0.0f);

    stroke(55, 204, 55);
    // World Coordinates are inferred from the camera, and seem to be motionless in the screen.
    PVector tmp = scene.camera().worldCoordinatesOf(new PVector(.3f*cos(angle), .3f*sin(angle), -2.0f));
    vertex(tmp.x, tmp.y, tmp.z);
    vertex(cos(angle), sin(angle), 0.0f);

    stroke(55, 55, 204);
    // These lines are defined in the world coordinate system and will move with the camera.
    vertex(1.5f*cos(angle), 1.5f*sin(angle), -1.0f);
    vertex(cos(angle), sin(angle), 0.0f);
  }
  endShape();
  
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power
// keyPressed() should always call Scene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
