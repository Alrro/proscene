public class WeirdCameraProfile extends CameraProfile {
  public WeirdCameraProfile(Scene scn, String name) {
    super(scn, name);
    // 1. Perform some keyboard configuration (warning: camera profiles override those of the scene):
    // 'u' = move camera up
    setShortcut('u', Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
    // CTRL + SHIFT + 'l' = move camera to the left
    setShortcut((Event.ALT | Event.SHIFT), 'l', Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
    // 'S' (note the caps) = move the camera to show all the scene
    setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
    // 2. Describe how to control the camera:
    // mouse left button = translate camera
    setCameraMouseBinding(LEFT, Scene.MouseAction.TRANSLATE);
    // SHIFT + mouse left button = rotate camera
    setCameraMouseBinding(Event.SHIFT, LEFT, Scene.MouseAction.ROTATE);   
    // Right button = zoom on region
    setCameraMouseBinding(RIGHT, Scene.MouseAction.ZOOM_ON_REGION);
    // 3. Describe how to control the interactive frame:
    // Left button = rotate interactive frame
    setFrameMouseBinding(LEFT, Scene.MouseAction.ROTATE);
    // Right button = translate interactive frame
    setFrameMouseBinding(RIGHT, Scene.MouseAction.TRANSLATE);
    // Right button + SHIFT = screen translate interactive frame
    setFrameMouseBinding(Event.SHIFT, RIGHT, Scene.MouseAction.SCREEN_TRANSLATE);
    // 4. Configure some click actions:
    // double click + button left = align frame with world
    setClickBinding(LEFT, 2, Scene.ClickAction.ALIGN_FRAME);
    // single click + middle button + SHIFT + ALT = interpolate to show all the scene
    setClickBinding((Event.SHIFT | Event.ALT), CENTER, 1, Scene.ClickAction.ZOOM_TO_FIT);
    // double click + middle button = align camera with world
    setClickBinding(CENTER, 2, Scene.ClickAction.ALIGN_CAMERA);
  }
}