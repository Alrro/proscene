public class MainViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);
    // enable computation of the frustum planes equations (disabled by default)
    scene.enableFrustumEquationsUpdate();
    scene.setHelpIsDrawn(false);
    scene.setGridIsDrawn(false);
    // register the drawing method which was defined externally
    scene.addDrawHandler(parentPApplet, "mainDrawing");
  }  
  
  void keyPressed() {
    if ( key == 'v')
      scene.toggleCameraKind();
    if ( (key == 'u') && (scene.camera().kind() == Camera.Kind.STANDARD) )
      scene.camera().changeStandardOrthoFrustumSize(true);
    if ( (key == 'U') && (scene.camera().kind() == Camera.Kind.STANDARD) )
      scene.camera().changeStandardOrthoFrustumSize(false);
  }
}