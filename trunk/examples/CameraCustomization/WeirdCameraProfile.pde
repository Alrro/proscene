public class WeirdCameraProfile extends CameraProfile {
  public WeirdCameraProfile(Scene scn, String name) {
   super(scn, name);
   // 1. Perform some keyboard configuration (warning: camera profiles override those of the scene):
   setShortcut('u', Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
   // CTRL + SHIFT + 'l' = move camera to the left
   setShortcut((Scene.Modifier.CTRL.ID | Scene.Modifier.SHIFT.ID), 'l', Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
   // 'S' (note the caps) = move the camera to show all the scene
   setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
   // 2. Describe how to control the camera:
   // SHIFT + mouse left button = zoom on region
   setCameraMouseBinding((Scene.Modifier.SHIFT.ID | Scene.Button.LEFT.ID), Scene.MouseAction.ZOOM_ON_REGION);
   // CTRL + mouse left button = rotate camera
   setCameraMouseBinding((Scene.Button.LEFT.ID | Scene.Modifier.CTRL.ID), Scene.MouseAction.ROTATE);
   // Right button = translate camera
   setCameraMouseBinding(Scene.Button.RIGHT.ID, Scene.MouseAction.TRANSLATE);
   // Middle button = zoom
   setCameraMouseBinding(Scene.Button.MIDDLE.ID, Scene.MouseAction.ZOOM);
   // 3. Describe how to control the interactive frame:
   // Left button = rotate interactive frame
   setFrameMouseBinding(Scene.Button.LEFT.ID, Scene.MouseAction.ROTATE);
   // Left button = translate interactive frame
   setFrameMouseBinding(Scene.Button.RIGHT.ID, Scene.MouseAction.TRANSLATE);
   // Right button + SHIFT = screen translate interactive frame
   setFrameMouseBinding((Scene.Button.RIGHT.ID | Scene.Modifier.SHIFT.ID), Scene.MouseAction.SCREEN_TRANSLATE);
   // 4. Configure some click actions:
   // double click + button left = align frame with world
   setClickBinding(Scene.Button.LEFT, 2, ClickAction.ALIGN_FRAME);
   // single click + middle button + SHIFT + ALT = interpolate to show all the scene
   setClickBinding((Scene.Modifier.SHIFT.ID | Scene.Modifier.ALT.ID), Scene.Button.MIDDLE, ClickAction.ZOOM_TO_FIT);
   // double click + button right = align camera with world
   setClickBinding(Scene.Button.RIGHT, 2, ClickAction.ALIGN_CAMERA);
  }
}