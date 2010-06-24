/**
 * Camera Interpolation.
 * by Jean Pierre Charalambos.
 * 
 * This example (together with Frame Interpolation) illustrates the
 * KeyFrameInterpolator functionality.
 *
 * KeyFrameInterpolator smoothly interpolate its attached Camera Frames over
 * time on a path defined by Frames. The interpolation can be
 * started/stopped/reset, played in loop, played at a different speed, etc...
 *
 * In this example, a Camera path is defined by four InteractivedCameraFrames
 * which are attached to the first Camera path (the Camera holds five such paths).
 * The Frames can be moved with the mouse making the path editable. The Camera
 * interpolating path is played with the shortcut '1'.
 *
 * Press 'j' to add (more) key frames to the Camera path.
 *
 * Press 'J' to delete the Camera path.
 *
 * The Camera holds 5 KeyFrameInterpolators, binded to [j..n]/[J..N]/[1..5] keys:
 * set/reset/play the key frame interpolators, respectively. Press 'r' to display
 * all the key frame camera paths (if any).
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  
  //create a camera path and add some key frames:
  //key frames can be added at runtime with keys [j..n]
  scene.camera().setPosition(new PVector(80,0,0));
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);
  
  scene.camera().setPosition(new PVector(30,30,-80));
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);
  
  scene.camera().setPosition(new PVector(-30,-30,-80));
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);
  
  scene.camera().setPosition(new PVector(-80,0,0));
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.camera().addKeyFrameToPath(1);
  
  //re-position the camera:
  scene.camera().setPosition(new PVector(0,0,1));
  scene.camera().lookAt( scene.camera().sceneCenter() );
  scene.showAll();
  
  //drawing of camera paths are toggled with key 'r'. 
  scene.setCameraPathsAreDrawn(true);
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  fill(204, 102, 0);
  box(20, 30, 50);
}