/**
 * <b>Basic Use</b> by Jean Pierre Charalambos.
 * <p>
 * This example illustrates a direct approach to using proscene by Scene proper
 * instantiation.
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;

void setup() {
  size(640, 360, P3D);
  //Scene instantiation
  scene = new Scene(this);  
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  fill(204, 102, 0);
  box(20, 30, 50);
}