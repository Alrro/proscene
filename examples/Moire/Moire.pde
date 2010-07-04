/**
 * <b>Moire</b> by Jean Pierre Charalambos.
 *
 * This example illustrates the camera and world coordinate systems relationship.<br>
 * Three sets of lines (red, green, blue) are drawn. They have common end points,<br>
 * located on a circle in the XY plane. However, they have different starting points<br>
 * defined as follows: 1. The red lines start at the camera position and will hence<br>
 * always be aligned with the viewing direction, making them invisible, unless you<br>
 * change the camera projection to ORTHOGRAPHIC (press 'e' to do it); 2. The green<br>
 * lines starting points are determined from the camera coordinate system and will<br>
 * hence always be fixed on the screen; and, 3. The blue lines starting points are<br>
 * defined in the world coordinate system, and will hence move in camera motion.
 * <p>
 * A nice Moire pattern can be obtained when rotating the camera.
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setGridIsDrawn(false);
  scene.setRadius(150);
  scene.showAll();
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  final float nbLines = 50.0f;
  beginShape(LINES);
  for (float i=0; i<nbLines; ++i) {
    float angle = 2.0f*PI*i/nbLines;
    
    stroke(204, 51, 51);
    // These lines will never be seen as they are always aligned with the viewing direction.
    vertex(scene.camera().position().x, scene.camera().position().y, scene.camera().position().z);
    vertex(100*cos(angle), 100*sin(angle), 0);
    
    stroke(55, 204, 55);
    // World Coordinates are infered from the camera, and seem to be immobile in the screen.
    PVector tmp = scene.camera().worldCoordinatesOf(new PVector(30*cos(angle), 30*sin(angle), -200));
    vertex(tmp.x, tmp.y, tmp.z);
    vertex(100*cos(angle), 100*sin(angle), 0);
    
    stroke(55, 55, 204);
    // These lines are defined in the world coordinate system and will move with the camera.
    vertex(150*cos(angle), 150*sin(angle), -100);
    vertex(100*cos(angle), 100*sin(angle), 0);
  }
  endShape();
}