/**
 * Camera Customization.
 * by Jean Pierre Charalambos.
 * 
 * This example shows the different aspects of proscene that
 * can be customized and how to do it.
 * 
 * Read the commented lines of the sketch code for details.
 */

import remixlab.proscene.*;
import remixlab.proscene.Scene.*;

Scene scene;
WeirdCameraProfile wProfile;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  // A Scene has a single InteractiveFrame (null by default). We set it
  // here.
  scene.setInteractiveFrame(new InteractiveFrame(scene));
  scene.interactiveFrame().translate(new PVector(30, 30, 0));

  // 1. Perform some keyboard configuration:
  // Note that there are some defaults set (soon to be  documented ;)
  // change interaction between camera an interactive frame:
  scene.setShortcut('f', Scene.GlobalKeyboardAction.FOCUS_INTERACTIVE_FRAME);
  // change the camera projection
  scene.setShortcut('z', Scene.GlobalKeyboardAction.CAMERA_TYPE);

  // 2. Configure some click actions:
  // double click + button left = align frame with world
  scene.setClickShortcut(Button.LEFT, 2, ClickAction.ALIGN_FRAME);
  // single click + middle button + SHIFT = interpolate to show all the scene.
  scene.setClickShortcut(Button.MIDDLE, Scene.Modifier.SHIFT,	ClickAction.ZOOM_TO_FIT);

  // 3. Customized camera profile:
  wProfile = new WeirdCameraProfile(scene, "MY_PROFILE");
  scene.registerCameraProfile(wProfile);
  // Unregister default camera profiles (i.e., leave only MY_PROFILE):
  scene.unregisterCameraProfile("ARCBALL");
  scene.unregisterCameraProfile("FIRST_PERSON");
}

void draw() {
  // Proscene sets the background to black by default. If you need to
  // change
  // it, don't call background() directly but use scene.background()
  // instead.
  fill(204, 102, 0);
  box(20, 20, 40);
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(scene.interactiveFrame().matrix()) is handy but
  // inefficient
  scene.interactiveFrame().applyTransformation(); // optimum
  // Draw an axis using the Scene static function
  scene.drawAxis(20);
  // Draw a second box attached to the interactive frame
  if (scene.interactiveFrame().grabsMouse()) {
    fill(255, 0, 0);
    box(12, 17, 22);
  } 
  else if (scene.interactiveFrameIsDrawn()) {
    fill(0, 255, 255);
    box(12, 17, 22);
  } 
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  }
  popMatrix();
}