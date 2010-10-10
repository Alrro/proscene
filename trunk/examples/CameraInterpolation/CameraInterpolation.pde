/**
 * Camera Interpolation.
 * by Jean Pierre Charalambos.
 * 
 * This example (together with Frame Interpolation) illustrates the
 * KeyFrameInterpolator functionality.
 * 
 * KeyFrameInterpolator smoothly interpolate its attached Camera Frames over time
 * on a path defined by Frames. The interpolation can be started/stopped/reset,
 * played in loop, played at a different speed, etc...
 * 
 * In this example, a Camera path is defined by four InteractivedCameraFrames
 * which are attached to the first Camera path (the Camera holds five such paths).
 * The Frames can be moved with the mouse, making the path editable. The Camera
 * interpolating path is played with the shortcut '1'.
 * 
 * Press CONTROL + '1' to add (more) key frames to the Camera path.
 * 
 * Press ALT + '1' to delete the Camera path.
 * 
 * The Camera holds 5 KeyFrameInterpolators, binded to [1..5] keys. Pressing
 * CONTROL + [1..5] adds key frames to the specific path. Pressing ALT + [1..5]
 * deletes the specific path. Press 'r' to display all the key frame camera paths
 * (if any). The displayed paths are editable.
 * 
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;
ArrayList buttons;
int fSize = 16;
int h;

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

  buttons = new ArrayList(6);
  for (int i=0; i<5; ++i)
    buttons.add(null);
  
  Button2D button = new ClickButton(scene, new PVector(10,5), "edit camera path", fSize, 0);
  h = button.myHeight;
  buttons.set(0, button);
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  fill(204, 102, 0);
  box(20, 30, 50);

  updateButtons();
  displayButtons();
}

void updateButtons() {
  for (int i = 1; i < buttons.size(); i++) {
    // Check if CameraPathPlayer is still valid
    if ((buttons.get(i) != null) && (scene.camera().keyFrameInterpolator(i) == null ) )
      buttons.set(i, null);
    // Or add it if needed
    if ((scene.camera().keyFrameInterpolator(i) != null) && (buttons.get(i) == null)) {
      String label = "path ";
      label += ((Integer)(i)).toString();
      buttons.set(i, new ClickButton(scene, new PVector(10, + ( i ) * ( h + 7 )), label, fSize, i));
    }
  }
}

void displayButtons() {
  for (int i = 0; i < buttons.size(); i++) {
    Button2D button = (Button2D) buttons.get(i);
    if ( button != null )
      button.display();
  }
}