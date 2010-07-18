/**
 * <b>Camera Interpolation</b> by Jean Pierre Charalambos.
 * <p>
 * This example (together with <b>Frame Interpolation</b>) illustrates the
 * KeyFrameInterpolator functionality.
 * <p>
 * KeyFrameInterpolator smoothly interpolate its attached Camera Frames over time<br>
 * on a path defined by Frames. The interpolation can be started/stopped/reset,<br>
 * played in loop, played at a different speed, etc...
 * <p>
 * In this example, a Camera path is defined by four InteractivedCameraFrames<br>
 * which are attached to the first Camera path (the Camera holds five such paths).<br>
 * The Frames can be moved with the mouse, making the path editable. The Camera<br>
 * interpolating path is played with the shortcut <b>'1'</b>.
 * <p>
 * Press <b>'j'</b> to add (more) key frames to the Camera path.
 * <p>
 * Press <b>'J'</b> to delete the Camera path.
 * <p>
 * The Camera holds 5 KeyFrameInterpolators, binded to <b>[j..n]/[J..N]/[1..5]</b> keys:<br>
 * set/reset/play the key frame interpolators, respectively. Press <b>'r'</b> to display<br>
 * all the key frame camera paths (if any).
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
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