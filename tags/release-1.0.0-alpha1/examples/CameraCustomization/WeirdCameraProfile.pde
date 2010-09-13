public class WeirdCameraProfile extends CameraProfile {
  public WeirdCameraProfile(Scene scn, String name) {
    super(scn, name);
    // 1. Perform some keyboard configuration (warning: camera profiles override those of the scene):
    setShortcut('u', Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
    setShortcut('l', Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
    // note the case:
    setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
    // 2. Describe how to control the camera:
    setCameraShortcut(Scene.Button.LEFT, Scene.MouseAction.ZOOM_ON_REGION);
    setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.CONTROL, Scene.MouseAction.ROTATE);
    setCameraShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);
    setCameraShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
    // 3. Describe how to control the interactive frame:
    setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
    setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);
    setIFrameShortcut(Scene.Button.RIGHT, Scene.Modifier.SHIFT, Scene.MouseAction.SCREEN_TRANSLATE);
  }
}