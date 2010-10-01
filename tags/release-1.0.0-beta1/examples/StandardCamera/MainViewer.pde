public class MainViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);
    // 'v' toggles camera kind:
    scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
    // enable computation of the frustum planes equations (disabled by default)
    scene.enableFrustumEquationsUpdate();
    scene.setGridIsDrawn(false);
    // register the drawing method which was defined externally
    scene.addDrawHandler(parentPApplet, "mainDrawing");
  }  
  
  void keyPressed() {
    if ( (key == 'u') && (scene.camera().kind() == Camera.Kind.STANDARD) )
      scene.camera().changeStandardOrthoFrustumSize(true);
    if ( (key == 'U') && (scene.camera().kind() == Camera.Kind.STANDARD) )
      scene.camera().changeStandardOrthoFrustumSize(false);
  }
}